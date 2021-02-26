package com.adt.kotlin.kats.control.instances.monad

import com.adt.kotlin.kats.control.instances.applicative.KleisliApplicative
import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.kleisli.Kleisli.KleisliProxy
import com.adt.kotlin.kats.control.data.kleisli.KleisliF.kleisli
import com.adt.kotlin.kats.control.data.kleisli.KleisliOf
import com.adt.kotlin.kats.control.data.kleisli.narrow
import com.adt.kotlin.kats.data.immutable.either.Either

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface KleisliMonad<F, A> : Monad<Kind1<Kind1<KleisliProxy, F>, A>>, KleisliApplicative<F, A> {

    fun monad(): Monad<F>

    override fun applicative(): Applicative<F> = monad()

    /**
     * Inject a value into the monadic type.
     */
    override fun <B> inject(a: B): Kleisli<F, A, B> = Kleisli { _: A -> monad().inject(a) }

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <B, C> bind(v: KleisliOf<F, A, B>, f: (B) -> KleisliOf<F, A, C>): Kleisli<F, A, C> {
        val vKleisli: Kleisli<F, A, B> = v.narrow()
        val g: (B) -> Kleisli<F, A, C> = { b -> f(b).narrow()}
        return Kleisli { a: A -> monad().bind(vKleisli.run(a)) { b: B -> g(b).run(a) } }
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> KleisliOf<F, A, Either<B, C>>): Kleisli<F, A, C> {
        fun recTailRecM(mf: Monad<F>, b: B, f: (B) -> Kleisli<F, A, Either<B, C>>): Kleisli<F, A, C> {
            return kleisli{a: A -> mf.tailRecM(b){bb: B -> f(bb)(a)}}
        }   // recTailRecM

        val g: (B) -> Kleisli<F, A, Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(monad(), b, g)
    }   // tailRecM

}   // KleisliMonad
