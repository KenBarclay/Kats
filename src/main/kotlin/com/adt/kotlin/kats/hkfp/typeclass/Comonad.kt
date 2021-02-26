package com.adt.kotlin.kats.hkfp.typeclass

/**
 * The Comonad class is the dual of Monad; that is, Comonad is like Monad but with
 *   all the function arrows flipped. As we see, extract is the dual of inject,
 *   duplicate is the dual of join, and extend is the dual of bind.
 *
 * @author	                    Ken Barclay
 * @since                       September 2018
 */

import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose

import com.adt.kotlin.kats.hkfp.kind.Kind1



interface Comonad<F> : Functor<F> {

    /**
     * The dual of inject.
     */
    fun <A> extract(v: Kind1<F, A>): A

    /**
     * coBind is the dual of bind. It applies a value in a context to a function
     *   that takes a value in a context and returns a normal value.
     */
    fun <A, B> coBind(v: Kind1<F, A>, f: (Kind1<F, A>) -> B): Kind1<F, B>

    fun <A, B> coflatMap(v: Kind1<F, A>, f: (Kind1<F, A>) -> B): Kind1<F, B> = this.coBind(v, f)

    /**
     * The dual of join.
     */
    fun <A> duplicate(v: Kind1<F, A>): Kind1<F, Kind1<F, A>> =
            coBind(v){kfa: Kind1<F, A> -> kfa}

    /**
     * The dual of bind.
     */
    fun <A, B> extend(v: Kind1<F, A>, f: (Kind1<F, A>) -> B): Kind1<F, B> {
        fun <X, Y> fmapC(f: (X) -> Y): (Kind1<F, X>) -> Kind1<F, Y> = {v -> fmap(v, f)}
        val duplicateC: (Kind1<F, A>) -> Kind1<F, Kind1<F, A>> = {kfa -> duplicate(kfa)}
        val composed: (Kind1<F, A>) -> Kind1<F, B> = compose(fmapC(f), duplicateC)
        return composed(v)
    }   // extend

    fun <A, B> extend(f: (Kind1<F, A>) -> B, v: Kind1<F, A>): Kind1<F, B> = this.extend(v, f)



// ---------- utility functions ---------------------------

    /**
     * Promote a function to a comonad.
     */
    fun <A, B> liftW(f: (A) -> B): (Kind1<F, A>) -> Kind1<F, B> =
            {fa -> extend(fa, compose(f, ::extract))}

    /**
     * Comonadic fixed point.
     */
    fun <A> wfix(w: Kind1<F, (Kind1<F, A>) -> A>): A =
            extract(w)(extend(w, ::wfix))

    // cfix, kfix

    /**
     * Right-to-left Cokleisli composition.
     */
    fun <A, B, C> rlComposition(v: Kind1<F, A>, f: (Kind1<F, B>) -> C, g: (Kind1<F, A>) -> B): C {
        val extendC: ((Kind1<F, A>) -> B) -> (Kind1<F, A>) -> Kind1<F, B> = {h -> {v -> extend(h, v)}}
        return compose(f, extendC(g))(v)
    }   // rlComposition

    /**
     * Left-to-right Cokleisli composition.
     */
    fun <A, B, C> lrComposition(v: Kind1<F, A>, f: (Kind1<F, A>) -> B, g: (Kind1<F, B>) -> C): C {
        val extendC: ((Kind1<F, A>) -> B) -> (Kind1<F, A>) -> Kind1<F, B> = {h -> {v -> extend(h, v)}}
        return compose(g, extendC(f))(v)
    }   // lrComposition

    // liftW2, liftW3

}   // Comonad
