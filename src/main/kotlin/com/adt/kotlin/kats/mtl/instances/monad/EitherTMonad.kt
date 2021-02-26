package com.adt.kotlin.kats.mtl.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.either.EitherF
import com.adt.kotlin.kats.data.immutable.either.EitherF.left
import com.adt.kotlin.kats.data.immutable.either.EitherF.right
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.mtl.data.either.EitherT
import com.adt.kotlin.kats.mtl.data.either.EitherT.EitherTProxy
import com.adt.kotlin.kats.mtl.data.either.EitherTOf
import com.adt.kotlin.kats.mtl.data.either.narrow
import com.adt.kotlin.kats.mtl.instances.applicative.EitherTApplicative



interface EitherTMonad<F, A> : Monad<Kind1<Kind1<EitherTProxy, F>, A>>, EitherTApplicative<F, A> {

    /**
     * Inject a value into the monadic type.
     */
    override fun <B> inject(b: B): EitherT<F, A, B> {
        val mf: Monad<F> = monad()
        return EitherT(mf.inject(EitherF.right(b)))
    }   // inject

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <B, C> bind(v: EitherTOf<F, A, B>, f: (B) -> EitherTOf<F, A, C>): EitherT<F, A, C> {
        val mf: Monad<F> = monad()
        val vEitherT: EitherT<F, A, B> = v.narrow()

        return EitherT(mf.bind(vEitherT.runEitherT){eab: Either<A, B> ->
            when (eab) {
                is Left -> mf.inject(left(eab.value))
                is Right -> {
                    val fac: EitherTOf<F, A, C> = f(eab.value)
                    val efac: EitherT<F, A, C> = fac.narrow()
                    efac.runEitherT
                }
            }
        })
    }   // bind

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> EitherTOf<F, A, Either<B, C>>): EitherT<F, A, C> {
        val mf: Monad<F> = monad()
        return EitherT(mf.tailRecM(b){bb: B ->
            val etabc: EitherT<F, A, Either<B, C>> = f(bb).narrow()
            val runet: Kind1<F, Either<A, Either<B, C>>> = etabc.runEitherT
            mf.run{
                fmap(runet){eabc: Either<A, Either<B, C>> ->
                    when (eabc) {
                        is Left -> right(left(eabc.value))
                        is Right -> {
                            val ebc: Either<B, C> = eabc.value
                            when (ebc) {
                                is Left -> left(ebc.value)
                                is Right -> right(right(ebc.value))
                            }
                        }
                    }
                }
            }
        })
    }   // tailRecM

}   // EitherTMonad
