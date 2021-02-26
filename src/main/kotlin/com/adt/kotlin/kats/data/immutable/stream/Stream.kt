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

import com.adt.kotlin.kats.data.immutable.list.*
import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some
import com.adt.kotlin.kats.data.immutable.stream.Stream.StreamProxy
import com.adt.kotlin.kats.data.immutable.stream.StreamF.cons
import com.adt.kotlin.kats.data.immutable.stream.StreamF.empty
import com.adt.kotlin.kats.data.immutable.stream.StreamF.nil
import com.adt.kotlin.kats.data.immutable.stream.product.Product1
import com.adt.kotlin.kats.data.immutable.stream.product.Product1F
import com.adt.kotlin.kats.data.immutable.stream.product.Product2
import com.adt.kotlin.kats.data.immutable.stream.product.Product2F
import com.adt.kotlin.kats.data.instances.applicative.StreamApplicative
import com.adt.kotlin.kats.data.instances.foldable.StreamFoldable
import com.adt.kotlin.kats.data.instances.functor.StreamFunctor
import com.adt.kotlin.kats.data.instances.monad.StreamMonad
import com.adt.kotlin.kats.data.instances.monoid.StreamMonoid
import com.adt.kotlin.kats.data.instances.semigroup.StreamSemigroup
import com.adt.kotlin.kats.data.instances.traversable.StreamTraversable

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*


typealias StreamOf<A> = Kind1<StreamProxy, A>

sealed class Stream<A> : Kind1<StreamProxy, A> {

    class StreamProxy private constructor()



    class Nil<A> internal constructor() : Stream<A>()

