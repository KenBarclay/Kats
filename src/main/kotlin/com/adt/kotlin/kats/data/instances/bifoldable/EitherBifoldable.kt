package com.adt.kotlin.kats.data.instances.bifoldable

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.either.EitherOf
import com.adt.kotlin.kats.data.immutable.either.narrow

import com.adt.kotlin.kats.hkfp.typeclass.Bifoldable



interface EitherBifoldable : Bifoldable<EitherProxy> {

    /**
     * Collapse the structure with a left-associative function.
     */
    override fun <A, B, C> bifoldLeft(v: EitherOf<A, B>, c: C, f: (C) -> (A) -> C, g: (C) -> (B) -> C): C {
        val vEither: Either<A, B> = v.narrow()
        return when(vEither) {
            is Left -> f(c)(vEither.value)
            is Right -> g(c)(vEither.value)
        }
    }

    /**
     * Collapse the structure with a right-associative function.
     */
    override fun <A, B, C> bifoldRight(v: EitherOf<A, B>, c: C, f: (A) -> (C) -> C, g: (B) -> (C) -> C): C {
        val vEither: Either<A, B> = v.narrow()
        return when(vEither) {
            is Left -> f(vEither.value)(c)
            is Right -> g(vEither.value)(c)
        }
    }

}   // EitherBifoldable
