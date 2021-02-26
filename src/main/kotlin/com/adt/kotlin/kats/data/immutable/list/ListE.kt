package com.adt.kotlin.kats.data.immutable.list

/**
 * A class hierarchy defining an immutable list collection. The algebraic data
 *   type declaration is:
 *
 * datatype List[A] = Nil
 *                  | Cons of A * List[A]
 *
 * The implementation mimics functional Lists as found in Haskell. The
 *   member functions and the extension functions mostly use primitive
 *   recursion over the List value constructors. Local tail recursive
 *   functions are commonly used.
 *
 * The documentation uses the notation [...] to represent a list instance.
 *
 * @param A                     the (covariant) type of elements in the list
 *
 * @author	                    Ken Barclay
 * @since                       October 2019
 */

import com.adt.kotlin.kats.data.immutable.list.List.Nil
import com.adt.kotlin.kats.data.immutable.list.List.Cons
import com.adt.kotlin.kats.data.immutable.option.Option

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2

import java.util.*
import java.util.stream.Stream as JStream
import java.util.stream.StreamSupport



/**
 * Since class List<A> is the only implementation for Kind1<ListProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A> ListOf<A>.narrow(): List<A> = this as List<A>



/**
 * Functions to support an applicative style of programming.
 *
 * Examples:
 *   {a: A -> ... B value} fmap listA ==> List<B>
 *   {a: A -> {b: B -> ... C value}} fmap listA ==> List<(B) -> C>
 *   {a: A -> {b: B -> ... C value}} fmap listA appliedOver listB ==> List<C>
 */
////infix fun <A, B> ((A) -> B).fmap(list: List<A>): List<B> =
////    list.fmap(this)

////infix fun <A, B> List<(A) -> B>.appliedOver(list: List<A>): List<B> =
////    list.ap(this)



// ---------- special lists -------------------------------

/**
 * Translate a list of characters into a string
 *
 * @return                      the resulting string
 */
fun List<Char>.charsToString(): String {
    val buffer: StringBuffer = this.foldLeft(StringBuffer()){res -> {ch -> res.append(ch)}}
    return buffer.toString()
}

/**
 * 'unlines' joins lines after appending a newline to each.
 *
 * @return                      joined lines
 */
fun List<String>.unlines(): String {
    val buffer: StringBuffer = this.foldLeft(StringBuffer()){res -> {str -> res.append(str).append('\n')}}
    return buffer.toString()
}

/**
 * 'unwords' joins words after appending a space to each.
 *
 * @return                      joined words
 */
fun List<String>.unwords(): String {
    val buf: StringBuffer = this.foldLeft(StringBuffer()){res -> {str -> res.append(str).append(' ')}}
    val buffer: StringBuffer = if (this.size() >= 1) buf.deleteCharAt(buf.length - 1) else buf
    return buffer.toString()
}

/**
 * 'and' returns the conjunction of a container of booleans.
 *
 * @return                      true, if all the elements are true
 */
fun List<Boolean>.and(): Boolean =
    this.forAll{bool -> (bool == true)}

/**
 * 'or' returns the disjunction of a container of booleans.
 *
 * @return                      true, if any of the elements is true
 */
fun List<Boolean>.or(): Boolean =
    this.thereExists{bool -> (bool == true)}

/**
 * The sum function computes the sum of the integers in a list.
 *
 * @return                      the sum of all the elements
 */
fun List<Int>.sum(): Int =
    this.foldLeft(0){n, m -> n + m}

/**
 * The sum function computes the sum of the doubles in a list.
 *
 * @return                      the sum of all the elements
 */
fun List<Double>.sum(): Double =
    this.foldLeft(0.0){x, y -> x + y}

/**
 * The product function computes the product of the integers in a list.
 *
 * @return                      the product of all the elements
 */
fun List<Int>.product(): Int =
    this.foldLeft(1){n, m -> n * m}

/**
 * The product function computes the product of the doubles in a list.
 *
 * @return                      the product of all the elements
 */
