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

import com.adt.kotlin.kats.data.immutable.tri.Try.Failure
import com.adt.kotlin.kats.data.immutable.tri.Try.Success
import com.adt.kotlin.kats.hkfp.typeclass.Applicative


/**
 * Since class Try<A> is the only implementation for Kind1<TryProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A>TryOf<A>.narrow(): Try<A> = this as Try<A>


// Contravariant extension functions:

/**
 * Return the value if this is a Success or the given default argument if this
 *   is a Failure.
 *
 * Examples:
 *   success(123).getOrElse(100) == 123
 *   failure(Exception("error")).getOrElse(100) == 100
 *
 * @param defaultValue      return value if this is a Failure
 * @return                  the value wrapped by this Success or the given default
 */
fun <A> Try<A>.getOrElse(defaultValue: A): A {
    return when (this) {
        is Failure -> defaultValue
        is Success -> this.value
    }
}   // getOrElse

/**
 * Apply the given function f if this is a Failure, otherwise returns this if
 *   this is a Success.
 *
 * Examples:
 *   success(123).recoverWith{_ -> success(456)} == success(123)
 *   failure(Exception("error")).recoverWith{_ -> success(456)} == success(456)
 */
fun <A> Try<A>.recoverWith(f: (Throwable) -> Try<A>): Try<A> =
        this.fold({th: Throwable -> f(th)}, {a: A -> Success(a)})



// Functor extension functions:

/**
 * An infix symbol for fmap.
 */
infix fun <A, B> ((A) -> B).dollar(v: Try<A>): Try<B> = v.map(this)



// Applicative extension functions:

/**
 * An infix symbol for ap.
 */
infix fun <A, B> Try<(A) -> B>.apply(v: Try<A>): Try<B> = this.appliedOver(v)

/**
 * An infix symbol for ap.
 */
infix fun <A, B> Try<(A) -> B>.appliedOver(v: Try<A>): Try<B> {
    val tryApplicative: Applicative<Try.TryProxy> = Try.applicative()
    return tryApplicative.ap(v, this).narrow()
}   // appliedOver
