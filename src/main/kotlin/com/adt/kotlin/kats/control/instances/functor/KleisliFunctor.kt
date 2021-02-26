package com.adt.kotlin.kats.control.instances.functor

import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.kleisli.Kleisli.KleisliProxy
import com.adt.kotlin.kats.control.data.kleisli.KleisliOf
import com.adt.kotlin.kats.control.data.kleisli.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface KleisliFunctor<F, A> : Functor<Kind1<Kind1<KleisliProxy, F>, A>> {

    fun functor(): Functor<F>

    /**
     * Apply the function to the content(s) of the Kleisli context.
     */
    override fun <B, C> fmap(v: KleisliOf<F, A, B>, f: (B) -> C): Kleisli<F, A, C> {
        val vKleisli: Kleisli<F, A, B> = v.narrow()
        return Kleisli { a: A -> functor().fmap(vKleisli.run(a), f) }
    }   // fmap



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Kleisli context.
     */
    override fun <B, C> lift(f: (B) -> C): (KleisliOf<F, A, B>) -> Kleisli<F, A, C> =
            {kafb: KleisliOf<F, A, B> ->
                fmap(kafb, f)
            }

}   // KleisliFunctor
