package com.adt.kotlin.kats.data.instances.functor

/**
 * A functor represents a context F<A> that can be mapped over. A simple intuition
 *   is that a Functor represents a container of some sort, along with the ability
 *   to apply a function uniformly to every element in the container.
 *
 * The two functor laws are:
 *   fmap id = id
 *   fmap (g . h) = (fmap g) . (fmap h)
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.narrow

import com.adt.kotlin.kats.hkfp.typeclass.Functor



/**
 * Functor over an Option.
 */
interface OptionFunctor : Functor<OptionProxy> {

    /**
     * Apply the function to the content(s) of the Option context.
     *
     * Examples:
     *   let some6 = some(6)
     *   let functor = Option.functor()
     *
     *   functor.run{ fmap(nothing){n: Int -> n + 1} } == none
     *   functor.run{ fmap(some6){n -> n + 1} } == some(7)
     */
    override fun <A, B> fmap(v: OptionOf<A>, f: (A) -> B): Option<B> = v.narrow().fmap(f)



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Option context.
     *
     * Examples:
     *   let nothing = none()
     *   let some6 = some(6)
     *   let functor = Option.functor()
     *   lifted = functor.lift{n: Int -> n + 1}
     *
     *   lifted(nothing) == none()
     *   lifted(some6) == some(7)
     */
    override fun <A, B> lift(f: (A) -> B): (OptionOf<A>) -> Option<B> =
            {oa: OptionOf<A> ->
                fmap(oa, f)
            }

}   // OptionFunctor
