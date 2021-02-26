package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.list.*
import com.adt.kotlin.kats.data.immutable.stream.Stream
import com.adt.kotlin.kats.data.immutable.stream.Stream.StreamProxy
import com.adt.kotlin.kats.data.immutable.stream.Stream.Nil
import com.adt.kotlin.kats.data.immutable.stream.Stream.Cons
import com.adt.kotlin.kats.data.immutable.stream.StreamF
import com.adt.kotlin.kats.data.immutable.stream.StreamOf
import com.adt.kotlin.kats.data.immutable.stream.narrow

import com.adt.kotlin.kats.hkfp.typeclass.Functor



/**
 * Functor over a Stream.
 */
interface StreamFunctor : Functor<StreamProxy> {

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
    override fun <A, B> fmap(v: StreamOf<A>, f: (A) -> B): Stream<B> = v.narrow().fmap(f)

    /**
     * Distribute the Stream<(A, B)> over the pair to get (Stream<A>, Stream<B>).
     *
     * Examples:
     *   let functor = Stream.functor()
     *
     *   [(1, 2), (3, 4), (5, 6)].distribute() = ([1, 3, 5], [2, 4, 6])
     *   [].distribute() = ([], [])
     *   functor.run{ distribute([("Ken", 25), ("John", 33), ("Jessie", 30)]) } == (["Ken", "John", "Jessie"], [25, 33, 30])
     */
    override fun <A, B> distribute(v: StreamOf<Pair<A, B>>): Pair<Stream<A>, Stream<B>> {
        fun recDistribute(stream: Stream<Pair<A, B>>, accFirst: ListBufferIF<A>, accSecond: ListBufferIF<B>): Pair<Stream<A>, Stream<B>> {
            return when(stream) {
                is Nil -> Pair(StreamF.from(accFirst.toList()), StreamF.from(accSecond.toList()))
                is Cons -> {
                    val head: Pair<A, B> = stream.head()
                    recDistribute(stream.tail().first(), accFirst.append(head.first), accSecond.append(head.second))
                }
            }
        }   // recDistribute

        val fabList: Stream<Pair<A, B>> = v.narrow()
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
    override fun <A, B> lift(f: (A) -> B): (StreamOf<A>) -> Stream<B> =
            {sa: StreamOf<A> ->
                fmap(sa, f)
            }

}   // StreamFunctor
