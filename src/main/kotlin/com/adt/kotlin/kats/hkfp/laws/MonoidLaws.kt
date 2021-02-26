package com.adt.kotlin.kats.hkfp.laws

/**
 * The laws which all instances of Monoid must follow simply state the properties we already know:
 *   combine is associative and empty is its identity element.
 *
 * @author	                    Ken Barclay
 * @since                       January 2019
 */

import com.adt.kotlin.kats.hkfp.typeclass.Monoid



class MonoidLaws<A>(val monoid: Monoid<A>) {

    /**
     * empty is the left identity element for combine.
     */
    fun leftIdentityLaw(v: A): Boolean =
            monoid.run{ combine(empty, v) == v }

    /**
     * empty is the right identity element for combine.
     */
    fun rightIdentityLaw(v: A): Boolean =
            monoid.run{ combine(v, empty) == v }

    /**
     * The binary operation combine must be associative:
     *
     *   (a combine b) combine c == a combine (b combine c)
     *
     * As long as you do not change the order of the arguments, you can insert
     *   parenthesis anywhere, and the result will be the same.
     */
    fun associativeLaw(v1: A, v2: A, v3: A): Boolean =
            monoid.run{ combine(combine(v1, v2), v3) == combine(v1, combine(v2, v3)) }

}   // MonoidLaws
