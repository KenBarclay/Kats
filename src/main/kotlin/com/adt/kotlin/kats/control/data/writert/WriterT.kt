package com.adt.kotlin.kats.control.data.writert

/**
 * WriterT is a writer monad parameterized by the type W of output to accumulate,
 *   and the result type A.
 *
 * @author	                    Ken Barclay
 * @since                       November 2018
 *
 * @param W                     the output type
 * @param A                     the result type
 * @param run                   the output/result pair
 */

import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.writert.WriterT.WriterTProxy
import com.adt.kotlin.kats.control.instances.applicative.WriterTApplicative
import com.adt.kotlin.kats.control.instances.foldable.WriterTFoldable
import com.adt.kotlin.kats.control.instances.functor.WriterTFunctor
import com.adt.kotlin.kats.control.instances.monad.WriterTMonad
import com.adt.kotlin.kats.control.instances.traversable.WriterTTraversable

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*



typealias WriterTOf<F, W, A> = Kind1<Kind1<Kind1<WriterTProxy, F>, W>, A>

open class WriterT<F, W, A> internal constructor(val run: Kind1<F, Pair<W, A>>) : Kind1<Kind1<Kind1<WriterTProxy, F>, W>, A> {

    class WriterTProxy                     // proxy for the WriterT context



    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param other             the other object
     * @return                  true if "equal", false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val otherWriterT: WriterT<F, W, A> = other as WriterT<F, W, A>
            (this.run == otherWriterT.run)
        }
    }   // equals

    fun <B> map(ff: Functor<F>, f: (A) -> B): WriterT<F, W, B> =
            WriterT(ff.fmap(run){pair: Pair<W, A> -> Pair(pair.first, f(pair.second))})

    fun tell(mf: Monad<F>, sw: Semigroup<W>, w: W): WriterT<F, W, A> =
            mapWritten(mf){ww: W ->
                sw.run{
                    combine(ww, w)
                }
            }   // tell

    /**
     * Apply the function f to the output part of this WriterT delivering a new
     *   WriterT.
     */
    fun <U> mapWritten(mf: Monad<F>, f: (W) -> U): WriterT<F, U, A> =
            mapBoth(mf){pair: Pair<W, A> -> Pair(f(pair.first), pair.second)}

    /**
     * Apply the function f to the tuple enclosed by the WriterT delivering a new
     *   WriterT.
     */
    fun <U, B> mapBoth(mf: Monad<F>, f: (Pair<W, A>) -> Pair<U, B>): WriterT<F, U, B> =
            WriterT(mf.bind(run){pair: Pair<W, A> -> mf.inject(f(pair))})

    /**
     * Obtain the output of the WriterT.
     */
    fun written(ff: Functor<F>): Kind1<F, W> =
            ff.run{
                fmap(run){pair: Pair<W, A> -> pair.first}
            }

    /**
     * Obtain the result of the WriterT.
     */
    fun result(ff: Functor<F>): Kind1<F, A> =
            ff.run{
                fmap(run){pair: Pair<W, A> -> pair.second}
            }

    fun <B> foldLeft(ff: Foldable<F>, e: B, f: (B) -> (A) -> B): B =
            ff.foldLeft(run, e){b: B -> {pair: Pair<W, A> -> f(b)(pair.second)}}

    fun <B> foldLeft(ff: Foldable<F>, e: B, f: (B, A) -> B): B = this.foldLeft(ff, e, C2(f))

    fun <B> foldRight(ff: Foldable<F>, e: B, f: (A) -> (B) -> B): B =
            ff.foldRight(run, e){pair: Pair<W, A> -> {b: B -> f(pair.second)(b)}}

    fun <B> foldRight(ff: Foldable<F>, e: B, f: (A, B) -> B): B = this.foldRight(ff, e, C2(f))

    /**
     * Apply the two functions f and g to respectively the output part and the result part
     *   of this WriterT producing a new WriterT.
     */
    fun <U, B> bimap(mf: Monad<F>, f: (W) -> U, g: (A) -> B): WriterT<F, U, B> =
            mapBoth(mf){pair: Pair<W, A> -> Pair(f(pair.first), g(pair.second))}

    /**
     * Deliver a new WriterT with the output and result pair interchanged.
     */
    fun swap(mf: Monad<F>): WriterT<F, A, W> = mapBoth(mf){pair: Pair<W, A> -> Pair(pair.second, pair.first)}



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F, W> functor(ff: Functor<F>): Functor<Kind1<Kind1<WriterTProxy, F>, W>> = object: WriterTFunctor<F, W> {
            override fun functor(): Functor<F> = ff
        }

        /**
         * Create an instance of this applicative.
         */
        fun <F, W> applicative(af: Applicative<F>, mnw: Monoid<W>): Applicative<Kind1<Kind1<WriterTProxy, F>, W>> = object: WriterTApplicative<F, W> {
            override fun applicative(): Applicative<F> = af
            override val mw: Monoid<W> = mnw
        }

        /**
         * Create an instance of this monad.
         */
        fun <F, W> monad(mf: Monad<F>, mnw: Monoid<W>): Monad<Kind1<Kind1<WriterTProxy, F>, W>> = object: WriterTMonad<F, W> {
            override fun monad(): Monad<F> = mf
            override val mw: Monoid<W> = mnw
        }

        /**
         * Create an instance of this foldable.
         */
        fun <F, W> foldable(ff: Foldable<F>): Foldable<Kind1<Kind1<WriterTProxy, F>, W>> = object: WriterTFoldable<F, W> {
            override fun foldable(): Foldable<F> = ff
        }

        /**
         * Create an instance of this traversable.
         */
        fun <F, W> traversable(tf: Traversable<F>): Traversable<Kind1<Kind1<WriterTProxy, F>, W>> = object: WriterTTraversable<F, W> {
            override fun traversable(): Traversable<F> = tf
            override fun functor(): Functor<F> = tf
        }

        /**
         * Entry point for monad bindings which enables for comprehension.
         */
        fun <F, W, A> forC(mf: Monad<F>, mnw: Monoid<W>, block: suspend MonadSyntax<Kind1<Kind1<WriterTProxy, F>, W>>.() -> A): WriterT<F, W, A> =
                monad(mf, mnw).forC.monad(block).narrow()

    }

}   // WriterT
