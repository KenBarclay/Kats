package com.adt.kotlin.kats.mtl.data.option

/**
 * The OptionT monad transformer extends a monad with the ability to exit the
 *   computation without returning a value. A sequence of actions produces a
 *   value only if all the actions in the sequence do. If one exits, the rest
 *   of the sequence is skipped and the composite action exits.
 *
 * The parameterizable OptionT monad, obtained by composing an arbitrary monad
 *   with the Option monad. Computations are actions that may produce a value
 *   or exit. The inject function yields a computation that produces that value,
 *   while bind sequences two subcomputations, exiting if either computation does.
 *
 * @author	                    Ken Barclay
 * @since                       January 2021
 */

import com.adt.kotlin.kats.mtl.data.option.OptionT.OptionTProxy

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.getOrElse
import com.adt.kotlin.kats.data.immutable.option.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*
import com.adt.kotlin.kats.mtl.instances.applicative.OptionTApplicative
import com.adt.kotlin.kats.mtl.instances.foldable.OptionTFoldable
import com.adt.kotlin.kats.mtl.instances.functor.OptionTFunctor
import com.adt.kotlin.kats.mtl.instances.monad.OptionTMonad
import com.adt.kotlin.kats.mtl.instances.traversable.OptionTTraversable



typealias OptionTOf<F, A> = Kind1<Kind1<OptionTProxy, F>, A>

data class OptionT<F, A>(val runOptionT: Kind1<F, Option<A>>) : Kind1<Kind1<OptionTProxy, F>, A> {

    class OptionTProxy private constructor()           // proxy for the OptionT context



    fun <B> ap(mf: Monad<F>, f: OptionTOf<F, (A) -> B>): OptionT<F, B> {
        val fOptionT: OptionT<F, (A) -> B> = f.narrow()
        return bind(mf){a: A -> fOptionT.map(mf){g: (A) -> B -> g(a)} }
    }   // ap

    fun <B> bind(mf: Monad<F>, f: (A) -> OptionTOf<F, B>): OptionT<F, B> =
            bindF(mf){a: A ->
                val fb: OptionT<F, B> = f(a).narrow()
                fb.runOptionT
            }   // bind

    fun <B> bindF(mf: Monad<F>, f: (A) -> Kind1<F, Option<B>>): OptionT<F, B> =
            mf.run{
                OptionT(bind(runOptionT){option: Option<A> -> option.fold({ inject(none()) }, f)})
            }   // bindF

    fun <B> flatMap(mf: Monad<F>, f: (A) -> OptionTOf<F, B>): OptionT<F, B> = bind(mf, f)

    fun <B> cata(ff: Functor<F>, default: () -> B, f: (A) -> B): Kind1<F, B> =
            fold(ff, default, f)

    fun filter(ff: Functor<F>, predicate: (A) -> Boolean): OptionT<F, A> =
            ff.run{
                OptionT(fmap(runOptionT){option: Option<A> -> option.filter(predicate)})
            }   // filter

    fun <B> filterMap(ff: Functor<F>, f: (A) -> OptionOf<B>): OptionT<F, B> {
        val g: (A) -> Option<B> = {a: A -> f(a).narrow()}
        return ff.run{
            OptionT(fmap(runOptionT){option: Option<A> -> option.bind(g)})
        }
    }   // filterMap

    fun <B> fold(ff: Functor<F>, default: () -> B, f: (A) -> B): Kind1<F, B> =
            ff.run{
                fmap(runOptionT){option: Option<A> -> option.fold(default, f)}
            }   // fold

    fun forAll(ff: Functor<F>, predicate: (A) -> Boolean): Kind1<F, Boolean> =
            ff.run{
                fmap(runOptionT){option: Option<A> -> option.forAll(predicate)}
            }   // forAll

    fun getOrElse(ff: Functor<F>, default: () -> A): Kind1<F, A> =
            ff.run{
                fmap(runOptionT){option: Option<A> -> option.getOrElse(default())}
            }   // getOrElse

    fun getOrElseF(mf: Monad<F>, default: () -> Kind1<F, A>): Kind1<F, A> =
            mf.run{
                bind(runOptionT){option: Option<A> -> option.fold(default, ::pure)}
            }

    fun isDefined(ff: Functor<F>): Kind1<F, Boolean> =
            ff.run{
                fmap(runOptionT){option: Option<A> -> option.isDefined()}
            }   // isDefined

    fun isEmpty(ff: Functor<F>): Kind1<F, Boolean> =
            ff.run{
                fmap(runOptionT){option: Option<A> -> option.isEmpty()}
            }   // isEmpty

    fun <B> liftF(ff: Functor<F>, fb: Kind1<F, B>): OptionT<F, B> =
            ff.run{
                OptionT(fmap(fb){b: B -> some(b) })
            }   // liftF

    fun <B> map(ff: Functor<F>, f: (A) -> B): OptionT<F, B> =
            ff.run{
                OptionT(fmap(runOptionT){option: Option<A> -> option.map(f)})
            }   // map

    fun orElseF(mf: Monad<F>, default: () -> Kind1<F, Option<A>>): OptionT<F, A> =
            mf.run{
                OptionT(bind(runOptionT){option: Option<A> ->
                    when (option) {
                        is None -> default()
                        is Some -> pure(option)
                    }
                })
            }   // orElseF

    fun orElse(mf: Monad<F>, default: () -> OptionT<F, A>): OptionT<F, A> =
            orElseF(mf){ default().runOptionT }

    fun <B> semiBind(mf: Monad<F>, f: (A) -> Kind1<F, B>): OptionT<F, B> =
            bind(mf){a: A -> liftF(mf, f(a))}

    fun <B> subBind(ff: Functor<F>, f: (A) -> OptionOf<B>): OptionT<F, B> {
        val g: (A) -> Option<B> = {a: A -> f(a).narrow()}
        return transform(ff){option: Option<A> -> option.bind(g)}
    }   // subBind

    fun <B> subflatMap(ff: Functor<F>, f: (A) -> OptionOf<B>): OptionT<F, B> = subBind(ff, f)

    fun <B> transform(ff: Functor<F>, f: (Option<A>) -> Option<B>): OptionT<F, B> =
            ff.run{
                OptionT(fmap(runOptionT, f))
            }   // transform



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F> functor(ff: Functor<F>): Functor<Kind1<OptionTProxy, F>> = object: OptionTFunctor<F> {
            override fun functor(): Functor<F> = ff
        }

        /**
         * Create an instance of this applicative.
         */
        fun <F> applicative(mf: Monad<F>): Applicative<Kind1<OptionTProxy, F>> = object: OptionTApplicative<F> {
            override fun monad(): Monad<F> = mf
        }

        /**
         * Create an instance of this monad.
         */
        fun <F> monad(mf: Monad<F>): Monad<Kind1<OptionTProxy, F>> = object: OptionTMonad<F> {
            override fun monad(): Monad<F> = mf
        }

        /**
         * Create an instance of this foldable.
         */
        fun <F> foldable(ff: Foldable<F>): Foldable<Kind1<OptionTProxy, F>> = object: OptionTFoldable<F> {
            override fun foldable(): Foldable<F> = ff
        }

        /**
         * Create an instance of this traversable.
         */
        fun <F> traversable(tf: Traversable<F>): Traversable<Kind1<OptionTProxy, F>> = object: OptionTTraversable<F> {
            override fun traversable(): Traversable<F> = tf
        }

    }

}   // OptionT
