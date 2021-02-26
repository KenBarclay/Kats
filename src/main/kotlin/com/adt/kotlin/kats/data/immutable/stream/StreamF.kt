package com.adt.kotlin.kats.data.immutable.stream

/**
 * A lazy (not yet evaluated), immutable, singly linked list. The algebraic data
 *   type declaration is:
 *
 * datatype Stream[A] = Nil
 *                    | Cons of A * Stream[A]
 *
 * @author	                    Ken Barclay
 * @since                       September 2019
 */

import com.adt.kotlin.kats.data.immutable.list.List

import com.adt.kotlin.kats.data.immutable.stream.product.Product1
import com.adt.kotlin.kats.data.immutable.stream.product.Product1F
import com.adt.kotlin.kats.data.immutable.stream.product.Product2
import com.adt.kotlin.kats.data.immutable.stream.product.Product2F


object StreamF {

    /**
     * Create an empty stream.
     *
     * @return                      empty stream
     */
    fun <A> empty(): Stream<A> = Stream.Nil()

    /**
     * Factory constructors
     */
    fun <A> nil(): Stream<A> = Stream.Nil()
    fun <A> cons(a: A, tl: Product1<Stream<A>>): Stream<A> = Stream.Cons(a, tl)

    /**
     * Prepend (cons) the given element to this stream to product a new stream.
     *
     * @param a                 the element to prepend
     * @return                  a new stream with the given element at the head
     */
    fun <A> cons(a: A, stream: Stream<A>): Stream<A> {
        return Stream.Cons(a, object : Product1<Stream<A>>() {
            override fun first(): Stream<A> = stream
        })
    }   // cons

    /**
     * Return a stream of one element containing the given value.
     *
     * @param a                     the value for the head of the returned stream.
     * @return                      a stream of one element containing the given value.
     */
    fun <A> singleton(a: A): Stream<A> = cons(a, object: Product1<Stream<A>>() {
        override fun first(): Stream<A> = empty()
    })

    /**
     * Factory functions to create the base instances from a list of none or more elements.
     */
    fun <A> of(): Stream<A> = nil()

    fun <A> of(a1: A): Stream<A> = cons(a1, empty())

    fun <A> of(a1: A, a2: A): Stream<A> = cons(a1, cons(a2, empty()))

    fun <A> of(a1: A, a2: A, a3: A): Stream<A> = cons(a1, cons(a2, cons(a3, empty())))

    fun <A> of(a1: A, a2: A, a3: A, a4: A): Stream<A> = cons(a1, cons(a2, cons(a3, cons(a4, empty()))))

    fun <A> of(a1: A, a2: A, a3: A, a4: A, a5: A): Stream<A> = cons(a1, cons(a2, cons(a3, cons(a4, cons(a5, empty())))))

    fun <A> of(vararg seq: A): Stream<A> = from(*seq)



    /**
     * Convert a variable-length parameter series into an immutable stream.
     *   If no parameters are present then an empty stream is produced.
     *
     * Examples:
     *   from(1, 2, 3) = [1, 2, 3]
     *   from() = []
     *
     * @param seq                   variable-length parameter series
     * @return                      immutable stream of the given values
     */
    fun <A> from(vararg seq: A): Stream<A> =
            seq.foldRight(nil()) {x: A, xs: Stream<A> -> cons(x, xs)}

    /**
     * Convert an immutable list into an immutable stream.
     *
     * Examples:
     *   from([1, 2, 3]) = [1, 2, 3]
     *   from([]) = []
     *
     * @param list                  list of elements
     * @return                      immutable stream of the given values
     */
    fun <A> from(list: List<A>): Stream<A> =
            list.foldRight(nil()){x: A -> {xs: Stream<A> -> cons(x, xs)}}

    /**
     * Convert a java-based list into an immutable stream.
     *
     * Examples:
     *   from([1, 2, 3]) = [1, 2, 3]
     *   from([]) = []
     *
     * @param list                  java based list of elements
     * @return                      immutable stream of the given values
     */
    fun <A> from(list: kotlin.collections.List<A>): Stream<A> =
            list.foldRight(nil()) {x: A, xs: Stream<A> -> cons(x, xs)}

    /**
     * Return an immutable list projection of this stream.
     *
     * @return                      a list projection of this stream
     */
    fun <A> toList(stream: Stream<A>): List<A> = stream.toList()

    /**
     * Return a stream of integers from the given from value (inclusive) to the given
     *   to value (exclusive).
     *
     * @param from                  the minimum value for the stream (inclusive)
     * @param to                    the maximum value for the stream (exclusive)
     * @return                      a stream of integers from the given from value (inclusive) to the given to value (exclusive)
     */
    fun range(from: Int, to: Int): Stream<Int> =
            if (from >= to) nil() else cons(from, object: Product1<Stream<Int>>() {
                override fun first(): Stream<Int> = range(1 + from, to)
            })  // range

    /**
     * Returns an infinite stream of integers from the given from value (inclusive).
     *
     * @param from                  the minimum value for the stream (inclusive)
     * @return                      a stream of integers from the given from value (inclusive)
     */
    fun range(from: Int): Stream<Int> = cons(from, object: Product1<Stream<Int>>() {
        override fun first(): Stream<Int> = range(1 + from)
    })  // range

    /**
     * Return a stream of doubles from the given from value (inclusive) to the given
     *   to value (exclusive).
     *
     * @param from                  the minimum value for the stream (inclusive)
     * @param to                    the maximum value for the stream (exclusive)
     * @param step                  the step value across the doubles
     * @return                      a stream of doubles from the given from value (inclusive) to the given to value (exclusive)
     */
    fun range(from: Double, to: Double, step: Double = 1.0): Stream<Double> =
            if (from >= to) nil() else cons(from, object: Product1<Stream<Double>>() {
                override fun first(): Stream<Double> = range(from + step, to, step)
            })


    /**
     * Return a stream constructed by applying the given iteration function starting at the given value.
     *
     * @param f                 the iteration function
     * @param a                 the value to begin iterating from
     * @return                  a stream constructed by applying the given iteration function starting at the given value
     */
    fun <A> iterate(a: A, f: (A) -> A): Stream<A> =
            StreamF.cons(a, iterate(f(a), f))

    /**
     * Return an infinite-length stream of the given element.
     *
     * @param a                     the element to repeat infinitely
     * @return                      an infinite-length stream of the given element
     */
    fun <A> repeat(a: A): Stream<A> {
        return cons(a, object: Product1<Stream<A>>() {
            override fun first(): Stream<A> = repeat(a)
        })
    }   // repeat

    /**
     * Transforms a stream of pairs into a stream of first components and a stream of second components.
     *
     * @param stream                the stream of pairs to transform.
     * @return                      a stream of first components and a stream of second components.
     */
    fun <A, B> unzip(stream: Stream<Product2<A, B>>): Product2<Stream<A>, Stream<B>> {
        return stream.foldRightP(Product2F.product2(empty(), empty())){ p2: Product2<A, B> ->
            {p1p2: Product1<Product2<Stream<A>, Stream<B>>> ->
                val pp: Product2<Stream<A>, Stream<B>> = p1p2.first()
                Product2F.product2(cons(p2.first(), Product1F.product1(pp.first())), cons(p2.second(), Product1F.product1(pp.second())))
            }
        }
    }   // unzip

}   // StreamF
