package com.adt.kotlin.kats.control.instances.alternative

import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.kleisli.Kleisli.KleisliProxy
import com.adt.kotlin.kats.control.data.kleisli.KleisliF.kleisli
import com.adt.kotlin.kats.control.data.kleisli.KleisliOf
import com.adt.kotlin.kats.control.data.kleisli.narrow
import com.adt.kotlin.kats.control.instances.applicative.KleisliApplicative

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Alternative
import com.adt.kotlin.kats.hkfp.typeclass.Applicative


interface KleisliAlternative<F, A> : Alternative<Kind1<Kind1<KleisliProxy, F>, A>>, KleisliApplicative<F, A> {

    fun alternative(): Alternative<F>

    override fun applicative(): Applicative<F> = alternative()

    /**
     * The identity of combine.
     */
    override fun <B> empty(): Kleisli<F, A, B> = kleisli{_: A -> alternative().empty()}

    /**
     * An associative binary operation.
     */
    override fun <B> combine(a: KleisliOf<F, A, B>, b: KleisliOf<F, A, B>): Kleisli<F, A, B> {
        val aKleisli: Kleisli<F, A, B> = a.narrow()
        val bKleisli: Kleisli<F, A, B> = b.narrow()
        return kleisli{aa: A ->
            val afb: Kind1<F, B> = aKleisli(aa)
            val bfb: Kind1<F, B> = bKleisli(aa)
            alternative().combine(afb, bfb)
        }
    }   // combine

}   // KleisliAlternative