    class Cons<A> internal constructor(val hd: A, val tl: Product1<Stream<A>>) : Stream<A>()



    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     *
     * Examples:
     *   let applicative = List.applicative()
     *   let functions = [{ _ -> 0 }, { n -> 100 + n }, { n -> n * n }]
     *
     *   applicative.run{ ap([], functions) } == []
     *   applicative.run{ ap([1, 2, 3, 4], functions) } == [0, 0, 0, 0, 101, 102, 103, 104, 1, 4, 9, 16]
     *   applicative.run{ ap([1, 2, 3, 4], [] } == []
     */
    fun <B> ap(f: Stream<(A) -> B>): Stream<B> {
        tailrec
        fun recAp(vs: Stream<A>, fs: Stream<(A) -> B>, acc: ListBufferIF<B>): Stream<B> {
            return when (fs) {
                is Nil -> StreamF.from(acc.toList())
                is Cons -> recAp(vs, fs.tail().first(), acc.append(vs.map(fs.head()).toList()))
            }
        }   // recApp

        return recAp(this, f, ListBuffer())
    }   // ap

    /**
     * Append the given stream to this stream.
     *
     * @param stream            the stream to append to this one
     * @return                  a new stream that has appended the given stream
     */
    fun append(stream: Stream<A>): Stream<A> {
        val self: Stream<A> = this
        return if (this.isEmpty())
            stream
        else
            StreamF.cons(this.head(),  object: Product1<Stream<A>>() {
                override fun first(): Stream<A> = self.tail().first().append(stream)
            })
    }   // append

    /**
     * Append a single element on to a stream, eg:
     *   appendElement([1, 2, 3], 4) = [1, 2, 3, 4]
     *
     * @param a                 new element
     * @return                  new stream with element at end
     */
    fun append(a: A): Stream<A> {
        val self: Stream<A> = this
        return if (this.isEmpty())
            StreamF.singleton(a)
        else
            StreamF.cons(this.head(), object: Product1<Stream<A>>() {
                override fun first(): Stream<A> = self.tail().first().append(a)
            })
    }   // append

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     *
     * Examples:
     *   [0, 1, 2, 3].bind{n -> [n, n + 1]} = [0, 1, 1, 2, 2, 3, 3, 4]
     *   [].bind{n -> [n, n + 1]} = []
     */
    fun <B> bind(f: (A) -> Stream<B>): Stream<B> =
            foldRight(nil()){a: A -> {str: Stream<B> -> f(a).append(str)}}

    fun <B> flatMap(f: (A) -> Stream<B>): Stream<B> = this.bind(f)

    /**
     * Append the given stream to this stream.
     *
     * @param stream            the stream to append to this one
     * @return                  a new stream that has appended the given stream
     */
    fun concatenate(stream: Stream<A>): Stream<A> = this.append(stream)

    /**
     * Determine if this stream contains the element determined by the predicate.
     *
     * Examples:
     *   [1, 2, 3, 4].contains{n -> (n == 4)} = true
     *   [1, 2, 3, 4].contains{n -> (n == 5)} = false
     *   [].contains{n -> (n == 4)} = false
     *
     * @param predicate         search predicate
     * @return                  true if search element is present, false otherwise
     */
    fun contains(predicate: (A) -> Boolean): Boolean {
        tailrec
        fun recContains(predicate: (A) -> Boolean, ps: Stream<A>): Boolean {
            return when(ps) {
                is Nil -> false
                is Cons -> if (predicate(ps.head())) true else recContains(predicate, ps.tail().first())
            }
        }   // recContains

        return recContains(predicate, this)
    }   // contains

    /**
     * Determine if this stream contains the given element.
     *
     * Examples:
     *   [1, 2, 3, 4].contains(4) = true
     *   [1, 2, 3, 4].contains(5) = false
     *   [].contains(4) = false
     *
     * @param x                 search element
     * @return                  true if search element is present, false otherwise
     */
    fun contains(x: A): Boolean = this.contains{y: A -> (y == x)}

    /**
     * Count the number of times a value appears in this stream matching the criteria.
     *
     * Examples:
     *   [1, 2, 3, 4].count{n -> (n == 2)} = 1
     *   [1, 2, 3, 4].count{n -> (n == 5)} = 0
     *   [].count{n -> (n == 2)} = 0
     *   [1, 2, 1, 2, 2].count{n -> (n == 2)} == 3
     *
     * @param predicate         the search criteria
     * @return                  the number of occurrences
     */
    fun count(predicate: (A) -> Boolean): Int {
        tailrec
        fun recCount(predicate: (A) -> Boolean, ps: Stream<A>, acc: Int): Int {
            return when(ps) {
                is Nil -> acc
                is Cons -> recCount(predicate, ps.tail().first(), if (predicate(ps.head())) 1 + acc else acc)
            }
        }   // recCount

        return recCount(predicate, this, 0)
    }   // count

    /**
     * Count the number of times the parameter appears in this stream.
     *
     * Examples:
     *   [1, 2, 3, 4].count(2) = 1
     *   [1, 2, 3, 4].count(5) = 0
     *   [].count(2) = 0
     *   [1, 2, 1, 2, 2].count(2) == 3
     *
     * @param x                 the search value
     * @return                  the number of occurrences
     */
    fun count(x: A): Int = this.count{y: A -> (y == x)}

    /**
     * Drop the given number of elements from the head of this stream if they are available.
     *
     * @param n                 the number of elements to drop from the head of this stream
     * @return                  a stream with a length the same, or less than, this stream
     */
    fun drop(n: Int): Stream<A> {
        tailrec
        fun recDrop(m: Int, ps: Stream<A>): Stream<A> {
            return if (m <= 0)
                ps
            else if (ps.isEmpty())
                ps
            else
                recDrop(m - 1, ps.tail().first())
        }

        return recDrop(n, this)
    }   // drop

    /**
     * Function dropUntil removes the leading elements from this list until a match
     *   against the predicate. The result list size will not exceed this list size.
     *   The result list is a suffix of this list.
     *
     * Examples:
     *   [1, 2, 3, 4].dropUntil{n -> (n <= 2)} = [1, 2, 3, 4]
     *   [1, 2, 3, 4, 5].dropUntil{n -> (n > 3)} = [4, 5]
     *   [1, 2, 3, 4].dropUntil{n -> (n <= 5)} = [1, 2, 3, 4]
     *   [1, 2, 3, 4].dropUntil{n -> (n <= 0)} = []
     *   [].dropUntil{n -> (n <= 2)} = []
     *
     * @param predicate         criteria
     * @return                  new list of remaining elements
     */
    fun dropUntil(predicate: (A) -> Boolean): Stream<A> {
        tailrec
        fun recDropUntil(predicate: (A) -> Boolean, xs: Stream<A>): Stream<A> {
            return when (xs) {
                is Nil -> StreamF.empty()
                is Cons -> if (predicate(xs.head())) xs else recDropUntil(predicate, xs.tail().first())
            }
        }   // recDropUntil

        return recDropUntil(predicate, this)
    }   // dropUntil

    /**
     * Removes elements from the head of this stream that do not match the given predicate function
     *   until an element is found that does match or the stream is exhausted.
     *
     * @param predicate         the predicate function to apply through this stream
     * @return                  the stream whose first element does not match the given predicate function
     */
    fun dropWhile(predicate: (A) -> Boolean): Stream<A> {
        tailrec
        fun recDropWhile(pred: (A) -> Boolean, ps: Stream<A>): Stream<A> {
            return if (ps.isEmpty())
                ps
            else if(!pred(ps.head()))
                ps
            else
                recDropWhile(pred, ps.tail().first())
        }

        return recDropWhile(predicate, this)
    }   // dropWhile

    /**
     * Are two streams equal?
     *
     * @param other             the other stream
     * @return                  true if both streams are the same; false otherwise
     */
    override fun equals(other: Any?): Boolean {
        tailrec
        fun recEquals(ps: Stream<A>, qs: Stream<A>): Boolean {
            return when(ps) {
                is Nil -> {
                    when(qs) {
                        is Nil -> true
                        is Cons -> false
                    }
                }
                is Cons -> {
                    when(qs) {
                        is Nil -> false
                        is Cons -> if (ps.head() != qs.head()) false else recEquals(ps.tail().first(), qs.tail().first())
                    }
                }
            }
        }   // recEquals

        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val otherStream: Stream<A> = other as Stream<A>
            recEquals(this, otherStream)
        }
    }   // equals

