package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.validation.Validation
import com.adt.kotlin.kats.data.immutable.validation.Validation.Failure
import com.adt.kotlin.kats.data.immutable.validation.Validation.Success
import com.adt.kotlin.kats.hkfp.typeclass.Monoid



class ValidationMonoid<E, A>(val me: Monoid<E>) : Monoid<Validation<E, A>> {

    override val empty: Validation<E, A> = Failure(me.empty)
    override fun combine(a: Validation<E, A>, b: Validation<E, A>): Validation<E, A> {
        return when(a) {
            is Failure -> when(b) {
                is Failure -> Failure(me.combine(a.err, b.err))
                is Success -> b
            }
            is Success -> a
        }
    }   // combine

}   // ValidationMonoid