fun List<Double>.product(): Double =
    this.foldLeft(1.0){x, y -> x * y}

/**
 * Find the largest integer in a list of integers. Throws a
 *   ListException if the list is empty.
 *
 * @return                      the maximum integer in the list
 */
fun List<Int>.max(): Int {
    tailrec
    fun recMax(xs: List<Int>, acc: Int): Int {
        return if (xs.isEmpty())
            acc
        else
            recMax(xs.tail(), Math.max(acc, xs.head()))
    }   // recMax

    return if (this.isEmpty())
        throw ListException("max: empty list")
    else
        recMax(this.tail(), this.head())
}

/**
 * Find the smallest integer in a list of integers. Throws a
 *   ListException if the list is empty.
 *
 * @return                      the minumum integer in the list
 */
fun List<Int>.min(): Int {
    tailrec
    fun recMin(xs: List<Int>, acc: Int): Int {
        return if (xs.isEmpty())
            acc
        else
            recMin(xs.tail(), Math.min(acc, xs.head()))
    }   // recMin

    return if (this.isEmpty())
        throw ListException("max: empty list")
    else
        recMin(this.tail(), this.head())
}

/**
 * Find the largest double in a list of doubles. Throws a
 *   ListException if the list is empty.
 *
 * @return                      the maximum double in the list
 */
fun List<Double>.max(): Double {
    tailrec
    fun recMax(xs: List<Double>, acc: Double): Double {
        return if (xs.isEmpty())
            acc
        else
            recMax(xs.tail(), Math.max(acc, xs.head()))
    }   // recMax

    return if (this.isEmpty())
        throw ListException("max: empty list")
    else
        recMax(this.tail(), this.head())
}

/**
 * Find the smallest double in a list of doubles. Throws a
 *   ListException if the list is empty.
 *
 * @return                      the minumum double in the list
 */
fun List<Double>.min(): Double {
    tailrec
    fun recMin(xs: List<Double>, acc: Double): Double {
        return if (xs.isEmpty())
            acc
        else
            recMin(xs.tail(), Math.min(acc, xs.head()))
    }   // recMin

    return if (this.isEmpty())
        throw ListException("max: empty list")
    else
        recMin(this.tail(), this.head())
}

/**
 * Generate a random permutation of the elements of this list.
 */
fun <A> List<A>.shuffle(): List<A> =
    if (this.size() < 2)
        this
    else {
        val rnd: Random = Random()
        val idx: Int = rnd.nextInt(this.size())
        val r: List<A> = this.take(idx).append(this.drop(1 + idx)).shuffle()
        Cons(this[idx], r)
    }   // shuffle



// Contravariant extension functions:

/**
 * Append a single element on to this list. The size of the result list
 *   will be one more than the size of this list. The last element in the
 *   result list will equal the appended element. This list will be a prefix
 *   of the result list.
 *
 * Examples:
 *   [1, 2, 3, 4].append(5) == [1, 2, 3, 4, 5]
 *   [1, 2, 3, 4].append(5).size() == 1 + [1, 2, 3, 4].size()
 *   [1, 2, 3, 4].append(5).last() == 5
 *   [1, 2, 3, 4].isPrefix([1, 2, 3, 4].append(5)) == true
 *
 * @param element           new element
 * @return                  new list with element at end
 */
fun <A> List<A>.append(element: A): List<A> {
    tailrec
    fun recAppend(element: A, list: List<A>, acc: ListBufferIF<A>): List<A> {
        return when(list) {
            is Nil -> acc.append(element).toList()
            is Cons -> recAppend(element, list.tail(), acc.append(list.head()))
        }
    }   // recAppend

    return recAppend(element, this, ListBuffer())
}   // append

operator fun <A> List<A>.plus(element: A): List<A> = this.append(element)