    /**
     * Filter elements from this stream by returning only elements which produce true
     *   when the given function is applied to them.
     *
     * @param predicate         the predicate function to filter on
     * @return                  a new stream whose elements all match the given predicate
     */
    fun filter(predicate: (A) -> Boolean): Stream<A> {
        val str: Stream<A> = this.dropWhile{a: A -> !predicate(a)}
        return if (str.isEmpty())
            str
        else
            StreamF.cons(str.head(), object: Product1<Stream<A>>() {
                override fun first(): Stream<A> = str.tail().first().filter(predicate)
            })
    }   // filter

    /**
     * Find the first occurrence of an element that matches the given predicate or no value if no
     *   elements match.
     *
     * @param predicate         the predicate function to test on elements of this stream
     * @return                  the first occurrence of an element that matches the given predicate or no value if no elements match
     */
    fun find(predicate: (A) -> Boolean): Option<A> {
        tailrec
        fun recFind(pred: (A) -> Boolean, ps: Stream<A>): Option<A> {
            return if (ps.isEmpty())
                none()
            else if (pred(ps.head()))
                some(ps.head())
            else
                recFind(pred, ps.tail().first())
        }

        return recFind(predicate, this)
    }   // find

    /**
     * Performs a left-fold reduction across this stream. This function runs in constant space.
     *
     * @param e                 the beginning value to start the application from
     * @param f                 the function to apply on each element of the stream
     * @return                  the final result after the left-fold reduction
     */
    fun <B> foldLeft(e: B, f: (B) -> (A) -> B): B {
        tailrec
        fun recFoldLeft(b: B, g: (B) -> (A) -> B, ps: Stream<A>): B {
            return if (ps.isEmpty())
                b
            else
                recFoldLeft(g(b)(ps.head()), g, ps.tail().first())
        }   // recFoldLeft

        return recFoldLeft(e, f, this)
    }   // foldLeft

    fun <B> foldLeft(e: B, f: (B, A) -> B): B = this.foldLeft(e, C2(f))

    /**
     * Take the first 2 elements of the stream and applies the function to them,
     *   then applies the function to the result and the third element and so on.
     *
     * @param f                 the function to apply on each element of the stream
     * @return                  the final result after the left-fold reduction
     */
    fun foldLeft1(f: (A) -> (A) -> A): A {
        return if (this.isEmpty())
            throw StreamException("foldLeft1: empty stream")
        else
            this.tail().first().foldLeft(this.head(), f)
    }   // foldLeft1

    fun foldLeft1(f: (A, A) -> A): A = this.foldLeft1(C2(f))

    /**
     * Perform a right-fold reduction across this stream. This function uses O(length) stack space.
     *
     * @param b                 the beginning value to start the application from
     * @param f                 the function to apply on each element of the stream
     * @return                  the final result after the right-fold reduction
     */
    fun <B> foldRightP(e: B, f: (A) -> (Product1<B>) -> B): B {
        val self: Stream<A> = this
        return if (this.isEmpty())
            e
        else f(this.head())(object: Product1<B>() {
            override fun first(): B = self.tail().first().foldRightP(e, f)
        })
    }   // foldRightP

