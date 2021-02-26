package com.adt.kotlin.kats.data.immutable.tri

/**
 * The Try type represents a computation that may return a successfully computed value
 *   or result in an exception. Instances of Try[A], are either an instance of Success[A]
 *   or Failure[A]. The code is modelled on the Scala Try.
 *
 * The algebraic data type declaration is:
 *
 * datatype Try[A] = Failure
 *                 | Success A
 *
 * @param A                     the type of element
 *
 * @author	                    Ken Barclay
 * @since                       October 2014
 */

import com.adt.kotlin.kats.data.immutable.tri.Try.TryProxy
import com.adt.kotlin.kats.data.instances.applicative.TryApplicative
import com.adt.kotlin.kats.data.instances.foldable.TryFoldable
import com.adt.kotlin.kats.data.instances.functor.TryFunctor
import com.adt.kotlin.kats.data.instances.monad.TryMonad
import com.adt.kotlin.kats.data.instances.monoid.TryMonoid
import com.adt.kotlin.kats.data.instances.semigroup.TrySemigroup
import com.adt.kotlin.kats.data.instances.traversable.TryTraversable

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*


typealias TryOf<A> = Kind1<TryProxy, A>

sealed class Try<out A> : Kind1<TryProxy, A> {

    class TryProxy private constructor()              // proxy for the Try context



    class Failure<out A> internal constructor(val throwable: Throwable) : Try<A>() {

        override val isFailure: Boolean = true
        override val isSuccess: Boolean = false

        /**
         * Convert this to a Failure if the predicate is not satisfied.
         *
         * @param predicate         test criteria
         * @return                  a Failure if this is one or this does not satisfy the predicate; this otherwise
         */
        override fun filter(predicate: (A) -> Boolean): Try<A> = this

        /**
         * Return the value if this is a Success or throws the exception if this is a Failure.
         *
         * Examples:
         *   success(123).get() == 123
         *   failure(Exception("error")) == error
         *
         * @return                  the value or an exception
         */
        override fun get(): A = throw throwable

        /**
         * Map the given function to the value from this Success or returns this if this is a Failure.
         *
         * Examples:
         *   success(123).map{n -> 1 + n} == success(124)
         *   failure(Exception("error")).map{n -> 1 + n} == failure(Exception("error"))
         *
         * @param f                 transformation function
         * @return                  a value of type B wrapped in a Try
         */
        override fun <B> map(f: (A) -> B): Try<B> = Failure(throwable)

        /**
         * Returns a string representation of the object.
         *
         * @return                  string representation
         */
        override fun toString(): String = "Failure(${throwable.message})"

    }   // Failure



    class Success<out A> internal constructor(val value: A) : Try<A>() {

        override val isFailure: Boolean = false
        override val isSuccess: Boolean = true

        /**
         * Convert this to a Failure if the predicate is not satisfied.
         *
         * @param predicate         test criteria
         * @return                  a Failure if this is one or this does not satisfy the predicate; this otherwise
         */
        override fun filter(predicate: (A) -> Boolean): Try<A> =
                if (predicate(value)) this else Failure(TryException("filter: predicate does not hold for ${value}"))

        /**
         * Return the value if this is a Success or throws the exception if this is a Failure.
         *
         * Examples:
         *   success(123).get() == 123
         *   failure(Exception("error")) == error
         *
         * @return                  the value or an exception
         */
        override fun get(): A = value

        /**
         * Map the given function to the value from this Success or returns this if this is a Failure.
         *
         * Examples:
         *   success(123).map{n -> 1 + n} == success(124)
         *   failure(Exception("error")).map{n -> 1 + n} == failure(Exception("error"))
         *
         * @param f                 transformation function
         * @return                  a value of type B wrapped in a Try
         */
        override fun <B> map(f: (A) -> B): Try<B> = Success(f(value))

        /**
         * Returns a string representation of the object.
         *
         * @return                  string representation
         */
        override fun toString(): String = "Success(${value})"

    }   // Success



    open val isFailure: Boolean = false
    open val isSuccess: Boolean = false

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    fun <B> ap(f: Try<(A) -> B>): Try<B> =
            when (f) {
                is Failure -> Failure(f.throwable)
                is Success -> this.map(f.value)
            }

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    fun <B> bind(f: (A) -> Try<B>): Try<B> =
            when (this) {
                is Failure -> Failure(this.throwable)
                is Success -> f(this.value)
            }   // bind

