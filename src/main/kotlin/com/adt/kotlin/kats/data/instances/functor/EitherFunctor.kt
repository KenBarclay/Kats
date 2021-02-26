package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.EitherOf
import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.either.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



/**
 * Functor over an Either.
 */
interface EitherFunctor<A> : Functor<Kind1<EitherProxy, A>> {

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <B, C> fmap(v: EitherOf<A, B>, f: (B) -> C): Either<A, C> {
        val vEither: Either<A, B> = v.narrow()
        return vEither.fmap(f)
    }   // fmap



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Either context.
     */
    override fun <B, C> lift(f: (B) -> C): (EitherOf<A, B>) -> Either<A, C> =
            {eab: EitherOf<A, B> ->
                fmap(eab, f)
            }

}   // EitherFunctor