/**
 * Append the given list on to this list. The size of the result list
 *   will equal the sum of the sizes of this list and the parameter
 *   list. This list will be a prefix of the result list and the
 *   parameter list will be a suffix of the result list.
 *
 * Examples:
 *   [1, 2, 3].append([4, 5]) == [1, 2, 3, 4, 5]
 *   [1, 2, 3].append([]) == [1, 2, 3]
 *   [].append([3, 4]) == [3, 4]
 *   [1, 2, 3].append([4, 5]).size() == [1, 2, 3].size() + [4, 5].size()
 *   [1, 2, 3].isPrefixOf([1, 2, 3].append([4, 5])) == true
 *   [4, 5].isSuffixOf([1, 2, 3].append([4, 5])) == true
 *
 * @param list              existing list
 * @return                  new list of appended elements
 */
fun <A> List<A>.append(list: List<A>): List<A> {
    tailrec
    fun recAppend(ps: List<A>, qs: List<A>, acc: ListBufferIF<A>): List<A> {
        return when(ps) {
            is Nil -> acc.prependTo(qs)
            is Cons -> recAppend(ps.tl, qs, acc.append(ps.hd))
        }
    }   // recAppend

    return recAppend(this, list, ListBuffer())
}   // append

operator fun <A> List<A>.plus(list: List<A>): List<A> = this.append(list)

/**
 * Append the given list on to this list. The size of the result list
 *   equals the sum of the size of this list and the list parameter.
 *   This list is a prefix of the result list and the parameter list
 *   is a suffix of the result list.
 *
 * Examples:
 *   [1, 2].concatenate([3, 4]) == [1, 2, 3, 4]
 *   [1, 2, 3, 4].concatenate([]) == [1, 2, 3, 4]
 *   [].concatenate([1, 2, 3, 4]) == [1, 2, 3, 4]
 *   [].concatenate([]) = []
 *
 * @param list              existing list
 * @return                  new list of appended elements
 */
fun <A> List<A>.concatenate(list: List<A>): List<A> = this.append(list)

/**
 * Determine if this list contains the given element.
 *
 * Examples:
 *   [1, 2, 3, 4].contains(4) == true
 *   [1, 2, 3, 4].contains(5) == false
 *   [].contains(4) == false
 *
 * @param element           search element
 * @return                  true if search element is present, false otherwise
 */
fun <A> List<A>.contains(element: A): Boolean = this.contains{ x: A -> (x == element)}

/**
 * Count the number of times the parameter appears in this list.
 *
 * Examples:
 *   [1, 2, 3, 4].count(2) == 1
 *   [1, 2, 3, 4].count(5) == 0
 *   [].count(2) == 0
 *   [1, 2, 1, 2, 2].count(2) == 3
 *
 * @param element           the search value
 * @return                  the number of occurrences
 */
fun <A> List<A>.count(element: A): Int = this.count{ x: A -> (x == element)}

/**
 * A variant of foldLeft that has no starting value argument, and thus must
 *   be applied to non-empty lists. The initial value is used as the start
 *   value. Throws a ListException on an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].foldLeft1{m -> {n -> m + n}} == 10
 *
 * @param f                 curried binary function:: A -> A -> A
 * @return                  folded result
 */
fun <A> List<A>.foldLeft1(f: (A) -> (A) -> A): A = when(this) {
    is Nil -> throw ListException("foldLeft1: empty list")
    is Cons -> this.tail().foldLeft(this.head(), f)
}   // foldLeft1

/**
 * A variant of foldLeft that has no starting value argument, and thus must
 *   be applied to non-empty lists. The initial value is used as the start
 *   value. Throws a ListException on an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].foldLeft1{m, n -> m + n} == 10
 *
 * @param f                 uncurried binary function:: A -> A -> A
 * @return                  folded result
 */
fun <A> List<A>.foldLeft1(f: (A, A) -> A): A = this.foldLeft1(C2(f))

/**
 * A variant of foldRight that has no starting value argument, and thus must
 *   be applied to non-empty lists. The initial value is used as the start
 *   value. Throws a ListException on an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].foldRight1{m -> {n -> m * n}} == 24
 *
 * @param f                 curried binary function:: A -> A -> A
 * @return                  folded result
 */
