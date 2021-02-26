package com.adt.kotlin.kats.mtl.instances.foldable

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.instances.foldable.EitherFoldable
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.mtl.data.either.EitherT
import com.adt.kotlin.kats.mtl.data.either.EitherT.EitherTProxy
import com.adt.kotlin.kats.mtl.data.either.EitherTOf
import com.adt.kotlin.kats.mtl.data.either.narrow



interface EitherTFoldable<F, A> : Foldable<Kind1<Kind1<EitherTProxy, F>, A>> {

    fun foldable(): Foldable<F>

    override fun <B, C> foldLeft(v: EitherTOf<F, A, B>, e: C, f: (C) -> (B) -> C): C {
        val ff: Foldable<F> = foldable()
        val ef: EitherFoldable<A> = Either.foldable<A>() as EitherFoldable<A>
        val vEitherT: EitherT<F, A, B> = v.narrow()

        return ff.run{
            foldLeft(vEitherT.runEitherT, e){c: C, eab: Either<A, B> ->
                ef.run{
                    eab.foldLeft(c, f)
                }
            }
        }
    }   // foldLeft

    override fun <B, C> foldRight(v: EitherTOf<F, A, B>, e: C, f: (B) -> (C) -> C): C {
        val ff: Foldable<F> = foldable()
        val ef: EitherFoldable<A> = Either.foldable<A>() as EitherFoldable<A>
        val vEitherT: EitherT<F, A, B> = v.narrow()

        return ff.run{
            foldRight(vEitherT.runEitherT, e){eab: Either<A, B>, c: C ->
                ef.run{
                    eab.foldRight(c, f)
                }
            }
        }
    }   // foldRight

}   // EitherTFoldable
