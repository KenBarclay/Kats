package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.tri.Try
import com.adt.kotlin.kats.data.immutable.tri.Try.Failure
import com.adt.kotlin.kats.data.immutable.tri.Try.Success

import com.adt.kotlin.kats.hkfp.typeclass.Monoid



class TryMonoid<A>(val md: Monoid<A>) : Monoid<Try<A>> {

    override val empty: Try<A> = Success(md.empty)
    override fun combine(a: Try<A>, b: Try<A>): Try<A> {
        return when (a) {
            is Failure -> b
            is Success -> {
                when (b) {
                    is Failure -> a
                    is Success -> Success(md.run { combine(a.value, b.value) })
                }
            }
        }
    }   // combine

}   // TryMonoid
