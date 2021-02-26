package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.option.Option

import com.adt.kotlin.kats.hkfp.typeclass.Monoid



/**
 * Monoid that combines options.
 */
class OptionMonoid<A>(val ma: Monoid<A>) : Monoid<Option<A>> {

    override val empty: Option<A> = Option.None
    override fun combine(a: Option<A>, b: Option<A>): Option<A> {
        return when (a) {
            is Option.None -> b
            is Option.Some -> {
                when (b) {
                    is Option.None -> a
                    is Option.Some -> Option.Some(ma.run { combine(a.value, b.value) })
                }
            }
        }
    }   // combine

}   // OptionMonoid
