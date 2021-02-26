package com.adt.kotlin.kats.data.immutable.option

/**
 * The Option type encapsulates an optional value.
 *
 * A value of type Option[A] either contains a value of type A (represented as Some A),
 *   or it is empty represented as None. Using Option is a good way to deal with errors
 *   without resorting to exceptions. The algebraic data type declaration is:
 *
 * datatype Option[A] = None
 *                    | Some A
 *
 * This Option type is inspired by the Haskell Maybe data type. The idiomatic way to
 *   employ an Option instance is as a monad using the functions map, inject, bind
 *   and filter. Given:
 *
 *   fun divide(num: Int, den: Int): Option<Int> ...
 *
 * then:
 *
 *   divide(a, c).bind{ac -> divide(b, c).bind{bc -> Some(Pair(ac, bc))}}
 *
 * finds the pair of divisions of a and b by c should c be an exact divisor.
 *
 * @author	                    Ken Barclay
 * @since                       October 2019
 */


/**
 * Since class Option<A> is the only implementation for Kind1<OptionProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A> OptionOf<A>.narrow(): Option<A> = this as Option<A>



// Contravariant extension functions:

/**
 * Test if this option is defined and contains the given element.
 *
 * Examples:
 *   none.contains(99) == false
 *   Some(5).contains(99) == false
 *   Some(99).contains(99) == true
 *
 * @param elem              the element to test
 * @return                  true if the option has an element that is equal to elem, false otherwise
 */
operator fun <A> Option<A>.contains(elem: A): Boolean = when(this) {
    is Option.None -> false
    is Option.Some -> (this.value == elem)
}   // contains

/**
 * Return the option's value if the option is nonempty, otherwise
 *   return the defaultValue.
 *
 * Examples:
 *   none.getOrElse(99) == 99
 *   Some(5).getOrElse(99) == 5
 *
 * @param defaultValue  	fall-back result
 * @return              	option's value or default
 */
fun <A> Option<A>.getOrElse(defaultValue: A): A = when(this) {
    is Option.None -> defaultValue
    is Option.Some -> this.value
}   // getOrElse

/**
 * Return this option if the option is nonempty, otherwise return another
 *   option provided lazily by default.
 *
 * Examples:
 *   none.orElse{-> Some(99)} == Some(99)
 *   Some(5).orElse{-> Some(99)} == Some(5)
 */
fun <A> Option<A>.orElse(defaultValue: () -> Option<A>): Option<A> =
    fold(defaultValue, {_: A -> this})
