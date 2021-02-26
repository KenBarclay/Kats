package com.adt.kotlin.kats.control.instances.applicative

import com.adt.kotlin.kats.control.data.writert.WriterT
import com.adt.kotlin.kats.control.data.writert.WriterT.WriterTProxy
import com.adt.kotlin.kats.control.data.writert.WriterTF.writert
import com.adt.kotlin.kats.control.data.writert.WriterTOf
import com.adt.kotlin.kats.control.data.writert.narrow

import com.adt.kotlin.kats.control.instances.functor.WriterTFunctor
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Monoid



interface WriterTApplicative<F, W> : Applicative<Kind1<Kind1<WriterTProxy, F>, W>>, WriterTFunctor<F, W> {

    fun applicative(): Applicative<F>
    override fun functor(): Functor<F> = applicative()

    val mw: Monoid<W>

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): WriterT<F, W, A> = writert(applicative().pure(Pair(mw.empty, a)))

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     *
     * Examples:
     *   let applicative = WriterT.applicative(Identity.applicative(), stringMonoid)
     *   let strInt = writer("Ken", 7)
     *
     *   applicative.ap(strInt, Writer("neth"){n: Int -> isEven(n)}) == writer("Kenneth", false)
     */
    override fun <A, B> ap(v: WriterTOf<F, W, A>, f: WriterTOf<F, W, (A) -> B>): WriterT<F, W, B> {
        val vWriter: WriterT<F, W, A> = v.narrow()
        val fWriter: WriterT<F, W, (A) -> B> = f.narrow()
        return WriterT(applicative().fmap2(vWriter.run, fWriter.run){vPair: Pair<W, A> ->
            {fPair: Pair<W, (A) -> B> ->
                Pair(mw.run{combine(vPair.first, fPair.first)}, fPair.second(vPair.second))
            }
        })
    }   // ap

}   // WriterTApplicative
