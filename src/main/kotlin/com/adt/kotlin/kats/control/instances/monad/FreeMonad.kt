package com.adt.kotlin.kats.control.instances.monad

import com.adt.kotlin.kats.control.data.free.Free
import com.adt.kotlin.kats.control.data.free.Free.Pure
import com.adt.kotlin.kats.control.data.free.Free.Bind
import com.adt.kotlin.kats.control.data.free.FreeOf
import com.adt.kotlin.kats.control.data.free.narrow
import com.adt.kotlin.kats.control.instances.applicative.FreeApplicative
import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface FreeMonad<F> : Monad<Kind1<Free.FreeProxy, F>>, FreeApplicative<F> {

    //fun monad(): Monad<F>
    //override fun applicative(): Applicative<F> = monad()

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): Free<F, A> = Pure(a)

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <A, B> bind(v: FreeOf<F, A>, f: (A) -> FreeOf<F, B>): Free<F, B> {
        //val mf: Monad<F> = monad()
        val ff: Functor<F> = functor()
        val vFree: Free<F, A> = v.narrow()
        val fFree: (A) -> Free<F, B> = {a: A -> f(a).narrow()}
        return when (vFree) {
            is Pure -> fFree(vFree.a)
            is Bind -> {
                val ma: Kind1<F, Free<F, A>> = vFree.free
                val bindC: ((A) -> FreeOf<F, B>) -> (FreeOf<F, A>) -> Free<F, B> =
                        {g -> {fa -> bind(fa, g)}}
                val bindf: (Free<F, A>) -> Free<F, B> = bindC(fFree)
                Bind(ff.fmap(ma, bindf))
                //Bind(mf.fmap(ma, bindf))
            }
        }
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <A, B> tailRecM(a: A, f: (A) -> FreeOf<F, Either<A, B>>): Free<F, B> {
        fun recTailRecM(a: A, f: (A) -> Free<F, Either<A, B>>): Free<F, B> {
            val fab: Free<F, Either<A, B>> = f(a)
            return bind(fab){either: Either<A, B> ->
                when (either) {
                    is Left -> recTailRecM(either.value, f)
                    is Right -> Pure(either.value)
                }
            }
        }   // recTailRecM

        val g: (A) -> Free<F, Either<A, B>> = {aa: A -> f(aa).narrow()}
        return recTailRecM(a, g)
    }

}   // FreeMonad
