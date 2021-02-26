package com.adt.kotlin.kats.control.instances.monad

import com.adt.kotlin.kats.control.data.writert.WriterT
import com.adt.kotlin.kats.control.data.writert.WriterT.WriterTProxy
import com.adt.kotlin.kats.control.data.writert.WriterTF.writert
import com.adt.kotlin.kats.control.data.writert.WriterTOf
import com.adt.kotlin.kats.control.data.writert.narrow

import com.adt.kotlin.kats.control.instances.applicative.WriterTApplicative
import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface WriterTMonad<F, W> : Monad<Kind1<Kind1<WriterTProxy, F>, W>>, WriterTApplicative<F, W> {

    fun monad(): Monad<F>
    override fun applicative(): Applicative<F> = monad()

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): WriterT<F, W, A> = writert(applicative().pure(Pair(mw.empty, a)))

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     *
     * Examples:
     *   let monad = WriterT.monad(Identity.monad(), stringMonoid)
     *   let strInt = writer("Ken", 7)
     *
     *   monad.bind(strInt){ n: Int -> writer("neth", isEven(n))} == writer("Kenneth", false)
     */
    override fun <A, B> bind(v: WriterTOf<F, W, A>, f: (A) -> WriterTOf<F, W, B>): WriterT<F, W, B> {
        val vWriter: WriterT<F, W, A> = v.narrow()
        return WriterT(monad().bind(vWriter.run){vPair: Pair<W, A> ->
            val fWriter: WriterT<F, W, B> = f(vPair.second).narrow()
            monad().fmap(fWriter.run){pair: Pair<W, B> ->
                Pair(mw.run{combine(vPair.first, pair.first)}, pair.second)
            }
        })
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> WriterTOf<F, W, Either<B, C>>): WriterT<F, W, C> {
        fun recTailRecM(b: B, f: (B) -> WriterT<F, W, Either<B, C>>): WriterT<F, W, C> {
            val mf: Monad<F> = monad()
            return writert(mf.tailRecM(b){bb: B ->
                val wbc: Kind1<F, Pair<W, Either<B, C>>> = f(bb).run
                mf.run{
                    mf.fmap(wbc){pair: Pair<W, Either<B, C>> ->
                        when (val either: Either<B, C> = pair.second) {
                            is Left -> Left(either.value)
                            is Right -> Right(Pair(pair.first, either.value))
                        }
                    }
                }
            })
        }   // recTailRecM

        val g: (B) -> WriterT<F, W, Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(b, g)
    }   // tailRecM

}   // WriterTMonad
