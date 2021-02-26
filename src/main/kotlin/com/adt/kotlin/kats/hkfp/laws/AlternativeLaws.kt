package com.adt.kotlin.kats.hkfp.laws

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Alternative



class AlternativeLaws<F>(val alternative: Alternative<F>) {

    /**
     * empty is the left identity element for combine.
     */
    fun <A> leftIdentityLaw(v: Kind1<F, A>): Boolean =
            alternative.run{ combine(empty(), v) == v }

    /**
     * empty is the right identity element for combine.
     */
    fun <A> rightIdentityLaw(v: Kind1<F, A>): Boolean =
            alternative.run{ combine(v, empty()) == v }

    /**
     * The binary operation combine must be associative:
     *
     *   (a combine b) combine c == a combine (b combine c)
     *
     * As long as you do not change the order of the arguments, you can insert
     *   parenthesis anywhere, and the result will be the same.
     */
    fun <A> associativeLaw(v1: Kind1<F, A>, v2: Kind1<F, A>, v3: Kind1<F, A>): Boolean =
            alternative.run{ combine(combine(v1, v2), v3) == combine(v1, combine(v2, v3)) }

}   // AlternativeLaws