fun <A> List<A>.foldRight1(f: (A) -> (A) -> A): A = when(this) {
    is Nil -> throw ListException("foldRight1: empty list")
    is Cons -> this.tail().foldRight(this.head(), f)
}   // foldRight1

/**
 * A variant of foldRight that has no starting value argument, and thus must
 *   be applied to non-empty lists. The initial value is used as the start
 *   value. Throws a ListException on an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].foldRight1{m, n -> m * n} == 24
 *
 * @param f                 uncurried binary function:: A -> A -> A
 * @return                  folded result
 */
fun <A> List<A>.foldRight1(f: (A, A) -> A): A = this.foldRight1(C2(f))

/**
 * Find the index of the given value, or -1 if absent.
 *
 * Examples:
 *   [1, 2, 3, 4].indexOf(1) == 0
 *   [1, 2, 3, 4].indexOf(3) == 2
 *   [1, 2, 3, 4].indexOf(5) == -1
 *   [].indexOf(2) == -1
 *
 * @param element           the search value
 * @return                  the index position
 */
fun <A> List<A>.indexOf(element: A): Int = this.indexOf{ x -> (x == element)}

/**
 * Insert a new element at the given index position. The new element
 *   can be inserted at the end of the list. Throws an exception
 *   if an illegal index is used.
 *
 * @param index             index position for the new element
 * @param element           the new element to insert
 * @return                  a new list
 */
fun <A> List<A>.insert(index: Int, element: A): List<A> {
    fun recInsert(list: List<A>, index: Int, element: A): List<A> {
        return when (list) {
            is Nil -> if (index == 0) Cons(element, Nil) else throw ListException("List.insert: index longer than list")
            is Cons -> if (index == 0) Cons(element, list) else Cons(list.hd, recInsert(list.tl, index - 1, element))
        }
    }   // recInsert

    return if (index < 0)
        throw ListException("List.insert: invalid index: $index")
    else
        recInsert(this, index, element)
}   // insert

/**
 * Insert the elements at the given index position. The new elements
 *   can be inserted at the end of the list. Throws an exception
 *   if an illegal index is used.
 *
 * @param index             index position for the new elements
 * @param elements          the new elements to insert
 * @return                  a new list
 */
fun <A> List<A>.insert(index: Int, elements: List<A>): List<A> {
    fun recInsert(list: List<A>, index: Int, elements: List<A>): List<A> {
        return when (list) {
            is Nil -> if (index == 0) elements else throw ListException("List.insert: index longer than list")
            is Cons -> if (index == 0) elements.append(list) else Cons(list.hd, recInsert(list.tl, index - 1, elements))
        }
    }   // recInsert

    return if (index < 0)
        throw ListException("List.insert: invalid index: $index")
    else
        recInsert(this, index, elements)
}   // insert

/**
 * Interleave this list and the given list, alternating elements from each list.
 *   If either list is empty then an empty list is returned. The first element is
 *   drawn from this list. The size of the result list will equal twice the size
 *   of the smaller list. The elements of the result list are in the same order as
 *   the two original.
 *
 * Examples:
 *   [].interleave([]) == []
 *   [].interleave([3, 4, 5]) == []
 *   [1, 2].interleave([]) == []
 *   [1, 2].interleave([3, 4, 5]) == [1, 3, 2, 4]
 *
 * @param xs                other list
 * @return                  result list of alternating elements
 */
fun <A> List<A>.interleave(xs: List<A>): List<A> {
    tailrec
    fun recInterleave(ps: List<A>, qs: List<A>, acc: ListBufferIF<A>): List<A> {
        return when(ps) {
            is Nil -> acc.toList()
            is Cons -> {
                when(qs) {
                    is List.Nil -> acc.toList()
                    is List.Cons -> recInterleave(ps.tail(), qs.tail(), acc.append(ps.head()).append(qs.head()))
                }
            }
        }
    }   // recInterleave

    return recInterleave(this, xs, ListBuffer<A>())
}   // interleave

