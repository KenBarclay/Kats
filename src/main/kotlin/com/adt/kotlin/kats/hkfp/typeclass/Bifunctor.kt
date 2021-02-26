package com.adt.kotlin.kats.hkfp.typeclass

/**
 * A Bifunctor is a type constructor that takes two type arguments and is a
 *   functor in both arguments. That is, unlike with Functor, a type constructor
 *   such as Either does not need to be partially applied for a Bifunctor instance,
 *   and the methods in this class permit mapping functions over the Left value or
 *   the Right value, or both at the same time.
 *
 * @author	                    Ken Barclay
 * @since                       September 2018
 */

import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose
import com.adt.kotlin.kats.hkfp.kind.Kind1



interface Bifunctor<F> {

    /**
     * Map over both arguments at the same time.
     */
    fun <A, B, C, D> bimap(v: Kind1<Kind1<F, A>, C>, f: (A) -> B, g: (C) -> D): Kind1<Kind1<F, B>, D> {
        val firstC: ((A) -> B) -> (Kind1<Kind1<F, A>, D>) -> Kind1<Kind1<F, B>, D> = {ff -> {v -> first(v, ff)}}
        val secondC: ((C) -> D) -> (Kind1<Kind1<F, A>, C>) -> Kind1<Kind1<F, A>, D> = {ff -> {v -> second(v, ff)}}
        val composed: (Kind1<Kind1<F, A>, C>) -> Kind1<Kind1<F, B>, D> = compose(firstC(f), secondC(g))
        return composed(v)
    }   // bimap

    /**
     * Map covariantly over the first argument.
     */
    fun <A, B, C> first(v: Kind1<Kind1<F, A>, C>, f: (A) -> B): Kind1<Kind1<F, B>, C> =
            bimap(v, f, {c: C -> c})

    /**
     * Map covariantly over the second argument.
     */
    fun <A, C, D> second(v: Kind1<Kind1<F, A>, C>, g: (C) -> D): Kind1<Kind1<F, A>, D> =
            bimap(v, {a: A -> a}, g)

}   // Bifunctor
