package com.adt.kotlin.kats.control.instances.monad

import com.adt.kotlin.kats.control.data.readert.ReaderT
import com.adt.kotlin.kats.control.data.readert.ReaderT.ReaderTProxy
import com.adt.kotlin.kats.control.data.readert.ReaderTF
import com.adt.kotlin.kats.control.data.readert.ReaderTF.readert
import com.adt.kotlin.kats.control.data.readert.ReaderTOf
import com.adt.kotlin.kats.control.data.readert.narrow
import com.adt.kotlin.kats.control.instances.applicative.ReaderTApplicative
import com.adt.kotlin.kats.data.immutable.either.Either

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface ReaderTMonad<F, A> : Monad<Kind1<Kind1<ReaderTProxy, F>, A>>, ReaderTApplicative<F, A> {

    fun monad(): Monad<F>

    override fun applicative(): Applicative<F> = monad()

    /**
     * Inject a value into the monadic type.
     */
    override fun <B> inject(a: B): ReaderT<F, A, B> = readert { _: A -> monad().inject(a) }

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <B, C> bind(v: ReaderTOf<F, A, B>, f: (B) -> ReaderTOf<F, A, C>): ReaderT<F, A, C> {
        val vReaderT: ReaderT<F, A, B> = v.narrow()
        val g: (B) -> ReaderT<F, A, C> = { b -> f(b).narrow()}
        return readert { a: A -> monad().bind(vReaderT.run(a)) { b: B -> g(b).run(a) } }
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> ReaderTOf<F, A, Either<B, C>>): ReaderT<F, A, C> {
        fun recTailRecM(mf: Monad<F>, b: B, f: (B) -> ReaderT<F, A, Either<B, C>>): ReaderT<F, A, C> {
            return ReaderTF.readert { a: A -> mf.tailRecM(b) { bb: B -> f(bb)(a) } }
        }   // recTailRecM

        val g: (B) -> ReaderT<F, A, Either<B, C>> = { bb: B -> f(bb).narrow()}
        return recTailRecM(monad(), b, g)
    }   // tailRecM

}   // ReaderTMonad
