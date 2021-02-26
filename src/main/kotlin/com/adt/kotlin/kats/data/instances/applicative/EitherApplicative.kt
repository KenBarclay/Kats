package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.either.EitherF.right
import com.adt.kotlin.kats.data.immutable.either.EitherOf
import com.adt.kotlin.kats.data.immutable.either.ap
import com.adt.kotlin.kats.data.immutable.either.narrow

import com.adt.kotlin.kats.data.instances.functor.EitherFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative



interface EitherApplicative<A> : Applicative<Kind1<EitherProxy, A>>, EitherFunctor<A> {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <B> pure(a: B): Either<A, B> = right(a)

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <B, C> ap(v: EitherOf<A, B>, f: EitherOf<A, (B) -> C>): Either<A, C> {
        val eitherV: Either<A, B> = v.narrow()
        val eitherF: Either<A, (B) -> C> = f.narrow()
        return eitherV.ap(eitherF)
    }   // ap

}   // EitherApplicative
