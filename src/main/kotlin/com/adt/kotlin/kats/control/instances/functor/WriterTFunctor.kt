package com.adt.kotlin.kats.control.instances.functor

import com.adt.kotlin.kats.control.data.writert.narrow
import com.adt.kotlin.kats.control.data.writert.WriterT
import com.adt.kotlin.kats.control.data.writert.WriterT.WriterTProxy
import com.adt.kotlin.kats.control.data.writert.WriterTOf

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface WriterTFunctor<F, W> : Functor<Kind1<Kind1<WriterTProxy, F>, W>> {

    fun functor(): Functor<F>

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <A, B> fmap(v: WriterTOf<F, W, A>, f: (A) -> B): WriterT<F, W, B> {
        val vWriterT: WriterT<F, W, A> = v.narrow()
        return vWriterT.map(functor(), f)
    }   // fmap



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Writer context.
     */
    override fun <A, B> lift(f: (A) -> B): (WriterTOf<F, W, A>) -> WriterT<F, W, B> =
            {wwa: WriterTOf<F, W, A> ->
                fmap(wwa, f)
            }

}   // WriterTFunctor
