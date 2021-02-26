package com.adt.kotlin.kats.control.data.kleisli

/**
 * Kleisli represents an arrow from A to a monadic value F<B>.
 *
 * @author	                    Ken Barclay
 * @since                       November 2018
 */

import com.adt.kotlin.kats.control.data.kleisli.Kleisli.KleisliProxy
import com.adt.kotlin.kats.control.data.kleisli.KleisliF.kleisli
import com.adt.kotlin.kats.control.instances.alternative.KleisliAlternative
import com.adt.kotlin.kats.control.instances.applicative.KleisliApplicative
import com.adt.kotlin.kats.control.instances.functor.KleisliFunctor
import com.adt.kotlin.kats.control.instances.monad.KleisliMonad
import com.adt.kotlin.kats.control.instances.monadplus.KleisliMonadPlus

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*


/**
 * Kleisli represents an arrow from A to a monadic value F<B>.
 *
 * @param A                     the dependency or environment we depend upon
 * @param F                     the context of the result
 * @param B                     resulting type of the computation
 * @param run                   run the arrow from A to F<B>
 */

typealias KleisliOf<F, A, B> = Kind1<Kind1<Kind1<KleisliProxy, F>, A>, B>

class Kleisli<F, A, B> internal constructor(val run: (A) -> Kind1<F, B>) : Kind1<Kind1<Kind1<KleisliProxy, F>, A>, B> {

    class KleisliProxy                     // proxy for the Kleisli context



    /**
     * Execute the Kleisli arrow against the given value.
     *
     * @param a                 parameter value for the arrow
     * @return                  computed context
     */
    operator fun invoke(a: A): Kind1<F, B> = run(a)

    /**
     * Composition of Kleisli arrow.
     *
     * Examples:
     *   let plus1 = Kleisli{n -> some(1 + n}
     *   let squared = Kleisli{n -> some(n * n)}
     *   plus1.compose(Option.monad(), squared)(2) == some(5)
     *   squared.compose(Option.monad(), plus1)(2) == some(9)
     */
    fun <C> compose(mf: Monad<F>, k: Kleisli<F, C, A>): Kleisli<F, C, B> =
            k.forwardCompose(mf, this)

    fun <C> compose(mf: Monad<F>, f: (C) -> Kind1<F, A>): Kleisli<F, C, B> =
            compose(mf, kleisli(f))

    /**
     * Forward composition of Kleisli arrow.
     *
     * Examples:
     *   let plus1 = Kleisli{n -> some(1 + n}
     *   let squared = Kleisli{n -> some(n * n)}
     *   plus1.forwardCompose(Option.monad(), squared)(2) == some(9)
     *   squared.forwardCompose(Option.monad(), plus1)(2) == some(5)
     */
    fun <C> forwardCompose(mf: Monad<F>, k: Kleisli<F, B, C>): Kleisli<F, A, C> =
            kleisli{a: A -> mf.bind(run(a)){b: B -> k.run(b)}}

    fun <C> forwardCompose(mf: Monad<F>, f: (B) -> Kind1<F, C>): Kleisli<F, A, C> =
            forwardCompose(mf, kleisli(f))

    fun <C> then(mf: Monad<F>, k: Kleisli<F, B, C>): Kleisli<F, A, C> = this.forwardCompose(mf, k)

    fun <C> then(mf: Monad<F>, f: (B) -> Kind1<F, C>): Kleisli<F, A, C> = this.forwardCompose(mf, f)

    /**
     * Map this Kleisli with the given function.
     *
     * Examples:
     *   let plus1 = Kleisli{n -> some(1 + n}
     *   let squared = Kleisli{n -> some(n * n)}
     *   plus1.map(Option.functor()){n -> isEven(n)}(2) == some(false)
     *   squared.map(Option.functor()){n -> isEven(n)}(2) == some(true)
     */
    fun <C> map(ff: Functor<F>, f: (B) -> C): Kleisli<F, A, C> =
            kleisli{a: A -> ff.fmap(run(a), f)}

    /**
     * Contramap on the first type parameter and map on the second type parameter.
     *
     * Examples:
     *   let plus1 = Kleisli{n -> some(1 + n}
     *   let squared = Kleisli{n -> some(n * n)}
     *   plus1.dimap(Option.functor(), {str -> str.length}, {n -> isEven(n)})("ken") == some(true)
     *   plus1.dimap(Option.functor(), {str -> str.length}, {n -> isEven(n)})("john") == some(false)
     *   squared.dimap(Option.functor(), {str -> str.length}, {n -> isEven(n)})("ken") == some(false)
     *   squared.dimap(Option.functor(), {str -> str.length}, {n -> isEven(n)})("john") == some(true)
     */
    fun <C, D> dimap(ff: Functor<F>, f: (C) -> A, g: (B) -> D): Kleisli<F, C, D> = ff.run{
        kleisli{c: C -> fmap(run(f(c)), g)}
    }   // dimap



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F, A> functor(ff: Functor<F>): Functor<Kind1<Kind1<KleisliProxy, F>, A>> =
                object: KleisliFunctor<F, A> {
                    override fun functor(): Functor<F> = ff
                }

        /**
         * Create an instance of this applicative.
         */
        fun <F, A> applicative(af: Applicative<F>): Applicative<Kind1<Kind1<KleisliProxy, F>, A>> =
                object: KleisliApplicative<F, A> {
                    override fun applicative(): Applicative<F> = af
                }

        /**
         * Create an instance of this monad.
         */
        fun <F, A> monad(mf: Monad<F>): Monad<Kind1<Kind1<KleisliProxy, F>, A>> =
                object: KleisliMonad<F, A> {
                    override fun monad(): Monad<F> = mf
                }

        /**
         * Create an instance of this alternative.
         */
        fun <F, A> alternative(af: Alternative<F>): Alternative<Kind1<Kind1<KleisliProxy, F>, A>> =
                object: KleisliAlternative<F, A> {
                    override fun alternative(): Alternative<F> = af
                }

        /**
         * Create an instance of this monadPlus.
         */
        fun <F, A> monadPlus(mf: MonadPlus<F>): MonadPlus<Kind1<Kind1<KleisliProxy, F>, A>> =
                object: KleisliMonadPlus<F, A> {
                    override fun monadPlus(): MonadPlus<F> = mf
                }

        /**
         * Entry point for monad bindings which enables for comprehension.
         */
        fun <F, A, B> forC(mf: Monad<F>, block: suspend MonadSyntax<Kind1<Kind1<KleisliProxy, F>, A>>.() -> B): Kleisli<F, A, B> =
                monad<F, A>(mf).forC.monad(block).narrow()

        /**********
        /**
         * Create an instance of this profunctor.
         */
        fun <F> profunctor(ff: Functor<F>): Profunctor<Kind1<KleisliProxy, F>> =
                object: KleisliProfunctor<F> {
                    override val functor: Functor<F> = ff
                }
        **********/

    }

}   // Kleisli
