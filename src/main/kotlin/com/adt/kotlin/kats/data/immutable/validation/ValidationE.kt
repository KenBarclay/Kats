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

import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList
import com.adt.kotlin.kats.data.immutable.validation.Validation.Failure
import com.adt.kotlin.kats.data.immutable.validation.Validation.Success
import com.adt.kotlin.kats.hkfp.kind.Kind1

import com.adt.kotlin.kats.hkfp.typeclass.*



/**
 * Since class Either<A, B> is the only implementation for Kind1<Kind1<EitherProxy, A>, B>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <E, A> ValidationOf<E, A>.narrow(): Validation<E, A> = this as Validation<E, A>



// Contravariant extension functions:

/**
 * Converts this to a Failure if the predicate is not satisfied.
 *
 * @param predicate         test criteria
 * @return                  a Failure if this is one or this does not satisfy the predicate; this otherwise
 */
fun <E, A> Validation<E, A>.filter(predicate: (A) -> Boolean, md: Monoid<E>): Validation<E, A> {
    return when (this) {
        is Failure -> this
        is Success -> if (predicate(this.value)) this else Failure(md.empty)
    }
}   // filter

/**
 * Returns the value if this is a Success or the given default argument if this is a Failure.
 *
 * @param defaultValue      return value if this is a Failure
 * @return                  the value wrapped by this Success or the given default
 */
fun <E, A> Validation<E, A>.getOrElse(defaultValue: A): A {
    return when(this) {
        is Failure -> defaultValue
        is Success -> this.value
    }
}   // getOrElse



// Functor extension functions:

/**
 * An infix symbol for fmap.
 */
infix fun <E, A, B> ((A) -> B).dollar(v: ValidationNel<E, A>): ValidationNel<E, B> = v.map(this)



// Applicative extension functions:

/**
 * An infix symbol for ap.
 */
infix fun <E, A, B> ValidationNel<E, (A) -> B>.apply(v: ValidationNel<E, A>): ValidationNel<E, B> =
        this.appliedOver(v)

/**
 * An infix symbol for ap.
 */
infix fun <E, A, B> ValidationNel<E, (A) -> B>.appliedOver(v: ValidationNel<E, A>): ValidationNel<E, B> {
    val applicative: Applicative<Kind1<Validation.ValidationProxy, NonEmptyList<E>>> = Validation.applicativeNel()
    return applicative.ap(v, this).narrow()
}   // appliedOver
