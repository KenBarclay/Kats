package com.adt.kotlin.kats.data.immutable.either

/**
 * Either[A, B] = Left of A
 *              | Right of B
 *
 * This Either type is inspired by the Haskell Either data type. The Either type represents
 *   values with two possibilities: a value of type Either[A, B] is either Left[A] or Right[B].
 *
 * The Either type is sometimes used to represent a value which is either correct or an error;
 *   by convention, the Left constructor is used to hold an error value and the Right constructor
 *   is used to hold a correct value (mnemonic: "right" also means "correct").
 *
 * This Either type is right-biased, so functions such as map and bind apply only to the Right
 *   case. This right-bias makes this Either more convenient to use in a monadic context than
 *   the either/Either type avoiding the need for a right projection.
 *
 * @param A                     the type of Left elements
 * @param B                     the type of Right elements
 *
 * @author	                    Ken Barclay
 * @since	                    October 2019
 */

import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right



/**
 * Since class Either<A, B> is the only implementation for Kind1<Kind1<EitherProxy, A>, B>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A, B> EitherOf<A, B>.narrow(): Either<A, B> = this as Either<A, B>



// Contravariant extension functions:

/**
 * Apply the function wrapped in the Either  context to the content of the
 *   value also wrapped in an Either context.
 *
 * Examples:
 *   left("Ken").ap(right{n: Int -> n + 1}) == left("Ken")
 *   right(2).ap(right{n: Int -> n + 1}) == right(3)
 */
fun <A, B, C> Either<A, B>.ap(f: Either<A, (B) -> C>): Either<A, C> =
        when (f) {
            is Left -> Left(f.value)
            is Right -> this.map(f.value)
        }   // ap

/**
 * Sequentially compose two actions, passing any value produced by the first
 *   as an argument to the second.
 *
 * Examples:
 *   left("Ken").bind{n: Int -> right(n + 1)} == left("Ken")
 *   right(2).bind{n: Int -> right(n + 1)} == right(3)
 */
fun <A, B, C> Either<A, B>.bind(f: (B) -> Either<A, C>): Either<A, C> =
        when (this) {
            is Left -> Left(this.value)
            is Right -> f(this.value)
        }   // bind

fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> = this.bind(f)

/**
 * Obtain the value of the Left (if it is one), otherwise return the
 *   default value.
 *
 * Examples:
 *   Left("Ken").getLeftOrElse("DEFAULT") == "Ken"
 *   Right(2).getLeftOrElse("DEFAULT") == "DEFAULT"
 */
fun <A, B> Either<A, B>.getLeftOrElse(defaultvalue: A): A {
    return when (this) {
        is Left -> this.value
        is Right -> defaultvalue
    }
}

/**
 * Obtain the value of the Right (if it is one), otherwise return the
 *   default value.
 *
 * Examples:
 *   Left("Ken").getRightOrElse(0) = 0
 *   Right(2).getRightOrElse(0) = 2
 */
fun <A, B> Either<A, B>.getRightOrElse(defaultvalue: B): B {
    return when (this) {
        is Left -> defaultvalue
        is Right -> this.value
    }
}
