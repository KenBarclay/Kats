package com.adt.kotlin.kats.hkfp.typeclass

/**
 * A monoid on applicative functors. The minimally complete definition
 *   includes empty and combine. The basic intuition is that empty represents
 *   some sort of failure, and combine represents a choice between alternatives.
 *   Of course, combine should be associative and empty should be the identity
 *   element for it.
 *
 * The minimal complete definition is provided by empty and combine.
 *
 * @author	                    Ken Barclay
 * @since                       September 2018
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1



interface Alternative<F> : Applicative<F> {

    /**
     * The identity of combine.
     */
    fun <A> empty(): Kind1<F, A>

    /**
     * An associative binary operation.
     */
    fun <A> combine(a: Kind1<F, A>, b: Kind1<F, A>): Kind1<F, A>

    /**********
    /**
     * One or more. [STACKOVERFLOW: needs trampolining]
     */
    fun <A> some(v: Kind1<F, A>): Kind1<F, List<A>> {
        fun some_v(): Kind1<F, List<A>> {
            val cons: (A) -> (List<A>) -> List<A> = {a: A -> {ls: List<A> -> ListF.cons(a, ls)}}
            fun many_v(): Kind1<F, List<A>> = combine(some_v(), pure(ListF.empty()))
            return liftA2(cons)(v)(many_v())
        }
        return some_v()
    }   // some

    /**
     * Zero or more.
     */
    fun <A> many(v: Kind1<F, A>): Kind1<F, List<A>> {
        fun many_v(): Kind1<F, List<A>> {
            val cons: (A) -> (List<A>) -> List<A> = {a: A -> {ls: List<A> -> ListF.cons(a, ls)}}
            fun some_v(): Kind1<F, List<A>> = liftA2(cons)(v)(many_v())
            return combine(some_v(), pure(ListF.empty()))
        }
        return many_v()
    }   // many
    **********/

}   // Alternative
