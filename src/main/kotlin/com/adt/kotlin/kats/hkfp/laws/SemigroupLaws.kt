package com.adt.kotlin.kats.hkfp.laws

/**
 * The law which all instances of Semigroup must follow simply state the properties we already know:
 *   combine is associative.
 *
 * @author	                    Ken Barclay
 * @since                       January 2019
 */

import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



class SemigroupLaws<A>(val semigroup: Semigroup<A>) {

    /**
     * The binary operation combine must be associative:
     *
     *   (a combine b) combine c == a combine (b combine c)
     *
     * As long as you do not change the order of the arguments, you can insert
     *   parenthesis anywhere, and the result will be the same.
     */
    fun associativeLaw(v1: A, v2: A, v3: A): Boolean =
            semigroup.run{ combine(combine(v1, v2), v3) == combine(v1, combine(v2, v3)) }

}   // SemigroupLaws

class SemigroupLawsDouble(val semigroup: Semigroup<Double>, val epsilon: Double = 1.0e-6) {

    fun associativeLaw(v1: Double, v2: Double, v3: Double): Boolean {
        return semigroup.run{
            val v1v2Andv3: Double = combine(combine(v1, v2), v3)
            val v1Andv2v3: Double = combine(v1, combine(v2, v3))
            Math.abs(v1v2Andv3 - v1Andv2v3) < epsilon
        }
    }

}   // SemigroupLawsDouble
