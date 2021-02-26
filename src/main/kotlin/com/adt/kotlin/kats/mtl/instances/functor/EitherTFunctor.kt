package com.adt.kotlin.kats.mtl.instances.functor

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.mtl.data.either.EitherT
import com.adt.kotlin.kats.mtl.data.either.EitherT.EitherTProxy
import com.adt.kotlin.kats.mtl.data.either.EitherTOf
import com.adt.kotlin.kats.mtl.data.either.narrow



interface EitherTFunctor<F, A> : Functor<Kind1<Kind1<EitherTProxy, F>, A>> {

    fun monad(): Monad<F>

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <B, C> fmap(v: EitherTOf<F, A, B>, f: (B) -> C): EitherT<F, A, C> {
        val mf: Monad<F> = monad()
        val vEitherT: EitherT<F, A, B> = v.narrow()

        return EitherT(mf.fmap(vEitherT.runEitherT){eab: Either<A, B> ->
            val eac: Either<A, C> = eab.fmap(f)
            eac
        })
    }   // fmap

}   // EitherTFunctor