    fun <B> flatMap(f: (A) -> Try<B>): Try<B> = bind(f)

    /**
     * Convert this to a Failure if the predicate is not satisfied.
     *
     * @param predicate         test criteria
     * @return                  a Failure if this is one or this does not satisfy the predicate; this otherwise
     */
    abstract fun filter(predicate: (A) -> Boolean): Try<A>

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    fun <B> foldLeft(e: B, f: (B) -> (A) -> B): B {
        return when (this) {
            is Failure -> e
            is Success -> f(e)(this.value)
        }
    }   // foldLeft

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    fun <B> foldRight(e: B, f: (A) -> (B) -> B): B {
        return when (this) {
            is Failure -> e
            is Success -> f(this.value)(e)
        }
    }   // foldRight

    /**
     * Return the value if this is a Success or throws the exception if this is a Failure.
     *
     * Examples:
     *   success(123).get() == 123
     *   failure(Exception("error")) == error
     *
     * @return                  the value or an exception
     */
    abstract fun get(): A

    /**
     * Map the given function to the value from this Success or returns this if this is a Failure.
     *
     * Examples:
     *   success(123).map{n -> 1 + n} == success(124)
     *   failure(Exception("error")).map{n -> 1 + n} == failure(Exception("error"))
     *
     * @param f                 transformation function
     * @return                  a value of type B wrapped in a Try
     */
    abstract fun <B> map(f: (A) -> B): Try<B>

    fun <B> fmap(f: (A) -> B): Try<B> = this.map(f)

    /**
     * Applies failure if this is a Failure, else success if this is a Success.
     *
     * Examples:
     *   success(123).fold({_ -> false}, {n -> (n % 2 == 1)}) == true
     *   failure(Exception("error")).fold({_ -> false}, {n -> (n % 2 == 1)}) == false
     */
    fun <B> fold(failure: (Throwable) -> B, success: (A) -> B): B {
        return when (this) {
            is Failure -> failure(this.throwable)
            is Success -> success(this.value)
        }
    }   // fold

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param other             the other object
     * @return                  true if "equal", false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val otherTry: Try<A> = other as Try<A>
            when (this) {
                is Failure -> when (otherTry) {
                    is Failure -> (this.throwable.message == otherTry.throwable.message)
                    is Success -> false
                }
                is Success -> when (otherTry) {
                    is Failure -> false
                    is Success -> (this.value == otherTry.value)
                }
            }
        }
    }   // equals

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    fun <G, B> traverse(ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Try<B>> {
        val self: Try<A> = this
        return ag.run{
            self.fold({th: Throwable -> pure(TryF.failure(th))}, { a: A -> fmap(f(a)){ b: B -> TryF.success(b) }})
        }
    }   //traverse



    companion object {

        /**
         * Create an instance of this semigroup.
         */
        fun <A> semigroup(sg: Semigroup<A>): Semigroup<Try<A>> = object: TrySemigroup<A> {
            override val sga: Semigroup<A> = sg
        }

        /**
         * Create an instance of this monoid.
         */
        fun <A> monoid(md: Monoid<A>): Monoid<Try<A>> = TryMonoid(md)

        /**
         * Create an instance of this functor.
         */
        fun functor(): Functor<TryProxy> = object: TryFunctor {}

        /**
         * Create an instance of this applicative.
         */
        fun applicative(): Applicative<TryProxy> = object: TryApplicative {}

        /**
         * Create an instance of this monad.
         */
        fun monad(): Monad<TryProxy> = object: TryMonad {}

        /**
         * Create an instance of this foldable.
         */
        fun foldable(): Foldable<TryProxy> = object: TryFoldable {}

        /**
         * Create an instance of this traversable.
         */
        fun traversable(): Traversable<TryProxy> = object: TryTraversable {}

        /**
         * Entry point for monad bindings which enables for comprehension.
         */
        fun <A> forC(block: suspend MonadSyntax<TryProxy>.() -> A): Try<A> =
                Try.monad().forC.monad(block).narrow()

    }

}   // Try
