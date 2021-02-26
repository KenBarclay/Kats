package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.either.EitherOf
import com.adt.kotlin.kats.data.immutable.either.narrow

import com.adt.kotlin.kats.data.instances.foldable.EitherFoldable
import com.adt.kotlin.kats.data.instances.functor.EitherFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface EitherTraversable<A> : Traversable<Kind1<EitherProxy, A>>, EitherFoldable<A>, EitherFunctor<A> {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    override fun <G, B, C> traverse(v: EitherOf<A, B>, ag: Applicative<G>, f: (B) -> Kind1<G, C>): Kind1<G, Either<A, C>> {
        val vEither: Either<A, B> = v.narrow()
        return vEither.traverse(ag, f)
    }   // traverse

}   // EitherTraversable