/**
 * The intersperse function takes an element and intersperses
 *   that element between the elements of this list. If this list
 *   is empty then an empty list is returned. If this list size is
 *   one then this list is returned.
 *
 * Examples:
 *   [1, 2, 3, 4].intersperse(0) == [1, 0, 2, 0, 3, 0, 4]
 *   [1].intersperse(0) == [1]
 *   [].intersperse(0) == []
 *
 * @param separator         separator
 * @return                  new list of existing elements and separators
 */
fun <A> List<A>.intersperse(separator: A): List<A> {
    tailrec
    fun recIntersperse(sep: A, ps: List<A>, acc: ListBufferIF<A>): List<A> {
        return when(ps) {
            is Nil -> acc.toList()
            is Cons -> recIntersperse(sep, ps.tail(), acc.append(sep).append(ps.head()))
        }
    }   // recIntersperse

    return when(this) {
        is Nil -> Nil
        is Cons -> if (this.size() == 1) Cons(this.head(), Nil) else Cons(this.head(), recIntersperse(separator, this.tail(), ListBuffer()))
    }
}   // intersperse

/**
 * The isInfixOf function returns true iff this list is a constituent of the argument.
 *
 * Examples:
 *   [2, 3].isInfixOf([]) == false
 *   [2, 3].isInfixOf([1, 2, 3, 4]) == true
 *   [1, 2].isInfixOf([1, 2, 3, 4]) == true
 *   [3, 4].isInfixOf([1, 2, 3, 4]) == true
 *   [].isInfixOf([1, 2, 3, 4]) == true
 *   [3, 2].isInfixOf([1, 2, 3, 4]) == false
 *   [1, 2, 3, 4, 5].isInfixOf([1, 2, 3, 4]) == false
 *
 * @param xs                existing list
 * @return                  true if this list is constituent of second list
 */
fun <A> List<A>.isInfixOf(xs: List<A>): Boolean {
    val isPrefix: (List<A>) -> (List<A>) -> Boolean = { ps -> { qs -> ps.isPrefixOf(qs)}}
    return xs.tails().thereExists(isPrefix(this))
}   // isInfixOf

/**
 * Is this list a mirror of the list parameter?
 *
 * Examples:
 *   [1, 4, 7, 9, 7, 4, 1].isMirror([1, 4, 7, 9, 7, 4, 1]) == true
 *   [1, 4, 7, 7, 4, 1].isMirror([1, 4, 7, 7, 4, 1]) == true
 *   [1, 4, 7, 9].isMirror([9, 7, 4, 1]) == true
 *   [1, 4, 6, 9].isMirror([9, 7, 4, 1]) == false
 *   [1, 4, 7, 9].isMirror([4, 1]) == false
 *   [1, 4].isMirror([9, 7, 4, 1]) == false
 *   [1, 4].isMirror([4, 1]) == true
 */
fun <A> List<A>.isMirror(list: List<A>): Boolean =
    (this.reverse() == list)

/**
 * Is this list palindromic, reading the same forwards and backwards?
 *
 * Examples:
 *   [1, 4, 7, 9, 7, 4, 1].isPalindrome() == true
 *   [1, 4, 7, 7, 4, 1].isPalindrome() == true
 *   [4, 4].isPalindrome() == true
 *   [4].isPalindrome() == true
 *   [1, 4, 7, 6, 4, 1].isPalindrome() == false
 */
fun <A> List<A>.isPalindrome(): Boolean {
    fun recIsPalindrome(list: List<A>, start: Int, end: Int): Boolean {
        return if (end < start)
            true
        else if (list[start] != list[end])
            false
        else
            recIsPalindrome(list, start + 1, end - 1)
    }   // recIsPalindrome

    return recIsPalindrome(this, 0, this.size() - 1)
}   // isPalindrome

