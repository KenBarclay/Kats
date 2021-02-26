package com.adt.kotlin.kats.hkfp.typeclass

import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose
import com.adt.kotlin.kats.hkfp.fp.FunctionF.id
import com.adt.kotlin.kats.hkfp.kind.Kind1



interface Profunctor<F> {

    /**
     * Map over both arguments at the same time.
    */
    fun <A, B, C, D> dimap(v: Kind1<Kind1<F, B>, C>, f: (A) -> B, g: (C) -> D): Kind1<Kind1<F, A>, D> {
        fun <X, Y, Z> lmapC(f: (X) -> Y): (Kind1<Kind1<F, Y>, Z>) -> Kind1<Kind1<F, X>, Z> = {v -> lmap(v, f)}
        fun <X, Y, Z> rmapC(f: (Y) -> Z): (Kind1<Kind1<F, X>, Y>) -> Kind1<Kind1<F, X>, Z> = {v -> rmap(v, f)}
        return compose(lmapC(f), rmapC<B, C, D>(g))(v)
    }   // dimap

    /**
     * Map the first argument contravariantly.
    */
    fun <A, B, C> lmap(v: Kind1<Kind1<F, B>, C>, f: (A) -> B): Kind1<Kind1<F, A>, C> = dimap(v, f, id())

    /**
     * Map the first argument covariantly.
    */
    fun <A, B, C> rmap(v: Kind1<Kind1<F, A>, B>, f: (B) -> C): Kind1<Kind1<F, A>, C> = dimap(v, id(), f)

}   // Profunctor
