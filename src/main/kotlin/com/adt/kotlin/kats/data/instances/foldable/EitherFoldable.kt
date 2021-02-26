package com.adt.kotlin.kats.data.instances.foldable

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.either.EitherOf
import com.adt.kotlin.kats.data.immutable.either.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface EitherFoldable<A> : Foldable<Kind1<EitherProxy, A>> {

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <B, C> foldLeft(v: EitherOf<A, B>, e: C, f: (C) -> (B) -> C): C {
        val vEither: Either<A, B> = v.narrow()
        return vEither.foldLeft(e, f)
    }   // foldLeft

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <B, C> foldRight(v: EitherOf<A, B>, e: C, f: (B) -> (C) -> C): C {
        val vEither: Either<A, B> = v.narrow()
        return vEither.foldRight(e, f)
    }   // foldRight

}   // EitherFoldable