/**
 * Return true if this list has the same content as the given list, regardless
 *   of order.
 *
 * Examples:
 *   [1, 2, 3, 4].isPermutationOf([1, 2, 3, 4]) == true
 *   [].isPermutationOf([1, 2, 3, 4]) == true
 *   [].isPermutationOf([]) == true
 *   [1, 2, 3, 4].isPermutationOf([]) == false
 *   [1, 2, 3, 4].isPermutationOf([5, 4, 3, 2, 1]) == true
 *   [5, 4, 3, 2, 1].isPermutationOf([1, 2, 3, 4]) == false
 *
 * @param ys                comparison list
 * @return                  true if this list has the same content as the given list; otherwise false
 */
fun <A> List<A>.isPermutationOf(ys: List<A>): Boolean = this.forAll{ x -> ys.contains(x)}

/**
 * The isPrefixOf function returns true iff this list is a prefix of the parameter list.
 *
 * Examples:
 *   [1, 2].isPrefixOf([1, 2, 3, 4]) == true
 *   [1, 2, 3, 4].isPrefixOf([1, 2, 3, 4]) == true
 *   [1, 2].isPrefixOf([2, 3, 4]) == false
 *   [1, 2].isPrefixOf([]) == false
 *   [].isPrefixOf([1, 2]) == true
 *   [].isPrefixOf([]) == true
 *
 * @param xs                existing list
 * @return                  true if this list is prefix of given list
 */
fun <A> List<A>.isPrefixOf(xs: List<A>): Boolean {
    tailrec
    fun recIsPrefixOf(ps: List<A>, qs: List<A>): Boolean {
        return when(ps) {
            is Nil -> true
            is Cons -> {
                when(qs) {
                    is Nil -> false
                    is Cons -> if (ps.head() != qs.head()) false else recIsPrefixOf(ps.tail(), qs.tail())
                }
            }
        }
    }   // recIsPrefixOf

    return recIsPrefixOf(this, xs)
}   // isPrefixOf

/**
 * Return true if this list has the same content as the given list, respecting
 *   the order.
 *
 * Examples:
 *   [1, 2, 3, 4].isOrderedPermutationOf([1, 2, 3, 4]) == true
 *   [1, 2, 3, 4].isOrderedPermutationOf([]) == false
 *   [].isOrderedPermutationOf([1, 2, 3, 4]) == true
 *   [].isOrderedPermutationOf([]) == true
 *   [1, 4].isOrderedPermutationOf([1, 2, 3, 4]) == true
 *   [1, 2, 3].isOrderedPermutationOf([1, 1, 2, 1, 2, 4, 3, 4]) == true
 *   [1, 2, 3].isOrderedPermutationOf([1, 1, 3, 1, 4, 3, 3, 4]) == false
 *
 * @param ys                comparison list
 * @return                  true if this list has the same content as the given list; otherwise false
 */
fun <A> List<A>.isOrderedPermutationOf(ys: List<A>): Boolean {
    tailrec
    fun recIsOrderedPermutationOf(xs: List<A>, ys: List<A>): Boolean {
        return when(xs) {
            is Nil -> true
            is Cons -> {
                val xHead: A = xs.head()
                val xTail: List<A> = xs.tail()
                val index: Int = ys.indexOf(xHead)
                if (index < 0)
                    false
                else
                    recIsOrderedPermutationOf(xTail, ys.drop(1 + index))
            }
        }
    }   // recIsOrderedPermutationOf

    return recIsOrderedPermutationOf(this, ys)
}   // isOrderedPermutationOf

/**
 * The isSuffixOf function takes returns true iff this list is a suffix of the second.
 *
 * Examples:
 *   [3, 4].isSuffixOf([1, 2, 3, 4]) == true
 *   [1, 2, 3, 4].isSuffixOf([1, 2, 3, 4]) == true
 *   [3, 4].isSuffixOf([1, 2, 3]) == false
 *   [].isSuffixOf([1, 2, 3, 4]) == true
 *   [1, 2, 3, 4].isSuffixOf([]) == false
 *
 * @param xs                existing list
 * @return                  true if this list is suffix of given list
 */
