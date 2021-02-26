package com.adt.kotlin.kats.control.instances.applicative

import com.adt.kotlin.kats.control.data.readert.ReaderT
import com.adt.kotlin.kats.control.data.readert.ReaderT.ReaderTProxy
import com.adt.kotlin.kats.control.data.readert.ReaderTF.readert
import com.adt.kotlin.kats.control.data.readert.ReaderTOf
import com.adt.kotlin.kats.control.data.readert.narrow
import com.adt.kotlin.kats.control.instances.functor.ReaderTFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface ReaderTApplicative<F, A> : Applicative<Kind1<Kind1<ReaderTProxy, F>, A>>, ReaderTFunctor<F, A> {

    fun applicative(): Applicative<F>

    override fun functor(): Functor<F> = applicative()

    /**
     * Take a value of any type and returns a context enclosing the value.
     *
     * Examples:
     *   let applicative = ReaderT.applicative(Identity.applicative())
     *
     *   applicative.pure(12).narrow().execute("anything") == 12
     */
    override fun <B> pure(a: B): ReaderT<F, A, B> = readert {_: A -> applicative().pure(a)}

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     *
     * Examples:
     *   let strToInt = reader{str: String -> str.length}
     *   let strTo3 = reader(3)
     *
     *   strToInt.ap(reader{str: String -> {n: Int -> n * n}}).execute("") == 0
     *   strToInt.ap(reader{str: String -> {n: Int -> n * n}}).execute("kenneth")) == 49
     *   strTo3.ap(reader{str: String -> {n: Int -> n * n}}).execute("anything") == 3
     */
    override fun <B, C> ap(v: ReaderTOf<F, A, B>, f: ReaderTOf<F, A, (B) -> C>): ReaderT<F, A, C> {
        val vReaderT: ReaderT<F, A, B> = v.narrow()
        val fReaderT: ReaderT<F, A, (B) -> C> = f.narrow()
        return readert { a: A -> applicative().ap(vReaderT.run(a), fReaderT.run(a)) }
    }   // ap

}   // fReaderTApplicative
