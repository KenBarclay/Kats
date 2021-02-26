package com.adt.kotlin.kats.data.immutable.nel

/**
 * A singly-linked list that is guaranteed to be non-empty. A data type which
 *   represents a non empty list, with single element (hd) and optional
 *   structure (tl).
 *
 * The documentation uses the notation [x0 :| x1, x2, ...] to represent a
 *   list instance.
 *
 * @param A                     the (covariant) type of elements in the list
 *
 * @author	                    Ken Barclay
 * @since                       October 2019
 */

import com.adt.kotlin.kats.data.immutable.list.*
import com.adt.kotlin.kats.data.immutable.list.List

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.hkfp.typeclass.Applicative

import kotlin.math.max
import kotlin.math.min



/**
 * Since class NonEmptyList<A> is the only implementation for Kind1<NonEmptyListProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A> NonEmptyListOf<A>.narrow(): NonEmptyList<A> = this as NonEmptyList<A>



/**
 * Functions to support an applicative style of programming.
 *
 * Examples:
 *   {a: A -> ... B value} fmap listA ==> List<B>
 *   {a: A -> {b: B -> ... C value}} fmap listA ==> List<(B) -> C>
 *   {a: A -> {b: B -> ... C value}} fmap listA appliedOver listB ==> List<C>
 *
infix fun <A, B> ((A) -> B).fmap(list: NonEmptyList<A>): NonEmptyList<B> =
list.fmap(this)

infix fun <A, B> NonEmptyList<(A) -> B>.appliedOver(list: NonEmptyList<A>): NonEmptyList<B> =
list.ap(this)
 *****/



// ---------- special lists -------------------------------

/**
 * Translate a list of characters into a string
 *
 * @return                      the resulting string
 */
fun NonEmptyList<Char>.charsToString(): String {
    val buffer: StringBuffer = this.foldLeft(StringBuffer()){res -> {ch -> res.append(ch)}}
    return buffer.toString()
}   // charsToString

/**
 * 'and' returns the conjunction of a container of booleans.
 *
 * @return                      true, if all the elements are true
 */
fun NonEmptyList<Boolean>.and(): Boolean =
        this.forAll{bool -> (bool == true)}

/**
 * 'or' returns the disjunction of a container of booleans.
 *
 * @return                      true, if any of the elements is true
 */
fun NonEmptyList<Boolean>.or(): Boolean =
        this.thereExists{bool -> (bool == true)}

/**
 * The sum function computes the sum of the integers in a list.
 *
 * @return                      the sum of all the elements
 */
fun NonEmptyList<Int>.sum(): Int =
        this.foldLeft(0){n, m -> n + m}

/**
 * The sum function computes the sum of the doubles in a list.
 *
 * @return                      the sum of all the elements
 */
fun NonEmptyList<Double>.sum(): Double =
        this.foldLeft(0.0){x, y -> x + y}

/**
 * The product function computes the product of the integers in a list.
 *
 * @return                      the product of all the elements
 */
fun NonEmptyList<Int>.product(): Int =
        this.foldLeft(1){n, m -> n * m}

/**
 * The product function computes the product of the doubles in a list.
 *
 * @return                      the product of all the elements
 */
fun NonEmptyList<Double>.product(): Double =
        this.foldLeft(1.0){x, y -> x * y}

/**
 * Find the largest integer in a list of integers.
 *
 * @return                      the maximum integer in the list
 */
fun NonEmptyList<Int>.max(): Int =
        max(this.head(), this.tail().max())

/**
 * Find the smallest integer in a list of integers.
 *
 * @return                      the minumum integer in the list
 */
fun NonEmptyList<Int>.min(): Int =
        min(this.head(), this.tail().min())

/**
 * Find the largest double in a list of doubles.
 *
 * @return                      the maximum double in the list
 */
fun NonEmptyList<Double>.max(): Double =
        max(this.head(), this.tail().max())

/**
 * Find the smallest double in a list of doubles.
 *
 * @return                      the minumum double in the list
 */
fun NonEmptyList<Double>.min(): Double =
        min(this.head(), this.tail().min())



// Contravariant extension functions:

/**
 * Append a single element on to this list. The size of the result list
 *   will be one more than the size of this list. The last element in the
 *   result list will equal the appended element. This list will be a prefix
 *   of the result list.
 *
 * Examples:
 *   [1 :| 2, 3, 4].append(5) == [1 :| 2, 3, 4, 5]
 *   [1 :| 2, 3, 4].append(5).size() == 1 + [1 :| 2, 3, 4].size()
 *   [1 :| 2, 3, 4].append(5).last() == 5
 *
 * @param x                 new element
 * @return                  new list with element at end
 */
fun <A> NonEmptyList<A>.append(x: A): NonEmptyList<A> =
        NonEmptyList(this.toList().append(x))

