package com.adt.kotlin.kats.data.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.function.Function
import com.adt.kotlin.kats.data.immutable.function.FunctionOf
import com.adt.kotlin.kats.data.immutable.function.narrow
import com.adt.kotlin.kats.data.instances.applicative.FunctionApplicative

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface FunctionMonad<A> : Monad<Kind1<Function.FunctionProxy, A>>, FunctionApplicative<A> {

    /**
     * Inject a value into the monadic type.
     */
    override fun <B> inject(a: B): Function<A, B> = Function{_: A -> a}

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <B, C> bind(v: FunctionOf<A, B>, f: (B) -> FunctionOf<A, C>): Function<A, C> {
        val vFunction: Function<A, B> = v.narrow()
        val g: (A) -> C = {a: A ->
            val b: B = vFunction(a)
            val fac: Function<A, C> = f(b).narrow()
            fac(a)
        }
        return Function(g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> FunctionOf<A, Either<B, C>>): Function<A, C> {
        tailrec
        fun step(b: B, a: A, f: (B) -> FunctionOf<A, Either<B, C>>): C {
            val fb: Function<A, Either<B, C>> = f(b).narrow()
            val fba: Either<B, C> = fb(a)
            return when (fba) {
                is Left -> step(fba.value, a, f)
                is Right -> fba.value
            }
        }   // step

        return Function{a: A -> step(b, a, f)}
    }   // tailRecM

}   // FunctionMonad
