package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.validation.Validation
import com.adt.kotlin.kats.data.immutable.validation.Validation.Failure
import com.adt.kotlin.kats.data.immutable.validation.Validation.Success
import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface ValidationSemigroup<E, A> : Semigroup<Validation<E, A>> {

    val sge: Semigroup<E>

    override fun combine(a: Validation<E, A>, b: Validation<E, A>): Validation<E, A> {
        return when (a) {
            is Failure -> when (b) {
                is Failure -> Failure(sge.run{ combine(a.err, b.err) })
                is Success -> b
            }
            is Success -> a
        }
    }   // combine

}   // ValidationSemigroup
