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

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.Nil
import com.adt.kotlin.kats.data.immutable.list.List.Cons
import com.adt.kotlin.kats.data.immutable.list.ListBuffer
import com.adt.kotlin.kats.data.immutable.list.ListBufferIF



object EitherF {

    /**
     * Factory functions to create the base instances.
     */
    fun <A, B> left(a: A): Either<A, B> = Left(a)
    fun <A, B> right(b: B): Either<A, B> = Right(b)

    /**
     * Extract from a list of either all the left elements. The elements
     *   are extracted in order.
     *
     * Examples:
     *   lefts([Left("ken"), Left("john"), Right(3), Right(7), Left("jessie)]) == ["ken", "john", "Jessie"]
     */
    fun <A, B> lefts(list: List<Either<A, B>>): List<A> {
        fun recLefts(list: List<Either<A, B>>, acc: ListBufferIF<A>): List<A> {
            return when (list) {
                is Nil -> acc.toList()
                is Cons -> {
                    val eab: Either<A, B> = list.head()
                    when (eab) {
                        is Left -> recLefts(list.tail(), acc.append(eab.value))
                        is Right -> recLefts(list.tail(), acc)
                    }
                }
            }
        }   // recLefts

        return recLefts(list, ListBuffer())
    }   // lefts

    /**
     * Extract from a list of either all the right elements. The elements
     *   are extracted in order.
     *
     * Examples:
     *   rights([Left("ken"), Left("john"), Right(3), Right(7), Left("jessie)]) == [3, 7]
     */
    fun <A, B> rights(list: List<Either<A, B>>): List<B> {
        fun recRights(list: List<Either<A, B>>, acc: ListBufferIF<B>): List<B> {
            return when (list) {
                is Nil -> acc.toList()
                is Cons -> {
                    val eab: Either<A, B> = list.head()
                    when (eab) {
                        is Left -> recRights(list.tail(), acc)
                        is Right -> recRights(list.tail(), acc.append(eab.value))
                    }
                }
            }
        }   // recRights

        return recRights(list, ListBuffer())
    }   // rights

    /**
     * Partition a list of either into two lists. All the left elements are
     *   extracted, in order, to the first component of the output. Similarly
     *   the right elements are extracted to the second component of the output.
     *
     * Examples:
     *   partition([Left("ken"), Left("john"), Right(3), Right(7), Left("jessie)]) == (["ken", "john", "Jessie"], [3, 7])
     */
    fun <A, B> partition(list: List<Either<A, B>>): Pair<List<A>, List<B>> {
        fun recPartition(list: List<Either<A, B>>, accLeft: ListBufferIF<A>, accRight: ListBufferIF<B>): Pair<List<A>, List<B>> {
            return when (list) {
                is Nil -> Pair(accLeft.toList(), accRight.toList())
                is Cons -> {
                    val eab: Either<A, B> = list.head()
                    when (eab) {
                        is Left -> recPartition(list.tail(), accLeft.append(eab.value), accRight)
                        is Right -> recPartition(list.tail(), accLeft, accRight.append(eab.value))
                    }
                }
            }
        }   // recPartition

        return recPartition(list, ListBuffer(), ListBuffer())
    }   // partition

}   // EitherF