fun <A> List<A>.isSuffixOf(xs: List<A>): Boolean {
    return this.reverse().isPrefixOf(xs.reverse())
}   // isSuffixOf

/**
 * Find the last index of the given value, or -1 if absent.
 *
 * Examples:
 *   [1, 2, 3, 4].lastIndexOf(1) == 0
 *   [1, 2, 3, 4].lastIndexOf(3) == 2
 *   [1, 2, 3, 4].lastIndexOf(5) == -1
 *   [].lastIndexOf(2) == -1
 *
 * @param element           the search value
 * @return                  the index position
 */
fun <A> List<A>.lastIndexOf(element: A): Int = this.lastIndexOf{ x -> (x == element)}

/**
 * Remove the first occurrence of the given element from this list. The result list
 *   will either have the same size as this list (if no such element is present) or
 *   will have the size of this list less one.
 *
 * Examples:
 *   [1, 2, 3, 4].remove(4) == [1, 2, 3]
 *   [1, 2, 3, 4].remove(5) == [1, 2, 3, 4]
 *   [4, 4, 4, 4].remove(4) == [4, 4, 4]
 *   [].remove(4) == []
 *
 * @param x                 element to be removed
 * @return                  new list with element deleted
 */
fun <A> List<A>.remove(x: A): List<A> = this.remove{ a: A -> (x == a)}

/**
 * The removeAll function removes all the elements from this list that match
 *   a given value. The result list size will not exceed this list size.
 *
 * Examples:
 *   [1, 2, 3, 4].removeAll(2) == [1, 3, 4]
 *   [1, 2, 3, 4].removeAll(5) == [1, 2, 3, 4]
 *   [].removeAll(4) == []
 *   [1, 4, 2, 3, 4].removeAll(4) == [1, 2, 3]
 *   [4, 4, 4, 4, 4].removeAll(4) == []
 *
 * @param predicate		    criteria
 * @return          		new list with all matching elements removed
 */
fun <A> List<A>.removeAll(x: A): List<A> = this.removeAll{ a: A -> (x == a)}

/**
 * Replace the first value if found in the list with the second value.
 *
 * @param a                     the value to be replaced if present in the list
 * @param b                     the replacement value
 */
fun <A> List<A>.replace(a: A, b: A): List<A> {
    fun recReplace(a: A, b: A, list: List<A>, acc: ListBufferIF<A>): List<A> {
        return when (list) {
            is Nil -> acc.toList()
            is Cons -> if (list.hd == a)
                acc.toList().append(b).append(list.tl)
            else
                recReplace(a, b, list.tail(), acc.append(list.hd))
        }
    }   // recReplace

    return recReplace(a, b, this, ListBuffer())
}   // replace

/**
 * scanLeft1 is a variant of scanLeft that has no starting value argument.
 *   The initial value in the list is used as the starting value. An empty list
 *   returns an empty list.
 *
 * Examples:
 *  [1, 2, 3, 4].scanLeft1{m -> {n -> m + n}} == [1, 3, 6, 10]
 *  [64, 4, 2, 8].scanLeft1{m -> {n -> m / n}} == [64, 16, 8, 1]
 *  [12].scanLeft1{m -> {n -> m / n}} == [12]
 *  [3, 6, 12, 4, 55, 11].scanLeft{m -> {n -> if (m > n) m else n}} == [3, 6, 12, 12, 55, 55]
 *
 * @param f                 curried binary function
 * @return                  new list
 */
fun <A> List<A>.scanLeft1(f: (A) -> (A) -> A): List<A> = when(this) {
    is Nil -> List.Nil
    is Cons -> this.tail().scanLeft(this.head(), f)
}   // scanLeft1

/**
 * scanLeft1 is a variant of scanLeft that has no starting value argument.
 *   The initial value in the list is used as the starting value. An empty list
 *   returns an empty list.
 *
 * Examples:
 *  [1, 2, 3, 4].scanLeft1{m, n -> m + n} == [1, 3, 6, 10]
 *  [64, 4, 2, 8].scanLeft1{m, n -> m / n} == [64, 16, 8, 1]
 *  [12].scanLeft1{m, n -> m / n} == [12]
 *  [3, 6, 12, 4, 55, 11].scanLeft{m, n -> if (m > n) m else n} == [3, 6, 12, 12, 55, 55]
 *
 * @param f                 binary function
 * @return                  new list
 */