    fun <B> foldRightP(e: B, f: (A, Product1<B>) -> B): B = this.foldRightP(e, C2(f))

    /**
     * Perform a right-fold reduction across this stream. This function uses O(length) stack space.
     *
     * @param e                 the beginning value to start the application from
     * @param f                 the function to apply on each element of the stream
     * @return                  the final result after the right-fold reduction
     */
    fun <B> foldRight(e: B, f: (A) -> (B) -> B): B {
        val compose: (((B) -> B) -> ((Product1<B>) -> B)) -> ((A) -> ((B) -> B)) -> ((A) -> ((Product1<B>) -> B)) = {ff -> {gg -> {a -> ff(gg(a))}}}
        val forwardCompose: ((Product1<B>) -> B) -> ((B) -> B) -> ((Product1<B>) -> B) = {ff: (Product1<B>) -> B -> {gg: (B) -> B -> {product: Product1<B> -> gg(ff(product))}}}
        val first: (Product1<B>) -> B = {product: Product1<B> -> product.first()}
        val forwardComposeFirst: ((B) -> B) -> ((Product1<B>) -> B) = forwardCompose(first)
        val composeForwardComposeFirstF: (A) -> ((Product1<B>) -> B) = compose(forwardComposeFirst)(f)
        return this.foldRightP(e, composeForwardComposeFirstF)
    }   // foldRight

    fun <B> foldRight(e: B, f: (A, B) -> B): B = this.foldRight(e, C2(f))

    /**
     * A variant of foldRight that has no starting value argument, and thus must
     *   be applied to non-empty streams. The initial value is used as the start
     *   value. Throws a StreamException on an empty stream.
     *
     * Examples:
     *   [1, 2, 3, 4].foldRight1{m -> {n -> m * n}} = 24
     *
     * @param f                 curried binary function:: A -> A -> A
     * @return                  folded result
     */
    fun foldRight1(f: (A) -> (A) -> A): A = when(this) {
        is Nil -> throw StreamException("foldRight1: empty stream")
        is Cons -> this.tail().first().foldRight(this.head(), f)
    }   // foldRight1

    fun foldRight1(f: (A, A) -> A): A = this.foldRight1(C2(f))

    /**
     * Return true if the predicate holds for all of the elements of this stream,
     *   false otherwise (true for the empty stream).
     *
     * @param predicate         the predicate function to test on each element of this stream
     * @return                  true if the predicate holds for all of the elements of this stream, false otherwise
     */
    fun forAll(predicate: (A) -> Boolean): Boolean =
            this.isEmpty() || predicate(this.head()) && this.tail().first().forAll(predicate)

    /**
     * Return the element at the specified position in this stream.
     *   Throws a StreamException if the index is out of bounds.
     *
     * @param index             position in list
     * @return                  the element at the specified position in the stream
     */
    operator fun get(index: Int): A {
        tailrec
        fun recGet(idx: Int, ps: Stream<A>): A {
            return if (idx < 0)
                throw StreamException("get: negative index")
            else if (ps.isEmpty())
                throw StreamException("get: empty stream")
            else if (idx == 0)
                ps.head()
            else
                recGet(idx - 1, ps.tail().first())
        }

        return recGet(index, this)
    }   // get

    /**
     * The first element of the stream or fails for the empty stream.
     *
     * @return                  the first element of the stream or fails for the empty stream
     */
    fun head(): A =
            when (this) {
                is Nil -> throw StreamException("head: empty stream")
                is Cons -> hd
            }   // head

    /**
     * Find the index of the first occurrence of the given value, or -1 if absent.
     *
     * Examples:
     *   [1, 2, 3, 4].indexOf{n -> (n == 1)} = 0
     *   [1, 2, 3, 4].indexOf{n -> (n == 3)} = 2
     *   [1, 2, 3, 4].indexOf{n -> (n == 5)} = -1
     *   [].indexOf{n -> (n == 2)} = -1
     *
     * @param predicate         the search predicate
     * @return                  the index position
     */
    fun indexOf(predicate: (A) -> Boolean): Int {
        tailrec
        fun recIndexOf(predicate: (A) -> Boolean, ps: Stream<A>, acc: Int): Int {
            return when(ps) {
                is Nil -> -1
                is Cons -> if (predicate(ps.head())) acc else recIndexOf(predicate, ps.tail().first(), 1 + acc)
            }
        }   // recIndexOf

        return recIndexOf(predicate, this, 0)
    }   // indexOf

