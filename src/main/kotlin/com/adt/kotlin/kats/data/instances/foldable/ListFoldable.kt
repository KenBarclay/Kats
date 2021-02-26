package com.adt.kotlin.kats.data.instances.foldable

import com.adt.kotlin.kats.data.immutable.list.List.ListProxy
import com.adt.kotlin.kats.data.immutable.list.ListOf
import com.adt.kotlin.kats.data.immutable.list.narrow

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface ListFoldable : Foldable<ListProxy> {

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *   let foldable = List.foldable()
     *
     *   foldable.run{ foldLeft([], 0) { x -> { y -> x + y } } } == 0
     *   foldable.run{ foldLeft([1, 2, 3, 4], 0) { x -> { y -> x + y } } } == 10
     *   foldable.run{ foldLeft([1, 2, 3, ... 1000], 0) { x -> { y -> x + y } } } == 500500
     *   foldable.run{ foldLeft([1, 2, 3, 4], []) { list -> { elem -> list.append(elem) } } } == [1, 2, 3, 4]
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <A, B> foldLeft(v: ListOf<A>, e: B, f: (B) -> (A) -> B): B = v.narrow().foldLeft(e, f)

    override fun <A, B> foldLeft(v: ListOf<A>, e: B, f: (B, A) -> B): B = foldLeft(v, e, C2(f))

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *   let foldable = List.foldable()
     *
     *  foldable.run{ foldRight([], 1) { x -> { y -> x * y } } } == 1
     *  foldable.run{ foldRight([1, 2, 3, 4], 1) { x -> { y -> x * y } } } == 24
     *  foldable.run{ foldRight([1, 2, 3, ... 1000], 0) { x -> { y -> x + y } } } == 500500
     *  foldable.run{ foldRight([1, 2, 3, ... 1000000], 0L) { x -> { y -> x + y } } } == 500000500000
     *  foldable.run{ foldRight([1, 2, 3, 4], []) { elem -> { list -> ListF.cons(elem, list) } } } == [1, 2, 3, 4]
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <A, B> foldRight(v: ListOf<A>, e: B, f: (A) -> (B) -> B): B = v.narrow().foldRight(e, f)

    override fun <A, B> foldRight(v: ListOf<A>, e: B, f: (A, B) -> B): B = foldRight(v, e, C2(f))

}   // ListFoldable
