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

import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some



object OptionF {

    /**
     * Factory functions to create the base instances.
     */
    fun <A> none(): Option<A> = None

    fun <A> some(a: A): Option<A> = Some(a)

    /**
     * The option function takes an Option value, a default value, and a function.
     *   If the Option value is None, the function returns the default value.
     *   Otherwise, it applies the function to the value inside the Some and returns
     *   the result. See also the member function fold.
     *
     * @param op            	    option value
     * @param defaultValue  	    fall-back result
     * @param f           	        pure function:: A -> B
     * @return              	    function's value or default
     */
    fun <A, B> option(op: Option<A>, defaultValue: B, f: (A) -> B): B =
        if(op.isEmpty()) defaultValue else f(op.get())

    /**
     * Return an optional value that has a value of the given parameter, if the given predicate holds
     *   on that parameter, otherwise, returns no value.
     *
     * @param predicate         the predicate to test of the given parameter
     * @param a                 the parameter to test the predicate on and potentially
     *                              the value of the returned optional
     * @return                  an optional value that has a value of the given parameter,
     *                              if the given predicate holds on that parameter, otherwise,
     *                              returns no value
     */
    fun <A> iif(a: A, predicate: (A) -> Boolean): Option<A> =
        if (predicate(a)) some(a) else none()

}   // OptionF