    /**
     * Find the index of the given value, or -1 if absent.
     *
     * Examples:
     *   [1, 2, 3, 4].indexOf(1) = 0
     *   [1, 2, 3, 4].indexOf(3) = 2
     *   [1, 2, 3, 4].indexOf(5) = -1
     *   [].indexOf(2) = -1
     *
     * @param x                 the search value
     * @return                  the index position
     */
    fun indexOf(x: A): Int = this.indexOf{y -> (y == x)}

    /**
     * Return all the elements of this stream except the last one. The stream must be non-empty.
     *   Throws a StreamException on an empty list.
     *
     * Examples:
     *   [1, 2, 3, 4].init() = [1, 2, 3]
     *   [5].init() = []
     *
     * @return                  new stream of the initial elements
     */
    fun init(): Stream<A> {
        tailrec
        fun recInit(ps: Stream<A>, acc: Stream<A>): Stream<A> {
            return when(ps) {
                is Nil -> throw StreamException("init: empty stream")
                is Cons -> if (ps.tail().first().isEmpty()) acc else recInit(ps.tail().first(), acc.append(ps.head()))
            }
        }   // recInit

        return recInit(this, StreamF.empty())
    }   // init

    /**
     * Return a stream of all prefixes of this stream. A stream is considered a prefix of itself in this context.
     *
     * @return                  a stream of the prefixes of this stream, starting with the stream itself
     */
    fun inits(): Stream<Stream<A>> {
        tailrec
        fun recInits(ps: Stream<A>, buf: Stream<A>, acc: Stream<Stream<A>>): Stream<Stream<A>> {
            return when(ps) {
                is Nil -> cons(nil(), acc)
                is Cons -> {
                    val buff: Stream<A> = nil<A>().append(buf.append(ps.head()))
                    recInits(ps.tail().first(), buff, acc.append(buff))
                }
            }
        }   // recInits

        return recInits(this, nil(), nil())
    }   // inits

    /**
     * Interleaves the given stream with this stream to produce a new stream.
     *
     * @param stream            the stream to interleave this stream with
     * @return                  a new stream with elements interleaved from this stream and the given stream
     */
    fun interleave(stream: Stream<A>): Stream<A> {
        tailrec
        fun recInterleave(ps: Stream<A>, qs: Stream<A>, acc: Stream<A>): Stream<A> {
            return when(ps) {
                is Nil -> acc
                is Cons -> {
                    when(qs) {
                        is Nil -> acc
                        is Cons -> recInterleave(ps.tail().first(), qs.tail().first(), acc.append(ps.head()).append(qs.head()))
                    }
                }
            }
        }   // recInterleave

        return recInterleave(this, stream, StreamF.empty())
    }   // interleave

    /**
     * Intersperses the given value between each two elements of the stream.
     *
     * @param sep               the value to intersperse between values of the stream
     * @return                  a new stream with the given value between each two elements of the stream
     */
    fun intersperse(sep: A): Stream<A> {
        val self: Stream<A> = this
        return if (this.isEmpty())
            this
        else
            StreamF.cons(this.head(), object: Product1<Stream<A>>() {
                override fun first(): Stream<A> = prefix(sep, self.tail().first())
            })
    }   // intersperse

    /**
     * Return true if this stream is empty, false otherwise.
     *
     * @return                  true if this stream is empty
     */
    fun isEmpty(): Boolean =
            when (this) {
                is Nil -> true
                is Cons -> false
            }   // is Empty

    /**
     * Get the last element of this stream. Undefined for infinite streams.
     *
     * @return                  the last element in this stream, if there is one
     */
    fun last(): A = this.reverse().head()

    /**
     * Obtain the length of a stream. This function will not terminate for an infinite stream.
     *
     * @return                  number of elements in the stream
     */
    fun length(): Int = this.size()

    /**
     * Map the given function across this stream.
     *
     * @param f                 the function to map across this stream
     * @return                  a new stream after the given function has been applied to each element
     */
    fun <B> map(f: (A) -> B): Stream<B> {
        val self: Stream<A> = this
        return if (this.isEmpty())
            StreamF.nil()
        else
            StreamF.cons(f(this.head()), object: Product1<Stream<B>>() {
                override fun first(): Stream<B> = self.tail().first().map(f)
            })
    }   // map

    fun <B> fmap(f: (A) -> B): Stream<B> = this.map(f)

