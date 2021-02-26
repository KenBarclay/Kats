package com.adt.kotlin.kats.control.instances.traversable

import com.adt.kotlin.kats.control.data.writert.WriterT
import com.adt.kotlin.kats.control.data.writert.WriterT.WriterTProxy
import com.adt.kotlin.kats.control.data.writert.WriterTF.writert
import com.adt.kotlin.kats.control.data.writert.WriterTOf
import com.adt.kotlin.kats.control.data.writert.narrow

import com.adt.kotlin.kats.control.instances.foldable.WriterTFoldable
import com.adt.kotlin.kats.control.instances.functor.WriterTFunctor
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface WriterTTraversable<F, W> : Traversable<Kind1<Kind1<WriterTProxy, F>, W>>, WriterTFoldable<F, W>, WriterTFunctor<F, W> {

    fun traversable(): Traversable<F>
    override fun foldable(): Foldable<F> = traversable()

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     *
     * Examples:
     *   let traversable = WriterT.traversable(Identity.traversable())
     *   let strInt = writer("Ken", 7)
     *
     *   traversable.traverse(strInt, Option.applicative()){n: Int -> some(isEven(n))} == some(writer("Ken", false))
     */
    override fun <G, A, B> traverse(v: WriterTOf<F, W, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, WriterT<F, W, B>> {
        val vWriter: WriterT<F, W, A> = v.narrow()
        fun g(pair: Pair<W, A>): Kind1<G, Pair<W, B>> = ag.fmap(f(pair.second)){b: B -> Pair(pair.first, b)}
        val gfwb: Kind1<G, Kind1<F, Pair<W, B>>> = traversable().traverse(vWriter.run, ag, ::g)
        return ag.fmap(gfwb){fwb -> writert(fwb)}
    }   // traverse

}   // WriterTTraversable
