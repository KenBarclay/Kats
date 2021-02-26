package com.adt.kotlin.kats.control.instances.foldable

import com.adt.kotlin.kats.control.data.writert.WriterT
import com.adt.kotlin.kats.control.data.writert.WriterT.WriterTProxy
import com.adt.kotlin.kats.control.data.writert.WriterTOf
import com.adt.kotlin.kats.control.data.writert.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface WriterTFoldable<F, W> : Foldable<Kind1<Kind1<WriterTProxy, F>, W>> {

    fun foldable(): Foldable<F>

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *   let foldable: = WriterT.foldable(Identity.foldable())
     *   let strInt = writer("Ken", 7)
     *
     *   foldable.foldLeft(strInt, 10){m: Int -> {n: Int -> m + n}} == 17
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <A, B> foldLeft(v: WriterTOf<F, W, A>, e: B, f: (B) -> (A) -> B): B {
        val vWriter: WriterT<F, W, A> = v.narrow()
        return foldable().foldLeft(vWriter.run, e){b: B -> {pair: Pair<W, A> -> f(b)(pair.second)}}
    }   // foldLeft

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *   let foldable: = WriterT.foldable(Identity.foldable())
     *   let strInt = writer("Ken", 7)
     *
     *   foldable.foldRight(strInt, 10){m: Int -> {n: Int -> m + n}} == 17
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <A, B> foldRight(v: WriterTOf<F, W, A>, e: B, f: (A) -> (B) -> B): B {
        val vWriter: WriterT<F, W, A> = v.narrow()
        return foldable().foldRight(vWriter.run, e){pair: Pair<W, A> -> {b: B -> f(pair.second)(b)}}
    }   // foldRight

}   // WriterTFoldable
