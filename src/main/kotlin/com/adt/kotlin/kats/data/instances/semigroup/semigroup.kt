package com.adt.kotlin.kats.data.instances.semigroup

/**
 * The class of semigroups (types with an associative binary operation).
 *   Instances should satisfy the associativity law:
 *
 *   x combine (y combine z) = (x combine y) combine z
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



/**
 * Semigroup that adds integers.
 */
val intAddSemigroup: Semigroup<Int> = object: Semigroup<Int> {
    override fun combine(a: Int, b: Int): Int = a + b
}

/**
 * Semigroup that multiplies integers.
 */
val intMulSemigroup: Semigroup<Int> = object: Semigroup<Int> {
    override fun combine(a: Int, b: Int): Int = a * b
}



/**
 * Semigroup that adds longs.
 */
val longAddSemigroup: Semigroup<Long> = object: Semigroup<Long> {
    override fun combine(a: Long, b: Long): Long = a + b
}

/**
 * Semigroup that multiplies longs.
 */
val longMulSemigroup: Semigroup<Long> = object: Semigroup<Long> {
    override fun combine(a: Long, b: Long): Long = a * b
}



/**
 * Semigroup that adds doubles.
 */
val doubleAddSemigroup: Semigroup<Double> = object: Semigroup<Double> {
    override fun combine(a: Double, b: Double): Double = a + b
}

/**
 * Semigroup that multiplies doubles.
 */
val doubleMulSemigroup: Semigroup<Double> = object: Semigroup<Double> {
    override fun combine(a: Double, b: Double): Double = a * b
}



/**
 * Semigroup that ands booleans.
 */
val booleanConjSemigroup: Semigroup<Boolean> = object: Semigroup<Boolean> {
    override fun combine(a: Boolean, b: Boolean): Boolean = a && b
}

/**
 * Semigroup that ors booleans.
 */
val booleanDisjSemigroup: Semigroup<Boolean> = object: Semigroup<Boolean> {
    override fun combine(a: Boolean, b: Boolean): Boolean = a || b
}

/**
 * Semigroup that xors booleans.
 */
val booleanExDisjSemigroup: Semigroup<Boolean> = object: Semigroup<Boolean> {
    override fun combine(a: Boolean, b: Boolean): Boolean = ((a && !b) || (!a && b))
}



/**
 * Semigroup that concatenates strings.
 */
val stringSemigroup: Semigroup<String> = object: Semigroup<String> {
    override fun combine(a: String, b: String): String = a + b
}



/**
 * Semigroup that yields the minimum of integers.
 */
val intMinimumSemigroup: Semigroup<Int> = object: Semigroup<Int> {
    override fun combine(a: Int, b: Int): Int = if (a < b) a else b
}

/**
 * Semigroup that yields the maximum of integers.
 */
val intMaximumSemigroup: Semigroup<Int> = object: Semigroup<Int> {
    override fun combine(a: Int, b: Int): Int = if (a > b) a else b
}



interface PairSemigroup<A, B> : Semigroup<Pair<A, B>> {

    val sga: Semigroup<A>
    val sgb: Semigroup<B>

    override fun combine(a: Pair<A, B>, b: Pair<A, B>): Pair<A, B> {
        val first: A = sga.run { combine(a.first, b.first) }
        val second: B = sgb.run{ combine(a.second, b.second) }
        return Pair(first, second)
    }   // combine

}   // PairSemigroup

fun <A, B> pairSemigroup(sga: Semigroup<A>, sgb: Semigroup<B>): PairSemigroup<A, B> = object: PairSemigroup<A, B> {
    override val sga: Semigroup<A> = sga
    override val sgb: Semigroup<B> = sgb
}
