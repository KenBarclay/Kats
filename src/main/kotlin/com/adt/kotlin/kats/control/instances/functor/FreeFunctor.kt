package com.adt.kotlin.kats.control.instances.functor

import com.adt.kotlin.kats.control.data.free.Free
import com.adt.kotlin.kats.control.data.free.Free.Pure
import com.adt.kotlin.kats.control.data.free.Free.Bind
import com.adt.kotlin.kats.control.data.free.Free.FreeProxy
import com.adt.kotlin.kats.control.data.free.FreeOf
import com.adt.kotlin.kats.control.data.free.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface FreeFunctor<F> : Functor<Kind1<FreeProxy, F>> {

    fun functor(): Functor<F>

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <A, B> fmap(v: FreeOf<F, A>, f: (A) -> B): Free<F, B> {
        val ff: Functor<F> = functor()
        fun go(fa: Free<F, A>): Free<F, B> =
                when (fa) {
                    is Pure -> Pure(f(fa.a))
                    is Bind -> Bind(ff.fmap(fa.free, ::go))
                }

        val vFree: Free<F, A> = v.narrow()
        return go(vFree)
    }   // fmap



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Free context.
     */
    override fun <A, B> lift(f: (A) -> B): (FreeOf<F, A>) -> Free<F, B> =
            {ffa: FreeOf<F, A> ->
                fmap(ffa, f)
            }

}   // FreeFunctor
