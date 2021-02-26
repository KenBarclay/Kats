package com.adt.kotlin.kats.data.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.tri.Try
import com.adt.kotlin.kats.data.immutable.tri.Try.TryProxy
import com.adt.kotlin.kats.data.immutable.tri.Try.Failure
import com.adt.kotlin.kats.data.immutable.tri.Try.Success
import com.adt.kotlin.kats.data.immutable.tri.TryF.success
import com.adt.kotlin.kats.data.immutable.tri.TryOf
import com.adt.kotlin.kats.data.immutable.tri.narrow

import com.adt.kotlin.kats.data.instances.applicative.TryApplicative
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadForC
import com.adt.kotlin.kats.hkfp.typeclass.MonadSyntax


interface TryMonad : Monad<TryProxy>, TryApplicative {

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): Try<A> = Success(a)

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <A, B> bind(v: TryOf<A>, f: (A) -> Kind1<TryProxy, B>): Try<B> {
        val vTry: Try<A> = v.narrow()
        val g: (A) -> Try<B> = {a: A -> f(a).narrow()}
        return vTry.bind(g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> TryOf<Either<B, C>>): Try<C> {
        tailrec
        fun recTailRecM(b: B, f: (B) -> Try<Either<B, C>>): Try<C> {
            val tri: Try<Either<B, C>> = f(b)
            return when (tri) {
                is Failure -> Failure(tri.throwable)
                is Success -> {
                    val either: Either<B, C> = tri.value
                    when (either) {
                        is Left -> recTailRecM(either.value, f)
                        is Right -> success(either.value)
                    }
                }
            }
        }   // recTailRecM

        val g: (B) -> Try<Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(b, g)
    }   // tailRecM



    /**
     * Entry point for monad bindings which enables for comprehension.
     */
    override val forC: MonadForC<TryProxy>
        get() = TryForCMonad

}   // TryMonad



internal object TryForCMonad : MonadForC<TryProxy> {

    override val mf: Monad<TryProxy> = Try.monad()
    override fun <A> monad(block: suspend MonadSyntax<TryProxy>.() -> A): Try<A> =
            super.monad(block).narrow()

}   // TryForCMonad
