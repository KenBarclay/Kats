package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.list.*
import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.Nil
import com.adt.kotlin.kats.data.immutable.list.List.Cons
import com.adt.kotlin.kats.data.immutable.list.List.ListProxy

import com.adt.kotlin.kats.hkfp.typeclass.Functor



/**
 * Functor over a List.
 */
interface ListFunctor : Functor<ListProxy> {

    /**
     * Apply the function to the content(s) of the List context.
     * Function map applies the function parameter to each item in this list, delivering
     *   a new list. The result list has the same size as this list.
     *
     * Examples:
     *   let functor = List.functor()
     *
     *   functor.run{ fmap([1, 2, 3, 4]) { n -> n + 1 } } == [2, 3, 4, 5]
     *   functor.run{ fmap(fmap([1, 2, 3, 4]) { n -> n + 1 }) { n -> n + 1 }} == [3, 4, 5, 6]
     *   functor.fmap(functor.fmap(numbers4) { n -> n + 1 }) { n -> n + 1 } == [3, 4, 5, 6]
     */
    override fun <A, B> fmap(v: ListOf<A>, f: (A) -> B): List<B> = v.narrow().map(f)

    /**
     * Distribute the List<(A, B)> over the pair to get (List<A>, List<B>).
     *
     * Examples:
     *   let functor = List.functor()
     *
     *   [(1, 2), (3, 4), (5, 6)].distribute() = ([1, 3, 5], [2, 4, 6])
     *   [].distribute() = ([], [])
     *   functor.run{ distribute([("Ken", 25), ("John", 33), ("Jessie", 30)]) } == (["Ken", "John", "Jessie"], [25, 33, 30])
     */
    override fun <A, B> distribute(v: ListOf<Pair<A, B>>): Pair<List<A>, List<B>> {
        fun recDistribute(list: List<Pair<A, B>>, accFirst: ListBufferIF<A>, accSecond: ListBufferIF<B>): Pair<List<A>, List<B>> {
            return when(list) {
                is Nil -> Pair(accFirst.toList(), accSecond.toList())
                is Cons -> {
                    val head: Pair<A, B> = list.head()
                    recDistribute(list.tail(), accFirst.append(head.first), accSecond.append(head.second))
                }
            }
        }   // recDistribute

        val fabList: List<Pair<A, B>> = v.narrow()
        return recDistribute(fabList, ListBuffer(), ListBuffer())
    }   // distribute



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the List context.
     *
     * Examples:
     *   let functor = List.functor()
     *   let lifted = functor.lift { n: Int -> n + 1 }
     *
     *   lifted([]) == []
     *   lifted([1, 2, 3, 4]) == [2, 3, 4, 5]
     */
    override fun <A, B> lift(f: (A) -> B): (ListOf<A>) -> List<B> =
            {la: ListOf<A> ->
                fmap(la, f)
            }

}   // ListFunctor
