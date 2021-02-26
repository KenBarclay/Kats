package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.Nil
import com.adt.kotlin.kats.data.immutable.list.List.Cons
import com.adt.kotlin.kats.data.immutable.list.ListBuffer
import com.adt.kotlin.kats.data.immutable.list.ListBufferIF
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList.NonEmptyListProxy
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyListOf
import com.adt.kotlin.kats.data.immutable.nel.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface NonEmptyListFunctor : Functor<NonEmptyListProxy> {

    /**
     * Apply the function to the content(s) of the List context.
     * Function map applies the function parameter to each item in this list, delivering
     *   a new list. The result list has the same size as this list.
     *
     * Examples:
     *   [1 :| 2, 3, 4].fmap{n -> n + 1} = [2 :| 3, 4, 5]
     */
    override fun <A, B> fmap(v: Kind1<NonEmptyListProxy, A>, f: (A) -> B): NonEmptyList<B> =
            v.narrow().fmap(f)

    /**
     * Distribute the List<(A, B)> over the pair to get (List<A>, List<B>).
     *
     * Examples:
     *   [(1, 2) :| (3, 4), (5, 6)].distribute() = ([1 :| 3, 5], [2 :| 4, 6])
     */
    override fun <A, B> distribute(v: Kind1<NonEmptyListProxy, Pair<A, B>>): Pair<NonEmptyList<A>, NonEmptyList<B>> {
        fun recDistribute(list: List<Pair<A, B>>, accFirst: ListBufferIF<A>, accSecond: ListBufferIF<B>): Pair<NonEmptyList<A>, NonEmptyList<B>> {
            return when(list) {
                is Nil -> Pair(NonEmptyList(accFirst.toList()), NonEmptyList(accSecond.toList()))
                is Cons -> {
                    val head: Pair<A, B> = list.head()
                    recDistribute(list.tail(), accFirst.append(head.first), accSecond.append(head.second))
                }
            }
        }   // recDistribute
        val fabNonEmptyList: NonEmptyList<Pair<A, B>> = v.narrow()
        return recDistribute(fabNonEmptyList.toList(), ListBuffer(), ListBuffer())
    }   // distribute



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the NonEmptyList context.
     */
    override fun <A, B> lift(f: (A) -> B): (NonEmptyListOf<A>) -> NonEmptyList<B> =
            {la: NonEmptyListOf<A> ->
                fmap(la, f)
            }

}   // NonEmptyListFunctor
