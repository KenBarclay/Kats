package com.adt.kotlin.kats.mtl.instances.applicative

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.either.EitherF.left
import com.adt.kotlin.kats.data.immutable.either.EitherF.right
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.mtl.data.either.EitherT
import com.adt.kotlin.kats.mtl.data.either.EitherTOf
import com.adt.kotlin.kats.mtl.data.either.narrow
import com.adt.kotlin.kats.mtl.instances.functor.EitherTFunctor

interface EitherTApplicative<F, A> : Applicative<Kind1<Kind1<EitherT.EitherTProxy, F>, A>>, EitherTFunctor<F, A> {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <B> pure(b: B): EitherT<F, A, B> {
        val mf: Monad<F> = monad()
        return EitherT(mf.inject(right(b)))
    }   // pure

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <B, C> ap(v: EitherTOf<F, A, B>, f: EitherTOf<F, A, (B) -> C>): EitherT<F, A, C> {
        val mf: Monad<F> = monad()
        val vEitherT: EitherT<F, A, B> = v.narrow()
        val fEitherT: EitherT<F, A, (B) -> C> = f.narrow()

        return EitherT(mf.bind(fEitherT.runEitherT){fbc: Either<A, (B) -> C> ->
            when (fbc) {
                is Left -> mf.inject(left(fbc.value))
                is Right -> {
                    val g: (B) -> C = fbc.value
                    mf.bind(vEitherT.runEitherT){eab: Either<A, B> ->
                        when (eab) {
                            is Left -> mf.inject(left(eab.value))
                            is Right -> mf.inject(right(g(eab.value)))
                        }
                    }
                }
            }
        })
    }   // ap

}   // EitherTApplicative
