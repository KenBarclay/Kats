package com.adt.kotlin.kats.control.instances.functor

import com.adt.kotlin.kats.control.data.readert.ReaderT
import com.adt.kotlin.kats.control.data.readert.ReaderT.ReaderTProxy
import com.adt.kotlin.kats.control.data.readert.ReaderTF.readert
import com.adt.kotlin.kats.control.data.readert.ReaderTOf
import com.adt.kotlin.kats.control.data.readert.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor


interface ReaderTFunctor<F, A> : Functor<Kind1<Kind1<ReaderTProxy, F>, A>> {

    fun functor(): Functor<F>

    /**
     * Apply the function to the content(s) of the ReaderT context.
     *
     * Examples:
     *   let functor = ReaderT.functor(Identity.functor())
     *
     *   functor.fmap(strToInt){n: Int -> isEven(n)}.narrow().execute("") == true
     *   functor.fmap(strToInt){n: Int -> isEven(n)}.narrow().execute("ken") == false
     *   functor.fmap(strTo3){n: Int -> isEven(n)}.narrow().execute("anything") == false
     */
    override fun <B, C> fmap(v: ReaderTOf<F, A, B>, f: (B) -> C): ReaderT<F, A, C> {
        val vReaderT: ReaderT<F, A, B> = v.narrow()
        return readert { a: A -> functor().fmap(vReaderT.run(a), f) }
    }   // fmap



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the ReaderT context.
     */
    override fun <B, C> lift(f: (B) -> C): (ReaderTOf<F, A, B>) -> ReaderT<F, A, C> =
            {rfab: ReaderTOf<F, A, B> ->
                fmap(rfab, f)
            }

}   // ReaderTFunctor
