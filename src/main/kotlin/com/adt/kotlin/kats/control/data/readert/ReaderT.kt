package com.adt.kotlin.kats.control.data.readert

import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.readert.ReaderT.ReaderTProxy
import com.adt.kotlin.kats.control.instances.applicative.ReaderTApplicative
import com.adt.kotlin.kats.control.instances.functor.ReaderTFunctor
import com.adt.kotlin.kats.control.instances.monad.ReaderTMonad

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*



typealias ReaderTOf<F, A, B> = Kind1<Kind1<Kind1<ReaderTProxy, F>, A>, B>

open class ReaderT<F, A, B> internal constructor(val run: (A) -> Kind1<F, B>) : Kind1<Kind1<Kind1<ReaderTProxy, F>, A>, B> {

    class ReaderTProxy



    /**
     * Execute the ReaderT arrow against the given value.
     *
     * @param a                 parameter value for the arrow
     * @return                  computed context
     */
    open operator fun invoke(a: A): Kind1<F, B> = run(a)

    /**
     * Composition of ReaderT arrow.
     *
     * Examples:
     */
    fun <C> compose(mf: Monad<F>, rfca: ReaderT<F, C, A>): ReaderT<F, C, B> =
            rfca.forwardCompose(mf, this)

    fun <C> compose(mf: Monad<F>, f: (C) -> Kind1<F, A>): ReaderT<F, C, B> =
            compose(mf, ReaderTF.readert(f))

    /**
     * Forward composition of ReaderT arrow.
     *
     * Examples:
     */
    fun <C> forwardCompose(mf: Monad<F>, rfcb: ReaderT<F, B, C>): ReaderT<F, A, C> =
            ReaderTF.readert { a: A -> mf.bind(run(a)) { b: B -> rfcb.run(b) } }

    fun <C> forwardCompose(mf: Monad<F>, f: (B) -> Kind1<F, C>): ReaderT<F, A, C> =
            forwardCompose(mf, ReaderTF.readert(f))

    fun <C> then(mf: Monad<F>, rfcb: ReaderT<F, B, C>): ReaderT<F, A, C> = this.forwardCompose(mf, rfcb)

    fun <C> then(mf: Monad<F>, f: (B) -> Kind1<F, C>): ReaderT<F, A, C> = this.forwardCompose(mf, f)

    /**
     * Map this Kleisli with the given function.
     *
     * Examples:
     */
    fun <C> map(ff: Functor<F>, f: (B) -> C): ReaderT<F, A, C> =
            ReaderTF.readert { a: A -> ff.fmap(run(a), f) }

    /**
     * Contramap on the first type parameter and map on the second type parameter.
     *
     * Examples:
     */
    fun <C, D> dimap(ff: Functor<F>, f: (C) -> A, g: (B) -> D): ReaderT<F, C, D> = ff.run{
        ReaderTF.readert { c: C -> fmap(run(f(c)), g) }
    }   // dimap



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F, A> functor(ff: Functor<F>): Functor<Kind1<Kind1<ReaderTProxy, F>, A>> =
                object: ReaderTFunctor<F, A> {
                    override fun functor(): Functor<F> = ff
                }

        /**
         * Create an instance of this applicative.
         */
        fun <F, A> applicative(af: Applicative<F>): Applicative<Kind1<Kind1<ReaderTProxy, F>, A>> =
                object: ReaderTApplicative<F, A> {
                    override fun applicative(): Applicative<F> = af
                }

        /**
         * Create an instance of this monad.
         */
        fun <F, A> monad(mf: Monad<F>): Monad<Kind1<Kind1<ReaderTProxy, F>, A>> =
                object: ReaderTMonad<F, A> {
                    override fun monad(): Monad<F> = mf
                }

        /**
         * Entry point for monad bindings which enables for comprehension.
         */
        fun <F, A, B> forC(mf: Monad<F>, block: suspend MonadSyntax<Kind1<Kind1<ReaderTProxy, F>, A>>.() -> B): ReaderT<F, A, B> =
                monad<F, A>(mf).forC.monad(block).narrow()

        /**********
        /**
         * Create an instance of this alternative.
         */
        fun <F, A> alternative(altf: Alternative<F>): Alternative<Kind1<Kind1<ReaderTProxy, F>, A>> =
                object: ReaderTAlternative<F, A> {
                    override fun alternative(): Alternative<F> = altf
                }

        /**
         * Create an instance of this monadPlus.
         */
        fun <F, A> monadPlus(mpf: MonadPlus<F>): MonadPlus<Kind1<Kind1<ReaderTProxy, F>, A>> =
                object: ReaderTMonadPlus<F, A> {
                    override fun monadPlus(): MonadPlus<F> = mpf
                }
        **********/

    }

}   // ReaderT
