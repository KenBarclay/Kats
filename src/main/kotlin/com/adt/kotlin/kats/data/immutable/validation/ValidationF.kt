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
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyListF
import com.adt.kotlin.kats.data.immutable.validation.Validation.Failure
import com.adt.kotlin.kats.data.immutable.validation.Validation.Success


object ValidationF {

    /**
     * Factory constructor functions.
     */
    fun <E, A> failure(err: E): Failure<E, A> = Failure(err)
    fun <E, A> success(a: A): Success<E, A> = Success(a)

    fun <E, A> failureNel(e: E): ValidationNel<E, A> = Failure(NonEmptyListF.singleton(e))
    fun <E, A> failureNel(nel: NonEmptyList<E>): ValidationNel<E, A> = Failure(nel)
    fun <E, A> successNel(a: A): ValidationNel<E, A> = Success(a)

}   // ValidationF
