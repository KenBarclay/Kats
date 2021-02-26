package com.adt.kotlin.kats.data.instances.monoid

/**
 * A class for monoids (types with an associative binary operation that has an identity)
 *   with various general-purpose instances. We see that only concrete types can be made
 *   instances of Monoid, because the A in the type class definition doesn't take any type
 *   parameters.
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose
import com.adt.kotlin.kats.hkfp.typeclass.*


/**
 * Monoid that adds integers.
 */
val intAddMonoid: Monoid<Int> = object: Monoid<Int> {
    override val empty: Int = 0
    override fun combine(a: Int, b: Int): Int = a + b
}

/**
 * Monoid that multiplies integers.
 */
val intMulMonoid: Monoid<Int> = object: Monoid<Int> {
    override val empty: Int = 1
    override fun combine(a: Int, b: Int): Int = a * b
}



/**
 * Monoid that adds longs.
 */
val longAddMonoid: Monoid<Long> = object: Monoid<Long> {
    override val empty: Long = 0L
    override fun combine(a: Long, b: Long): Long = a + b
}

/**
 * Monoid that multiplies longs.
 */
val longMulMonoid: Monoid<Long> = object: Monoid<Long> {
    override val empty: Long = 1L
    override fun combine(a: Long, b: Long): Long = a * b
}



/**
 * Monoid that adds doubles.
 */
val doubleAddMonoid: Monoid<Double> = object: Monoid<Double> {
    override val empty: Double = 0.0
    override fun combine(a: Double, b: Double): Double = a + b
}

/**
 * Monoid that multiplies doubles.
 */
val doubleMulMonoid: Monoid<Double> = object: Monoid<Double> {
    override val empty: Double = 1.0
    override fun combine(a: Double, b: Double): Double = a * b
}



/**
 * Monoid that ands booleans.
 */
val booleanConjMonoid: Monoid<Boolean> = object: Monoid<Boolean> {
    override val empty: Boolean = true
    override fun combine(a: Boolean, b: Boolean): Boolean = a && b
}

/**
 * Monoid that ors booleans.
 */
val booleanDisjMonoid: Monoid<Boolean> = object: Monoid<Boolean> {
    override val empty: Boolean = false
    override fun combine(a: Boolean, b: Boolean): Boolean = a || b
}



/**
 * Monoid that concatenates strings.
 */
val stringMonoid: Monoid<String> = object: Monoid<String> {
    override val empty: String = ""
    override fun combine(a: String, b: String): String = a + b
}



/**
 * Monoid that combines pairs.
 */
class PairMonoid<A, B>(val ma: Monoid<A>, val mb: Monoid<B>) : Monoid<Pair<A, B>> {

    override val empty: Pair<A, B> = Pair(ma.run{ empty }, mb.run{ empty })
    override fun combine(a: Pair<A, B>, b: Pair<A, B>): Pair<A, B> {
        val first: A = ma.run { combine(a.first, b.first) }
        val second: B = mb.run{ combine(a.second, b.second) }
        return Pair(first, second)
    }   // combine

}   // PairMonoid



class DualMonoid<A>(val ma: Monoid<A>) : Monoid<Dual<A>> {

    override val empty: Dual<A> = dual(ma.empty)
    override fun combine(a: Dual<A>, b: Dual<A>): Dual<A> =
            dual(ma.combine(a.dual, b.dual))

}   // DualMonoid

/**
 * Monoid that combines functions.
 */
class EndoMonoid<A> : Monoid<Endo<A>> {

    override val empty: Endo<A> = endo({a: A -> a})
    override fun combine(a: Endo<A>, b: Endo<A>): Endo<A> =
            endo(compose(a.endo, b.endo))

}   // EndoMonoid