/**
 * Append the given list on to this list. The size of the result list
 *   will equal the sum of the sizes of this list and the parameter
 *   list. This list will be a prefix of the result list and the
 *   parameter list will be a suffix of the result list.
 *
 * Examples:
 *   [1 :| 2, 3].append([4, 5]) == [1 :| 2, 3, 4, 5]
 *   [1 :| 2, 3].append([]) == [1 :| 2, 3]
 *   [1 :| 2, 3].append([4, 5]).size() == [1 :| 2, 3].size() + [4 :| 5].size()
 *   [1 :| 2, 3].isPrefixOf([1, 2, 3].append([4, 5])) == true
 *   [4 :| 5].isSuffixOf([1, 2, 3].append([4, 5])) == true
 *
 * @param xs                existing list
 * @return                  new list of appended elements
 */
fun <A> NonEmptyList<A>.append(list: List<A>): NonEmptyList<A> =
        NonEmptyList(hd, tl.append(list))

fun <A> NonEmptyList<A>.append(list: NonEmptyList<A>): NonEmptyList<A> =
        NonEmptyList(hd, tl.append(list.toList()))

/**
 * Append the given list on to this list. The size of the result list
 *   equals the sum of the size of this list and the list parameter.
 *   This list is a prefix of the result list and the parameter list
 *   is a suffix of the result list.
 *
 * Examples:
 *   [1 :| 2].concatenate([3, 4]) == [1 :| 2, 3, 4]
 *
 * @param xs                existing list
 * @return                  new list of appended elements
 */
fun <A> NonEmptyList<A>.concatenate(xs: NonEmptyList<A>): NonEmptyList<A> =
        this.append(xs)

fun <A> NonEmptyList<A>.concatenate(xs: List<A>): NonEmptyList<A> =
        this.append(xs)

/**
 * Determine if this list contains the given element.
 *
 * Examples:
 *   [1 :| 2, 3, 4].contains(4) == true
 *   [1 :| 2, 3, 4].contains(5) == false
 *
 * @param x                 search element
 * @return                  true if search element is present, false otherwise
 */
fun <A> NonEmptyList<A>.contains(x: A): Boolean =
        this.toList().contains(x)

/**
 * Count the number of times the parameter appears in this list.
 *
 * Examples:
 *   [1 :| 2, 3, 4].count(2) == 1
 *   [1 :| 2, 3, 4].count(5) == 0
 *   [1 :| 2, 1, 2, 2].count(2) == 3
 *
 * @param x                 the search value
 * @return                  the number of occurrences
 */
fun <A> NonEmptyList<A>.count(x: A): Int =
        this.toList().count(x)

/**
 * Find the index of the given value, or -1 if absent.
 *
 * Examples:
 *   [1 :| 2, 3, 4].indexOf(1) = 0
 *   [1 :| 2, 3, 4].indexOf(3) = 2
 *   [1 :| 2, 3, 4].indexOf(5) = -1
 *
 * @param x                 the search value
 * @return                  the index position
 */
fun <A> NonEmptyList<A>.indexOf(x: A): Int =
        this.toList().indexOf(x)

/**
 * Interleave this list and the given list, alternating elements from each list.
 *   If either list is empty then an empty list is returned. The first element is
 *   drawn from this list. The size of the result list will equal twice the size
 *   of the smaller list. The elements of the result list are in the same order as
 *   the two original.
 *
 * Examples:
 *   [1 :| 2].interleave([3 :| 4, 5]) == [1 :| 3, 2, 4]
 *
 * @param xs                other list
 * @return                  result list of alternating elements
 */
fun <A> NonEmptyList<A>.interleave(xs: NonEmptyList<A>): NonEmptyList<A> =
        NonEmptyList(this.toList().interleave(xs.toList()))

/**
 * The intersperse function takes an element and intersperses
 *   that element between the elements of this list. If this list
 *   is empty then an empty list is returned. If this list size is
 *   one then this list is returned.
 *
 * Examples:
 *   [1, 2, 3, 4].intersperse(0) == [1, 0, 2, 0, 3, 0, 4]
 *   [1].intersperse(0) == [1]
 *
 * @param separator         separator
 * @return                  new list of existing elements and separators
 */
fun <A> NonEmptyList<A>.intersperse(separator: A): NonEmptyList<A> =
        NonEmptyList(this.toList().intersperse(separator))

/**
 * The isInfixOf function returns true iff this list is a constituent of the argument.
 *
 * Examples:
 *   [2 :| 3].isInfixOf([1 :| 2, 3, 4]) == true
 *   [1 :| 2].isInfixOf([1 :| 2, 3, 4]) == true
 *   [3 :| 4].isInfixOf([1 :| 2, 3, 4]) == true
 *   [3 :| 2].isInfixOf([1 :| 2, 3, 4]) == false
 *   [1 :| 2, 3, 4, 5].isInfixOf([1 :| 2, 3, 4]) == false
 *
 * @param xs                existing list
 * @return                  true if this list is constituent of second list
 */
fun <A> NonEmptyList<A>.isInfixOf(xs: NonEmptyList<A>): Boolean =
        this.toList().isInfixOf(xs.toList())

