package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.tri.Try
import com.adt.kotlin.kats.data.immutable.tri.Try.Failure
import com.adt.kotlin.kats.data.immutable.tri.Try.Success

import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface TrySemigroup<A> : Semigroup<Try<A>> {

    val sga: Semigroup<A>

    override fun combine(a: Try<A>, b: Try<A>): Try<A> {
        return when (a) {
            is Failure -> b
            is Success -> {
                when (b) {
                    is Failure -> a
                    is Success -> Success(sga.run{ combine(a.value, b.value) })
                }
            }
        }
    }   // combine

}   // TrySemigroup
