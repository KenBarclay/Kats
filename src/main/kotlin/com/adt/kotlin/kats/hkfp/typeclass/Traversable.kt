package com.adt.kotlin.kats.hkfp.typeclass

/**
 * A Traversable is a foldable functor. To make an instance one only has to define
 *   traverse (probably the most common) or sequence.
 *
 * The minimal complete definition is provided by traverse.
 *
 * The two functor laws are:
 *   traverse id = id
 *   traverse (Compose  . fmap g . f) = Compose . fmap (traverse g) . traverse f
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1



interface Traversable<F> : Foldable<F>, Functor<F> {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    fun <G, A, B> traverse(v: Kind1<F, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Kind1<F, B>>

    /**
     * Evaluate each action in the structure from left to right, and and collect the results.
     */
    fun <G, A> sequenceA(v: Kind1<F, Kind1<G, A>>, ag: Applicative<G>): Kind1<G, Kind1<F, A>> =
            traverse(v, ag){kga: Kind1<G, A> -> kga}

    /**
     * Map each element of a structure to a monadic action, evaluate these actions from left to right,
     *   and collect the results
     */
    fun <M, A, B> mapM(v: Kind1<F, A>, md: Monad<M>, f: (A) -> Kind1<M, B>): Kind1<M, Kind1<F, B>> =
            traverse(v, md, f)

    /**
     * Evaluate each monadic action in the structure from left to right, and collect the results.
     */
    fun <M, A> sequence(v: Kind1<F, Kind1<M, A>>, md: Monad<M>): Kind1<M, Kind1<F, A>> =
            sequenceA(v, md)

    /**
     * Function for is traverse with its arguments flipped.
     */
    fun <G, A, B> `for`(f: (A) -> Kind1<G, B>, ag: Applicative<G>, v: Kind1<F, A>): Kind1<G, Kind1<F, B>> =
            traverse(v, ag, f)

    /**
     * Function forM is mapM with its arguments flipped.
     */
    fun <M, A, B> forM(f: (A) -> Kind1<M, B>, md: Monad<M>, v: Kind1<F, A>): Kind1<M, Kind1<F, B>> =
            mapM(v, md, f)

}   // Traversable
