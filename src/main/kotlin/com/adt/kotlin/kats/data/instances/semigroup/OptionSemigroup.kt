package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface OptionSemigroup<A> : Semigroup<Option<A>> {

    val sga: Semigroup<A>

    override fun combine(a: Option<A>, b: Option<A>): Option<A> {
        return when (a) {
            is Option.None -> b
            is Option.Some -> {
                when (b) {
                    is Option.None -> a
                    is Option.Some -> Option.Some(sga.run { combine(a.value, b.value) })
                }
            }
        }
    }   // combine

}   // OptionSemigroup
