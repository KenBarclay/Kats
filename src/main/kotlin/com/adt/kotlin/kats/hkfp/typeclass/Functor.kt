package com.adt.kotlin.kats.hkfp.typeclass

/**
 * A functor represents a context F<A> that can be mapped over. A simple intuition
 *   is that a Functor represents a container, along with the ability to apply a
 *   function uniformly to every element in the container. No default implementation
 *   is provided for function fmap.
 *
 * The minimal complete definition is provided by fmap.
 *
 * The two functor laws are:
 *   fmap id = id
 *   fmap (g . h) = (fmap g) . (fmap h)
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1


/**
 * A functor represents a context F<A> that can be mapped over.
 *
 * @param F                     representation for the context (the type constructor)
 */
interface Functor<F> {

    // ---------- primitive operation ---------------------

    /**
     * Apply the function to the content(s) of the context.
     */
    fun <A, B> fmap(v: Kind1<F, A>, f: (A) -> B): Kind1<F, B>



    // ---------- derived operations ----------------------

    /**
     * Replace all locations in the input with the given value.
     */
    fun <A, B> replaceAll(v: Kind1<F, A>, b: B): Kind1<F, B> = fmap(v){_ -> b}

    /**
     * Distribute the F<(A, B)> over the pair to get (F<A>, F<B>).
     */
    fun <A, B> distribute(v: Kind1<F, Pair<A, B>>): Pair<Kind1<F, A>, Kind1<F, B>> =
        Pair(fmap(v){pr -> pr.first}, fmap(v){pr -> pr.second})



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the F context.
     */
    fun <A, B> lift(f: (A) -> B): (Kind1<F, A>) -> Kind1<F, B> =
            {fa: Kind1<F, A> ->
                fmap(fa, f)
            }

}   // Functor
