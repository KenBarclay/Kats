package com.adt.kotlin.kats.data.instances.alternative

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.narrow
import com.adt.kotlin.kats.data.instances.applicative.OptionApplicative

import com.adt.kotlin.kats.hkfp.typeclass.Alternative



interface OptionAlternative : Alternative<OptionProxy>, OptionApplicative {

    /**
     * The identity of combine.
     */
    override fun <A> empty(): Option<A> = None

    /**
     * An associative binary operation.
     */
    override fun <A> combine(a: OptionOf<A>, b: OptionOf<A>): Option<A> {
        val aOption: Option<A> = a.narrow()
        val bOption: Option<A> = b.narrow()
        return when(aOption) {
            is None -> bOption
            is Some -> aOption
        }
    }   // combine

}   // OptionAlternative
