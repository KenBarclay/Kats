package com.adt.kotlin.kats.data.immutable.validation

/**
 * The Validation type represents a computation that may return a successfully computed value
 *   or result in a failure. Instances of Validation[E, A], are either an instance of Success[A]
 *   or Failure[E]. The code is modelled on the FunctionalJava Validation.
 *
 * datatype Validation[E, A] = Failure of E
 *                           | Success of A
 *
 * @param E                     the type of element in a failure
 * @param A                     the type of element in a success
 *
 * @author	                    Ken Barclay
 * @since                       December 2018
 */

import com.adt.kotlin.kats.data.immutable.validation.Validation.ValidationProxy
import com.adt.kotlin.kats.data.immutable.validation.ValidationF.failureNel
import com.adt.kotlin.kats.data.immutable.validation.ValidationF.successNel

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.ListF
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList
import com.adt.kotlin.kats.data.instances.applicative.ValidationApplicative
import com.adt.kotlin.kats.data.instances.foldable.ValidationFoldable
import com.adt.kotlin.kats.data.instances.functor.ValidationFunctor
import com.adt.kotlin.kats.data.instances.monoid.ValidationMonoid
import com.adt.kotlin.kats.data.instances.semigroup.NonEmptyListSemigroup
import com.adt.kotlin.kats.data.instances.semigroup.ValidationSemigroup
import com.adt.kotlin.kats.data.instances.traversable.ValidationTraversable

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*


typealias ValidationNel<E, A> = Validation<NonEmptyList<E>, A>

typealias ValidationOf<E, A> = Kind1<Kind1<ValidationProxy, E>, A>

sealed class Validation<out E, out A> : Kind1<Kind1<ValidationProxy, E>, A> {

    class ValidationProxy private constructor()           // proxy for the Validation context



    class Failure<out E, out A> internal constructor(val err: E) : Validation<E, A>() {

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
                @Suppress("UNCHECKED_CAST") val otherFailure: Failure<E, A> = other as Failure<E, A>
                (this.err == otherFailure.err)
            }
        }   // equals

        override fun toString(): String = "Failure($err)"

    }   // Failure



    class Success<out E, out A> internal constructor(val value: A) : Validation<E, A>() {

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
                @Suppress("UNCHECKED_CAST") val otherSuccess: Success<E, A> = other as Success<E, A>
                (this.value == otherSuccess.value)
            }
        }   // equals

        override fun toString(): String = "Success($value)"

    }   // Success



    /**
     * Is this a Success and matching the given predicate.
     */
    fun exists(predicate: (A) -> Boolean): Boolean = this.fold({false}, {a -> predicate(a)})

    /**
     * Run the application of the first function if this is a Failure, otherwise the application of the
     *   second function if this is a Success.
     *
     * @param fail              the function to apply if this is a Failure
     * @param succ              the function to apply if this is a Success
     * @return                  application of the corresponding function
     */
    fun <B> fold(fail: (E) -> B, succ: (A) -> B): B {
        return when (this) {
            is Failure -> fail(this.err)
            is Success -> succ(this.value)
        }
    }   // fold

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
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
     * Return true if the Validation is a Failure.
     */
    fun isFailure(): Boolean {
        return when (this) {
            is Failure -> true
            is Success -> false
        }
    }   // isFailure

    /**
     * Return true if this Validation is a Success.
     */
    fun isSuccess(): Boolean = !isFailure()

    /**
     * Map the given function on the success of this validation.
     *
     * @param f                 transformer function
     * @return                  new validation
     */
    fun <B> map(f: (A) -> B): Validation<E, B> {
        return when (this) {
            is Failure -> Failure(this.err)
            is Success -> Success(f(this.value))
        }
    }   // map

    fun <B> fmap(f: (A) -> B): Validation<E, B> = this.map(f)

    /**
     * Flip the Failure/Success values.
     *
     * @return                  new validation
     *
    fun swap(): Validation<A, E> {
    return when(this) {
    is Failure -> Success(this.errs)
    is Success -> Failure(NonEmptyListF.singleton(this.value))
    }
    }   // swap
     *****/

    /**
     * Return Success values wrapped in Some, and None for Failure values.
     */
    fun toOption(): Option<A> {
        return when(this) {
            is Failure -> none()
            is Success -> some(this.value)
        }
    }   // toOption

    /**
     * Convert the value to an Either<E, A>.
     */
    fun toEither(): Either<E, A> {
        return when(this) {
            is Failure -> Left(this.err)
            is Success -> Right(this.value)
        }
    }
    /**
     * Convert this value to a single element List if it is a Success,
     *   otherwise return an empty List.
     */
    fun toList(): List<A> {
        return when(this) {
            is Failure -> ListF.empty()
            is Success -> ListF.singleton(this.value)
        }
    }   // toList

    /**
     * Lift the invalid value into a NonEmptyList.
     */
    fun toValidationNel(): ValidationNel<E, A> =
            when (this) {
                is Failure -> failureNel(this.err)
                is Success -> successNel(this.value)
            }

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    fun <G, B> traverse(ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Validation<E, B>> {
        val self: Validation<E, A> = this
        return when (self) {
            is Failure -> ag.run{ pure(Failure(self.err)) }
            is Success -> ag.run{ ap(f(self.value), pure{b: B -> Success<E, B>(b)}) }
        }
    }   // traverse



    companion object {

        /**
         * Create an instance of this semigroup.
         */
        fun <E, A> semigroup(se: Semigroup<E>): Semigroup<Validation<E, A>> = object: ValidationSemigroup<E, A> {
            override val sge: Semigroup<E> = se
        }

        /**
         * Create an instance of this monoid.
         */
        fun <E, A> monoid(me: Monoid<E>): Monoid<Validation<E, A>> = ValidationMonoid(me)

        /**
         * Create an instance of this functor.
         */
        fun <E> functor(): Functor<Kind1<ValidationProxy, E>> = object: ValidationFunctor<E> {}

        /**
         * Create an instance of this applicative.
         */
        fun <E> applicative(sge: Semigroup<E>): Applicative<Kind1<ValidationProxy, E>> =
                object: ValidationApplicative<E> {
                    override val se: Semigroup<E> = sge
                }

        fun <E> applicativeNel(): Applicative<Kind1<ValidationProxy, NonEmptyList<E>>> =
                object: ValidationApplicative<NonEmptyList<E>> {
                    override val se: Semigroup<NonEmptyList<E>> = NonEmptyListSemigroup()
                }

        /**
         * Create an instance of this foldable.
         */
        fun <E> foldable(): Foldable<Kind1<ValidationProxy, E>> = object: ValidationFoldable<E> {}

        /**
         * Create an instance of this traversable.
         */
        fun <E> traversable(): Traversable<Kind1<ValidationProxy, E>> = object: ValidationTraversable<E> {}

        /**********
        /**
         * Create an instance of this bifunctor.
         */
        fun bifunctor(): Bifunctor<ValidationProxy> = object: ValidationBifunctor {}

        /**
         * Create an instance of this bifoldable.
         */
        fun bifoldable(): Bifoldable<ValidationProxy> = object: ValidationBifoldable {}

        /**
         * Create an instance of this bifoldable.
         */
        fun bitraversable(): Bitraversable<ValidationProxy> = object: ValidationBitraversable {}
        **********/

    }

}   // Validation
