package com.adt.kotlin.kats.data.instances.foldable

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.narrow

import com.adt.kotlin.kats.hkfp.typeclass.Foldable



/**
 * Foldable over an Option.
 */
interface OptionFoldable : Foldable<OptionProxy> {

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *   let foldable = Option.foldable()
     *
     *   foldable.run{ foldLeft(none(), 1){m -> {n -> m + n}} } == 1
     *   foldable.run{ foldLeft(some(5), 1){m -> {n -> m + n}} } == 6
     *   foldable.run{ foldLeft(some(6), 1){m -> {n -> m + n}} } == 7
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <A, B> foldLeft(v: OptionOf<A>, e: B, f: (B) -> (A) -> B): B {
        val vOption: Option<A> = v.narrow()
        return vOption.foldLeft(e, f)
    }

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *   let foldable = Option.foldable()
     *
     *   foldable.run{ foldRight(none(), 1){m -> {n -> m + n}} } == 1
     *   foldable.run{ foldRight(some(5), 1){m -> {n -> m + n}} } == 6
     *   foldable.run{ foldRight(some(6), 1){m -> {n -> m + n}} } == 7
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <A, B> foldRight(v: OptionOf<A>, e: B, f: (A) -> (B) -> B): B {
        val vOption: Option<A> = v.narrow()
        return vOption.foldRight(e, f)
    }

}   // OptionFoldable
