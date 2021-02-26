package com.adt.kotlin.kats.data.instances.bifunctor

/**
 * A Bifunctor is a type constructor that takes two type arguments and is a
 *   functor in both arguments. That is, unlike with Functor, a type constructor
 *   such as Either does not need to be partially applied for a Bifunctor instance,
 *   and the methods in this class permit mapping functions over the Left value or
 *   the Right value, or both at the same time.
 *
 * @author	                    Ken Barclay
 * @since                       September 2018
 */

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.either.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Bifunctor



interface EitherBifunctor : Bifunctor<EitherProxy> {

    /**
     * Map over both arguments at the same time.
     */
    override fun <A, B, C, D> bimap(v: Kind1<Kind1<EitherProxy, A>, C>, f: (A) -> B, g: (C) -> D): Either<B, D> {
        val vEither: Either<A, C> = v.narrow()
        return when(vEither) {
            is Left -> Left(f(vEither.value))
            is Right -> Right(g(vEither.value))
        }
    }   // bimap

    /**
     * Map covariantly over the first argument.
     */
    override fun <A, B, C> first(v: Kind1<Kind1<EitherProxy, A>, C>, f: (A) -> B): Either<B, C> =
            bimap(v, f, {c: C -> c})

    /**
     * Map covariantly over the second argument.
     */
    override fun <A, C, D> second(v: Kind1<Kind1<EitherProxy, A>, C>, g: (C) -> D): Either<A, D> =
            bimap(v, {a: A -> a}, g)

}   // EitherBifunctor