    /**
     * Remove the first occurrence of the given element from the stream.
     *
     * @param a                 element to be removed
     * @return                  new stream with element deleted
     */
    fun remove(a: A): Stream<A> {
        val self: Stream<A> = this
        return if (this.isEmpty())
            StreamF.empty()
        else if(this.head() == a)
            self.tail().first()
        else StreamF.cons(this.head(), object: Product1<Stream<A>>() {
            override fun first(): Stream<A> = self.tail().first().remove(a)
        })
    }   // remove

    /**
     * Remove the first occurrence of the matching element from the stream.
     *
     * @param predicate         search predicate
     * @return                  new stream with element deleted
     */
    fun remove(predicate: (A) -> Boolean): Stream<A> {
        val self: Stream<A> = this
        return if (this.isEmpty())
            StreamF.empty()
        else if(predicate(this.head()))
            self.tail().first()
        else
            StreamF.cons(this.head(), object: Product1<Stream<A>>() {
                override fun first(): Stream<A> = self.tail().first().remove(predicate)
            })
    }   // remove

    /**
     * Reverse this stream in constant stack space.
     *
     * @return                  a new stream that is the reverse of this one
     */
    fun reverse(): Stream<A> {
        return this.foldLeft(StreamF.nil<A>()){str: Stream<A> ->
            {a: A -> StreamF.cons(a, object: Product1<Stream<A>>() {
                override fun first(): Stream<A> = str
            })}
        }
    }   // reverse

    /**
     * Obtain the length of a stream. This function will not terminate for an infinite stream.
     *
     * @return                  number of elements in the stream
     */
    fun size(): Int = StreamF.toList(this).size()

    /**
     * Returns a tuple where the first element is the longest prefix of this stream that satisfies
     *   the given predicate and the second element is the remainder of the stream.
     *
     * @param predicate         a predicate to be satisfied by a prefix of this stream
     * @return                  a tuple where the first element is the longest prefix of this stream that satisfies the given predicate and the second element is the remainder of the stream
     */
    fun span(predicate: (A) -> Boolean): Product2<Stream<A>, Stream<A>> {
        val self: Stream<A> = this
        return if (this.isEmpty())
            Product2F.product2(this, this)
        else if (predicate(this.head())) {
            val p1p2: Product1<Product2<Stream<A>, Stream<A>>> = object: Product1<Product2<Stream<A>, Stream<A>>>() {
                override fun first(): Product2<Stream<A>, Stream<A>> = self.tail().first().span(predicate)
            }
            object: Product2<Stream<A>, Stream<A>>() {
                override fun first(): Stream<A> = StreamF.cons(self.head(), p1p2.map{p2: Product2<Stream<A>, Stream<A>> -> p2.first()})
                override fun second(): Stream<A> = p1p2.first().second()
            }
        } else
            Product2F.product2(StreamF.nil(), this)
    }   // span

    /**
     * The stream without the first element or fails for the empty stream.
     *
     * @return                  the stream without the first element or fails for the empty stream
     */
    fun tail(): Product1<Stream<A>> =
            when (this) {
                is Nil -> throw StreamException("tail: empty stream")
                is Cons -> tl
            }   // tail

    /**
     * Return a stream of the suffixes of this stream. A stream is considered to be a suffix of itself in this context.
     *
     * <{1, 2, 3}>tails() == <{<{1, 2, 3}>, <{2, 3}>, <{3}>}>
     *
     * @return                  a stream of the suffixes of this stream, starting with the stream itself
     */
    fun tails(): Stream<Stream<A>> {
        tailrec
        fun recTails(ps: Stream<A>, acc: Stream<Stream<A>>): Stream<Stream<A>> {
            return when(ps) {
                is Nil -> acc.append(empty<A>())
                is Cons -> recTails(ps.tail().first(), acc.append(ps))
            }
        }   // recTails

        return recTails(this, empty())
        /***val self: Stream<A> = this
        return if (this.isEmpty())
        StreamF.nil()
        else
        StreamF.cons(self, object: Product1<Stream<Stream<A>>>() {
        override fun first(): Stream<Stream<A>> = self.tail().first().tails()
        })
         ***/
    }   // tails

    /**
     * Returns the first n elements from the head of this stream
     *
     * @param n                 the number of elements to take from this stream
     * @return                  the first n elements from the head of this stream
     */
    fun take(n: Int): Stream<A> {
        val self: Stream<A> = this
        return if (n <= 0 || this.isEmpty())
            nil()
        else
            cons(this.head(), object: Product1<Stream<A>>() {
                override fun first(): Stream<A> = self.tail().first().take(n - 1)
            })
    }   // take

