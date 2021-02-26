package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left

import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface EitherSemigroup<A, B> : Semigroup<Either<A, B>> {

    override fun combine(a: Either<A, B>, b: Either<A, B>): Either<A, B> {
        return when(a) {
            is Left -> b
            else -> a
        }
    }   // combine

}   // EitherSemigroup