/**
 * Return true if this list has the same content as the given list, respecting
 *   the order.
 *
 * Examples:
 *   [1 :| 2, 3, 4].isOrderedPermutationOf([1 :| 2, 3, 4]) == true
 *   [1 :| 4].isOrderedPermutationOf([1 :| 2, 3, 4]) == true
 *   [1 :| 2, 3].isOrderedPermutationOf([1 :| 1, 2, 1, 2, 4, 3, 4]) == true
 *   [1 :| 2, 3].isOrderedPermutationOf([1 :| 1, 3, 1, 4, 3, 3, 4]) == false
 *
 * @param ys                comparison list
 * @return                  true if this list has the same content as the given list; otherwise false
 */
fun <A> NonEmptyList<A>.isOrderedPermutationOf(ys: NonEmptyList<A>): Boolean =
        this.toList().isOrderedPermutationOf(ys.toList())

/**
 * Return true if this list has the same content as the given list, regardless
 *   of order.
 *
 * Examples:
 *   [1 :| 2, 3, 4].isPermutationOf([1 :| 2, 3, 4]) == true
 *   [1 :| 2, 3, 4].isPermutationOf([5 :| 4, 3, 2, 1]) == true
 *   [5 :| 4, 3, 2, 1].isPermutationOf([1 :| 2, 3, 4]) == false
 *
 * @param ys                comparison list
 * @return                  true if this list has the same content as the given list; otherwise false
 */
fun <A> NonEmptyList<A>.isPermutationOf(ys: NonEmptyList<A>): Boolean =
        this.toList().isPermutationOf(ys.toList())

/**
 * The isPrefixOf function returns true iff this list is a prefix of the second.
 *
 * Examples:
 *   [1 :| 2].isPrefixOf([1 :| 2, 3, 4]) == true
 *   [1 :| 2, 3, 4].isPrefixOf([1 :| 2, 3, 4]) == true
 *   [1 :| 2].isPrefixOf([2 :| 3, 4]) == false
 *
 * @param xs                existing list
 * @return                  true if this list is prefix of given list
 */
fun <A> NonEmptyList<A>.isPrefixOf(xs: NonEmptyList<A>): Boolean =
        this.toList().isPrefixOf(xs.toList())

/**
 * The isSuffixOf function takes returns true iff the this list is a suffix of the second.
 *
 * Examples:
 *   [3 :| 4].isSuffixOf([1 :| 2, 3, 4]) == true
 *   [1 :| 2, 3, 4].isSuffixOf([1 :| 2, 3, 4]) == true
 *   [3 :| 4].isSuffixOf([1 :| 2, 3]) == false
 *
 * @param xs                existing list
 * @return                  true if this list is suffix of given list
 */
fun <A> NonEmptyList<A>.isSuffixOf(xs: NonEmptyList<A>): Boolean =
        this.toList().isSuffixOf(xs.toList())

/**
 * Remove the first occurrence of the given element from this list. The result list
 *   will either have the same size as this list (if no such element is present) or
 *   will have the size of this list less one.
 *
 * Examples:
 *   [1 :| 2, 3, 4].remove(4) == [1, 2, 3]
 *   [1 :| 2, 3, 4].remove(5) == [1, 2, 3, 4]
 *   [4 :| 4, 4, 4].remove(4) == [4, 4, 4]
 *
 * @param x                 element to be removed
 * @return                  new list with element deleted
 */
fun <A> NonEmptyList<A>.remove(x: A): List<A> =
        this.toList().remove(x)

/**
 * The stripPrefix function drops this prefix from the given list. It returns
 *   None if the list did not start with this prefix, or Some the
 *   list after the prefix, if it does.
 *
 * Examples:
 *   [1 :| 2].stripPrefix([1 :| 2, 3, 4]) == Some([3 :| 4])
 *   [2 :| 3, 4].stripPrefix([1 :| 2]) == None
 *
 * @param xs                existing list of possible prefix
 * @return                  new list of prefix
 */
fun <A> NonEmptyList<A>.stripPrefix(xs: NonEmptyList<A>): Option<List<A>> =
        this.toList().stripPrefix(xs.toList())



// Functor extension functions:

/**
 * An infix symbol for fmap.
 */
infix fun <A, B> ((A) -> B).dollar(nela: NonEmptyList<A>): NonEmptyList<B> =
        nela.map(this)



// Applicative extension functions:

/**
 * An infix symbol for ap.
 */
infix fun <A, B> NonEmptyList<(A) -> B>.apply(v: NonEmptyList<A>): NonEmptyList<B> {
    val applicative: Applicative<NonEmptyList.NonEmptyListProxy> = NonEmptyList.applicative()
    return applicative.ap(v, this).narrow()
}

/**
 * An infix symbol for ap.
 */
infix fun <A, B> NonEmptyList<(A) -> B>.appliedOver(v: NonEmptyList<A>): NonEmptyList<B> {
    val applicative: Applicative<NonEmptyList.NonEmptyListProxy> = NonEmptyList.applicative()
    return applicative.ap(v, this).narrow()
}   // appliedOver