    /**
     * Function takeUntil retrieves the leading elements from this list that match
     *   some predicate. The result list size will not exceed this list size.
     *   The result list is a prefix of this list.
     *
     * Examples:
     *   [1, 2, 3, 4].takeUntil{n -> (n <= 2)} == []
     *   [1, 2, 3, 4].takeUntil{n -> (n > 5)} == [1, 2, 3, 4]
     *   [1, 2, 3, 4].takeUntil{n -> (n > 3)} == [1, 2, 3]
     *   [].takeUntil{n -> (n <= 2)} == []
     *
     * @param predicate         criteria
     * @return                  new list of trailing elements matching criteria
     */
    fun takeUntil(predicate: (A) -> Boolean): Stream<A> = this.takeWhile{a -> !predicate(a)}

    /**
     * Return the first elements of the head of this stream that match the given predicate function.
     *
     * @param predicate         the predicate function to apply on this stream until it finds an element that does not hold, or the stream is exhausted
     * @return                  the first elements of the head of this stream that match the given predicate function
     */
    fun takeWhile(predicate: (A) -> Boolean): Stream<A> {
        val self: Stream<A> = this
        return if (this.isEmpty())
            this
        else if (predicate(this.head()))
            cons(this.head(), object: Product1<Stream<A>>() {
                override fun first(): Stream<A> = self.tail().first().takeWhile(predicate)
            })
        else
            StreamF.nil<A>()
    }   // takeWhile

    /**
     * Returns true if the predicate holds for at least one of the elements of this
     *   stream, false otherwise (false for the empty stream).
     *
     * @param predicate         the predicate function to test on the elements of this stream
     * @return                  true if the predicate holds for at least one of the elements of this stream
     */
    fun thereExists(predicate: (A) -> Boolean): Boolean =
            !dropWhile{a: A -> !predicate(a)}.isEmpty()

    /**
     * There exists only one element of this stream that meets some criteria. If the
     *   stream is empty then false is returned.
     *
     * Examples:
     *   [1, 2, 3, 4].thereExistsUnique{m -> (m == 2)} = true
     *   [1, 2, 3, 4].thereExistsUnique{m -> (m == 5)} = false
     *   [1, 2, 3, 4].thereExistsUnique{m -> true} = false
     *   [1, 2, 3, 4].thereExistsUnique{m -> false} = false
     *   [].thereExistsUnique{m -> (m == 2)} = false
     *
     * @param predicate         criteria
     * @return                  true if only one element matches the criteria
     */
    fun thereExistsUnique(predicate: (A) -> Boolean): Boolean = (this.count(predicate) == 1)

    /**
     * Return an immutable list projection of this stream.
     *
     * @return                      a list projection of this stream
     */
    fun toList(): List<A> {
        var result: List<A> = ListF.empty()
        var cursor: Stream<A> = this
        while (!cursor.isEmpty()) {
            result = result.append(cursor.head())
            cursor = cursor.tail().first()
        }
        return result
    }   // toList

