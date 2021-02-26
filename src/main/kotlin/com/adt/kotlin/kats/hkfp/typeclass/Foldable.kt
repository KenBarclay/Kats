package com.adt.kotlin.kats.hkfp.typeclass

/**
 * The Foldable class represents a container that can be folded into a summary value.
 *   This allows the folding operations to be written in a container-agnostic way.
 *
 * The minimal complete definition is provided by foldLeft and foldRight.
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.data.immutable.list.List

import com.adt.kotlin.kats.data.immutable.list.ListF.cons
import com.adt.kotlin.kats.data.immutable.list.ListF.empty
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2

import com.adt.kotlin.kats.hkfp.kind.Kind1



interface Foldable<F> {

    /**
     * Combine the elements of a structure using a monoid.
     */
    fun <A> fold(v: Kind1<F, A>, md: Monoid<A>): A =
            md.run {
                foldLeft(v, empty){acc -> {a -> combine(acc, a)}}
            }   // fold

    /**
     * Map each element of the structure to a monoid, and combine the results.
     */
    fun <A, B> foldMap(v: Kind1<F, A>, md: Monoid<B>, f: (A) -> B): B =
            md.run {
                foldLeft(v, empty){b -> {a -> combine(b, f(a))}}
            }   // foldMap

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    fun <A, B> foldLeft(v: Kind1<F, A>, e: B, f: (B) -> (A) -> B): B

    fun <A, B> foldLeft(v: Kind1<F, A>, e: B, f: (B, A) -> B): B = foldLeft(v, e, C2(f))

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    fun <A, B> foldRight(v: Kind1<F, A>, e: B, f: (A) -> (B) -> B): B

    fun <A, B> foldRight(v: Kind1<F, A>, e: B, f: (A, B) -> B): B = foldRight(v, e, C2(f))

    /**
     * Map each element of a structure to an action, evaluate these actions
     *   from left to right, and ignore the results. For a version that
     *   doesn't ignore the results see Traversable.traverse.
     */
    fun <G, A, B> traverse_(v: Kind1<F, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Unit> =
            ag.run {
                foldRight(v, pure(Unit)){a -> {acc -> sDF(f(a), acc)}}
            }   // traverse_

    /**
     * Evaluate each action in the structure from left to right, and ignore
     *   the results.
     */
    fun <G, A> sequence_(v: Kind1<F, Kind1<G, A>>, ag: Applicative<G>): Kind1<G, Unit> =
            this.traverse_(v, ag){x -> x}

    /**
     * Convert the context into a list.
     */
    fun <A> toList(v: Kind1<F, A>): List<A> =
            foldRight(v, empty()){x: A, xs: List<A> -> cons(x, xs)}

    /**
     * Left associative monadic folding.
     */
    fun <G, A, B> foldLeftM(v: Kind1<F, A>, mg: Monad<G>, e: B, f: (B) -> (A) -> Kind1<G, B>): Kind1<G, B> =
            mg.run {
                foldLeft(v, inject(e)){gb: Kind1<G, B> -> {a: A -> bind(gb){b -> f(b)(a)} }}
            }

    /**
     * Monadic folding on F by mapping A values to G<B>, combining the B values
     *   using the given Monoid<B> instance. Similar to foldLeftM, but using a Monoid<B>.
     */
    fun <G, A, B> foldMapM(v: Kind1<F, A>, mg: Monad<G>, mb: Monoid<B>, f: (A) -> Kind1<G, B>): Kind1<G, B> =
            mg.run {
                foldLeftM(v, mg, mb.empty){b: B -> {a: A -> fmap(f(a)){bb: B -> mb.run{ combine(b, bb) }} }}
            }

}   // Foldable
