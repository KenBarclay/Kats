package com.adt.kotlin.kats.control.instances.monadplus

import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.kleisli.KleisliF.kleisli
import com.adt.kotlin.kats.control.data.kleisli.KleisliOf
import com.adt.kotlin.kats.control.data.kleisli.narrow
import com.adt.kotlin.kats.control.instances.alternative.KleisliAlternative
import com.adt.kotlin.kats.control.instances.monad.KleisliMonad

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Alternative
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadPlus



interface KleisliMonadPlus<F, A> : MonadPlus<Kind1<Kind1<Kleisli.KleisliProxy, F>, A>>, KleisliMonad<F, A>, KleisliAlternative<F, A> {

    fun monadPlus(): MonadPlus<F>

    override fun alternative(): Alternative<F> = monadPlus()
    override fun applicative(): Applicative<F> = monadPlus()
    override fun monad(): Monad<F> = monadPlus()

    /**
     * An empty result.
     */
    override fun <B> mzero(): Kleisli<F, A, B> = kleisli{_: A -> monadPlus().mzero()}

    /**
     * Combine two results into one.
     */
    override fun <B> mplus(fa1: KleisliOf<F, A, B>, fa2: KleisliOf<F, A, B>): Kleisli<F, A, B> {
        val aKleisli: Kleisli<F, A, B> = fa1.narrow()
        val bKleisli: Kleisli<F, A, B> = fa2.narrow()
        return kleisli{aa: A ->
            val afb: Kind1<F, B> = aKleisli(aa)
            val bfb: Kind1<F, B> = bKleisli(aa)
            monadPlus().combine(afb, bfb)
        }
    }   // mplus

}   // KleisliMonadPlus