    /**
     * Simple presentation.
     */
    override fun toString(): String =
            when (this) {
                is Nil -> "Nil"
                is Cons -> "Cons($hd, ${tl.first()})"
            }

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     *
     * Examples:
     *   [].traverse(listApplicative()){n -> [n, n + 1]} = [[]]
     *   [0, 1, 2, 3].traverse(listApplicative()){n -> [n, n + 1]} = [
     *       [0, 1, 2, 3], [0, 1, 2, 4], [0, 1, 3, 3], [0, 1, 3, 4],
     *       [0, 2, 2, 3], [0, 2, 2, 4], [0, 2, 3, 3], [0, 2, 3, 4],
     *       [1, 1, 2, 3], [1, 1, 2, 4], [1, 1, 3, 3], [1, 1, 3, 4],
     *       [1, 2, 2, 3], [1, 2, 2, 4], [1, 2, 3, 3], [1, 2, 3, 4]
     *   ]
     *
     *   [].traverse(optionApplicative()){n -> some(n % 2 == 0} = some([])
     *   [0, 1, 2, 3].traverse(optionApplicative()){n -> some(n % 2 == 0} = some([true, false, true, false])
     */
    fun <G, B> traverse(ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Stream<B>> {
        val self: Stream<A> = this
        return ag.run {
            val consF: (A) -> (Kind1<G, Stream<B>>) -> Kind1<G, Stream<B>> =
                    {a: A ->
                        {xs: Kind1<G, Stream<B>> ->
                            ag.run{
                                {kgb: Kind1<G, B> ->
                                    {kglb: Kind1<G, Stream<B>> ->
                                        ap(kglb, fmap(kgb){b: B -> {bs: Stream<B> -> StreamF.cons(b, bs)}})
                                    }
                                }
                            }(f(a))(xs)
                        }
                    }
            self.foldRight(pure(nil()), consF)
        }
    }   // traverse

    /**
     * Zips this stream with the given stream of functions, applying each function in turn to the
     *   corresponding element in this stream to produce a new stream. If this stream and the given stream
     *   have different lengths, then the longer stream is normalised so this function never fails.
     *
     * @param fs                the stream of functions to apply to this stream
     * @return                  a new stream with a length the same as the shortest of this stream and the given stream
     */
    fun <B> zap(fs: Stream<(A) -> B>): Stream<B> {
        val self: Stream<A> = this
        return if (this.isEmpty() || fs.isEmpty())
            StreamF.nil()
        else
            StreamF.cons(fs.head()(this.head()), object: Product1<Stream<B>>() {
                override fun first(): Stream<B> = self.tail().first().zap(fs.tail().first())
            })
    }   // zap

    /**
     * Zip this stream with the given stream to produce a stream of pairs. If this stream and the
     *   given stream have different lengths, then the longer stream is normalised so this function
     *   never fails.
     *
     * @param stream            the stream to zip this stream with
     * @return                  a new stream with a length the same as the shortest of this stream and the given stream
     */
    fun <B> zip(stream: Stream<B>): Stream<Product2<A, B>> =
            this.zipWith(stream){a: A -> {b: B -> Product2F.product2(a, b)}}

    /**
     * Zip this stream with the given stream using the given function to produce a new stream. If
     *   this stream and the given stream have different lengths, then the longer stream is normalised
     *   so this function never fails.
     *
     * @param stream            the stream to zip this stream with
     * @param f                 the function to zip this stream and the given stream with
     * @return                  a new stream with a length the same as the shortest of this stream and the given stream
     */
    fun <B, C> zipWith(stream: Stream<B>, f: (A) -> (B) -> C): Stream<C> =
            stream.zap(this.zap(StreamF.repeat(f)))

    /**
     * Zip this stream with the given stream using the given function to produce a new stream. If
     *   this stream and the given stream have different lengths, then the longer stream is normalised
     *   so this function never fails.
     *
     * @param stream            the stream to zip this stream with
     * @param f                 the function to zip this stream and the given stream with
     * @return                  a new stream with a length the same as the shortest of this stream and the given stream
     */
    fun <B, C> zipWith(stream: Stream<B>, f: (A, B) -> C): Stream<C> = this.zipWith(stream, C2(f))

    /**
     * Zip this stream with the index of its element as a pair.
     *
     * @return                  a new stream with the same length as this stream
     */
    fun zipWithIndex(): Stream<Product2<A, Int>> =
            this.zipWith(StreamF.range(0)){a: A -> {n: Int -> Product2F.product2(a, n)}}



    companion object {

        /**
         * Create an instance of this semigroup.
         */
        fun <A> semigroup(): Semigroup<Stream<A>> = object: StreamSemigroup<A> {}

        /**
         * Create an instance of this monoid.
         */
        fun <A> monoid(): Monoid<Stream<A>> = StreamMonoid()

        /**
         * Create an instance of this functor.
         */
        fun functor(): Functor<StreamProxy> = object: StreamFunctor {}

        /**
         * Create an instance of this applicative.
         */
        fun applicative(): Applicative<StreamProxy> = object: StreamApplicative {}

        /**
         * Create an instance of this monad.
         */
        fun monad(): Monad<StreamProxy> = object: StreamMonad {}

        /**
         * Create an instance of this foldable.
         */
        fun foldable(): Foldable<StreamProxy> = object: StreamFoldable {}

        /**
         * Create an instance of this traversable.
         */
        fun traversable(): Traversable<StreamProxy> = object: StreamTraversable {}

    }



// ---------- implementation ------------------------------

    fun prefix(a: A, stream: Stream<A>): Stream<A> =
            if (stream.isEmpty()) stream else StreamF.cons(a, Product1F.product1(StreamF.cons(stream.head(),
                    object: Product1<Stream<A>>() {
                        override fun first(): Stream<A> = prefix(a, stream.tail().first())
                    })))

}   // Stream
