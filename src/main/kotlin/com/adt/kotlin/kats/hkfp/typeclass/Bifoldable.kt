package com.adt.kotlin.kats.hkfp.typeclass

/**
 * A type class abstracting over types that give rise to two independent
 *   Foldables. Minimal definition is either bifoldRight or bifoldMap.
 *
 * @author	                    Ken Barclay
 * @since                       September 2018
 */

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.kind.Kind1



interface Bifoldable<F> {

    /**
     * Collapse the structure with a left-associative function.
     */
    fun <A, B, C> bifoldLeft(v: Kind1<Kind1<F, A>, B>, c: C, f: (C) -> (A) -> C, g: (C) -> (B) -> C): C     // TODO

    /***{
    fun <X> dual(x: X): Dual<X> = object: Dual<X> { override val dual: X = x }
    fun <X> endo(x: X): Endo<X> = object: Endo<X> { override val endo: (X) -> X = {_: X -> x} }
    //val composeF = compose(compose(::dual, ::endo), flip(f))
    val composeF = compose(::dual, compose(::endo, flip(f)))
    val composeG = compose(::dual, compose(::endo, flip(g)))
    val bfM = bifoldMap(v, composeF, composeG)
    }   // bifoldLeft***/

    fun <A, B, C> bifoldLeft(v: Kind1<Kind1<F, A>, B>, c: C, f: (C, A) -> C, g: (C, B) -> C): C = this.bifoldLeft(v, c, C2(f), C2(g))

    /**
     * Collapse the structure with a right-associative function.
     */
    fun <A, B, C> bifoldRight(v: Kind1<Kind1<F, A>, B>, c: C, f: (A) -> (C) -> C, g: (B) -> (C) -> C): C

    fun <A, B, C> bifoldRight(v: Kind1<Kind1<F, A>, B>, c: C, f: (A, C) -> C, g: (B, C) -> C): C = this.bifoldRight(v, c, C2(f), C2(g))

    /**
     * Collapse the structure by mapping each element to an element of a type
     *   that has a Monoid.
     */
    fun <A, B, C> bifoldMap(v: Kind1<Kind1<F, A>, B>, f: (A) -> C, g: (B) -> C, mc: Monoid<C>): C {
        return mc.run {
            bifoldLeft(v, empty, {c -> {a -> combine(c, f(a))}}, {c -> {b -> combine(c, g(b))}})
        }
    }   // bifoldMap

    ////fun bifold(v: Kind1<Kind1<F, Monoid<F>>, Monoid<F>>): Monoid<F>

}   // Bifoldable
