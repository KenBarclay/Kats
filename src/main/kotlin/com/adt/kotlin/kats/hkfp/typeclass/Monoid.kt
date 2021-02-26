package com.adt.kotlin.kats.hkfp.typeclass

/**
 * A class for monoids (types with an associative binary operation that has an identity)
 *   with various general-purpose instances. We see that only concrete types can be made
 *   instances of Monoid, because the A in the type class definition doesn't take any type
 *   parameters.
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.data.immutable.list.List



interface Monoid<A> : Semigroup<A> {

    val empty: A

    /**
     * Fold a list using the monoid.
     *
     * For most types, the default definition for concat will be sufficient.
     *   The function is included in the class definition so that an optimized
     *   version can be provided for specific types.
     */
    fun concat(list: List<A>): A = list.foldRight(empty, ::combine)

}   // Monoid



/**
 * The dual of a Monoid, obtained by swapping the arguments of combine.
 */
interface Dual<A> {

    val dual: A

}   // Dual

fun <A> dual(a: A): Dual<A> = object: Dual<A> {
    override val dual: A = a
}   // dual

fun <A> appDual(dual: Dual<A>): A = dual.dual



/**
 * The monoid of endomorphisms under composition.
 */
interface Endo<A> {

    val endo: (A) -> A

}   // Endo

fun <A> endo(f: (A) -> A): Endo<A> = object: Endo<A> {
    override val endo: (A) -> A = f
}   // endo

fun <A> appEndo(endo: Endo<A>): (A) -> A = endo.endo
