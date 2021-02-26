package com.adt.kotlin.kats.hkfp.typeclass

/**
 * The class of semigroups (types with an associative binary operation).
 *   Instances should satisfy the associativity law:
 *
 *   x combine (y combine z) = (x combine y) combine z
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */



interface Semigroup<A> {

    fun combine(a: A, b: A): A

    operator fun A.plus(b: A): A = combine(this, b)

}   // Semigroup