fun <A> List<A>.scanLeft1(f: (A, A) -> A): List<A> = this.scanLeft1(C2(f))

/**
 * scanRight1 is a variant of scanRight that has no starting value argument.
 *   The initial value in the list is used as the starting value. An empty list
 *   returns an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].scanRight1{m -> {n -> m + n}} == [10, 9, 7, 4]
 *   [8, 12, 24, 2].scanRight1{m -> {n -> m / n}} == [8, 1, 12, 2]
 *   [12].scanRight1{m -> {n -> m / n}} == [12]
 *   [3, 6, 12, 4, 55, 11].scanRight1{m -> {n -> if (m > n) m else n}} == [55, 55, 55, 55, 55, 11]
 *
 * @param f                 curried binary function
 * @return                  new list
 */
fun <A> List<A>.scanRight1(f: (A) -> (A) -> A): List<A> = when(this) {
    is Nil -> List.Nil
    is Cons -> this.init().scanRight(this.last(), f)
}   // scanRight1

/**
 * scanRight1 is a variant of scanRight that has no starting value argument.
 *   The initial value in the list is used as the starting value. An empty list
 *   returns an empty list.
 *
 * Examples:
 *   [1, 2, 3, 4].scanRight1{m, n -> m + n} == [10, 9, 7, 4]
 *   [8, 12, 24, 2].scanRight1{m, n -> m / n} == [8, 1, 12, 2]
 *   [12].scanRight1{m, n -> m / n} == [12]
 *   [3, 6, 12, 4, 55, 11].scanRight1{m, n -> if (m > n) m else n} == [55, 55, 55, 55, 55, 11]
 *
 * @param f                 uncurried binary function
 * @return                  new list
 */
fun <A> List<A>.scanRight1(f: (A, A) -> A): List<A> = this.scanRight1(C2(f))

/**
 * The stripPrefix function drops this prefix from the given list. It returns
 *   None if the list did not start with this prefix, or Some the
 *   list after the prefix, if it does.
 *
 * Examples:
 *   [1, 2].stripPrefix([1, 2, 3, 4]) == Some([3, 4])
 *   [2, 3, 4].stripPrefix([1, 2]) == None
 *   [].stripPrefix([1, 2, 3, 4]) == Some([1, 2, 3, 4])
 *   [1, 2, 3, 4].stripPrefix([]) == None
 *
 * @param xs                existing list of possible prefix
 * @return                  new list of prefix
 */
fun <A> List<A>.stripPrefix(xs: List<A>): Option<List<A>> {
    tailrec
    fun recStripPrefix(ps: List<A>, qs: List<A>): Option<List<A>> {
        return when(ps) {
            is Nil -> Option.Some(qs)
            is Cons -> {
                when(qs) {
                    is Nil -> Option.None
                    is Cons -> if (ps.head() != qs.head()) Option.None else recStripPrefix(ps.tail(), qs.tail())
                }
            }
        }
    }   // recStripPrefix

    return recStripPrefix(this, xs)
}   // stripPrefix

/**
 * Return a sequence over the elements of this list.
 *
 * Examples:
 *   [1, 2, 3, 4].sequence().count() == 4
 */
fun <A> List<A>.sequence(): Sequence<A> {
    val iterator: Iterator<A> = this.iterator()
    val sequence: Sequence<A> = Sequence{ -> iterator}
    return sequence
}   // sequence


/**
 * Return a stream over the elements of this list.
 *
 * Examples:
 *   [1, 2, 3, 4].stream().count() = 4
 */
fun <A> List<A>.stream(): JStream<A> {
    val iterator: Iterator<A> = this.iterator()
    val stream: JStream<A> = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
    return stream
}   // stream
