package com.adt.kotlin.kats.mtl.data.either

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.EitherF.left
import com.adt.kotlin.kats.data.immutable.either.EitherF.right
import com.adt.kotlin.kats.mtl.data.either.EitherT.EitherTProxy
import com.adt.kotlin.kats.data.immutable.either.bind
import com.adt.kotlin.kats.data.instances.applicative.EitherApplicative
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*
import com.adt.kotlin.kats.mtl.instances.applicative.EitherTApplicative
import com.adt.kotlin.kats.mtl.instances.foldable.EitherTFoldable
import com.adt.kotlin.kats.mtl.instances.functor.EitherTFunctor
import com.adt.kotlin.kats.mtl.instances.monad.EitherTMonad
import com.adt.kotlin.kats.mtl.instances.traversable.EitherTTraversable


typealias EitherTOf<F, A, B> = Kind1<Kind1<Kind1<EitherTProxy, F>, A>, B>

data class EitherT<F, A, B>(val runEitherT: Kind1<F, Either<A, B>>) : Kind1<Kind1<Kind1<EitherTProxy, F>, A>, B> {

    class EitherTProxy private constructor()           // proxy for the EitherT context



    fun <C> ap(mf: Monad<F>, f: EitherTOf<F, A, (B) -> C>): EitherT<F, A, C> =
            bind(mf){b: B ->
                val fEitherT: EitherT<F, A, (B) -> C> = f.narrow()
                fEitherT.map(mf){g: (B) -> C -> g(b)}
            }   // ap

    fun <C> bindF(mf: Monad<F>, f: (B) -> Kind1<F, Either<A, C>>): EitherT<F, A, C> =
            mf.run{
                EitherT(bind(runEitherT){either: Either<A, B> ->
                    either.fold({a: A -> mf.inject(left(a))}, {b: B -> f(b)})
                })
            }   // bindF

    fun <C> bind(mf: Monad<F>, f: (B) -> EitherTOf<F, A, C>): EitherT<F, A, C> =
            bindF(mf){b: B ->
                val et: EitherTOf<F, A, C> = f(b)
                val etfac: EitherT<F, A, C> = et.narrow()
                etfac.runEitherT
            }   // bind

    fun <C> flatMap(mf: Monad<F>, f: (B) -> EitherTOf<F, A, C>): EitherT<F, A, C> =
            bind(mf, f)

    fun <C> cata(ff: Functor<F>, left: (A) -> C, right: (B) -> C): Kind1<F, C> =
            fold(ff, left, right)

    fun exists(ff: Functor<F>, predicate: (B) -> Boolean): Kind1<F, Boolean> =
            ff.run{
                fmap(runEitherT){eab: Either<A, B> -> eab.exists(predicate)}
            }   // exists

    fun <C> fold(ff: Functor<F>, foldLeft: (A) -> C, foldRight: (B) -> C): Kind1<F, C> =
            ff.run{
                fmap(runEitherT){either: Either<A, B> -> either.fold(foldLeft, foldRight)}
            }   // fold

    fun <C> liftF(ff: Functor<F>, fc: Kind1<F, C>): EitherT<F, A, C> =
            ff.run{
                EitherT(fmap(fc){c: C -> right(c)})
            }   // liftF

    fun <C> map(ff: Functor<F>, f: (B) -> C): EitherT<F, A, C> =
            ff.run{
                EitherT(fmap(runEitherT){eab: Either<A, B> ->
                    val eac: Either<A, C> = eab.map(f)
                    eac
                })
            }   // map

    fun <C> mapLeft(ff: Functor<F>, f: (A) -> C): EitherT<F, C, B> =
            ff.run{
                EitherT(fmap(runEitherT){eab: Either<A, B> ->
                    eab.mapLeft(f)
                })
            }   // mapLeft

    fun <C> semiBind(mf: Monad<F>, f: (B) -> Kind1<F, C>): EitherT<F, A, C> =
            bind(mf){b: B -> liftF(mf, f(b))}

    fun <C> semiflatMap(mf: Monad<F>, f: (B) -> Kind1<F, C>): EitherT<F, A, C> =
            semiBind(mf, f)

    fun <C> subBind(ff: Functor<F>, f: (B) -> Either<A, C>): EitherT<F, A, C> =
            transform(ff){eab: Either<A, B> -> eab.bind(f)}

    fun <C> subflatMap(ff: Functor<F>, f: (B) -> Either<A, C>): EitherT<F, A, C> =
            subBind(ff, f)

    fun <C, D> transform(ff: Functor<F>, f: (Either<A, B>) -> Either<C, D>): EitherT<F, C, D> =
            ff.run{
                EitherT(fmap(runEitherT){eab: Either<A, B> -> f(eab)})
            }   // transform



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F, A> functor(mf: Monad<F>): Functor<Kind1<Kind1<EitherTProxy, F>, A>> = object: EitherTFunctor<F, A> {
            override fun monad(): Monad<F> = mf
        }

        /**
         * Create an instance of this applicative.
         */
        fun <F, A> applicative(mf: Monad<F>): Applicative<Kind1<Kind1<EitherTProxy, F>, A>> = object: EitherTApplicative<F, A> {
            override fun monad(): Monad<F> = mf
        }

        /**
         * Create an instance of this monad.
         */
        fun <F, A> monad(mf: Monad<F>): Monad<Kind1<Kind1<EitherTProxy, F>, A>> = object: EitherTMonad<F, A> {
            override fun monad(): Monad<F> = mf
        }

        /**
         * Create an instance of this foldable.
         */
        fun <F, A> foldable(ff: Foldable<F>): Foldable<Kind1<Kind1<EitherTProxy, F>, A>> = object: EitherTFoldable<F, A> {
            override fun foldable(): Foldable<F> = ff
        }

        /**
         * Create an instance of this traversable.
         */
        fun <F, A> traversable(tf: Traversable<F>, mf: Monad<F>): Traversable<Kind1<Kind1<EitherTProxy, F>, A>> = object: EitherTTraversable<F, A> {
            override fun traversable(): Traversable<F> = tf
            override fun monad(): Monad<F> = mf
        }

    }

}   // EitherT
