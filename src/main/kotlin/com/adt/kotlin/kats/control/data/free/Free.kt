package com.adt.kotlin.kats.control.data.free

import com.adt.kotlin.kats.control.data.free.Free.FreeProxy
import com.adt.kotlin.kats.control.instances.applicative.FreeApplicative
import com.adt.kotlin.kats.control.instances.foldable.FreeFoldable
import com.adt.kotlin.kats.control.instances.functor.FreeFunctor
import com.adt.kotlin.kats.control.instances.monad.FreeMonad
import com.adt.kotlin.kats.control.instances.traversable.FreeTraversable

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some

import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose
import com.adt.kotlin.kats.hkfp.fp.FunctionF.forwardCompose
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*


typealias FreeOf<F, A> = Kind1<Kind1<FreeProxy, F>, A>

sealed class Free<F, A> : Kind1<Kind1<FreeProxy, F>, A> {

    class FreeProxy private constructor()           // proxy for the Free context


    class Pure<F, A>(val a: A) : Free<F, A>()

    class Bind<F, A>(val free: Kind1<F, Free<F, A>>) : Free<F, A>()


    /**
     * retract is the left inverse of lift and liftF
     */
    fun retract(mf: Monad<F>): Kind1<F, A> =
            when (this) {
                is Pure -> mf.inject(this.a)
                is Bind -> mf.bind(this.free) { fa: Free<F, A> -> fa.retract(mf) }
            }

    /**
     * Tear down a Free monad using iteration.
     */
    fun iter(ff: Functor<F>, f: (Kind1<F, A>) -> A): A =
            when (this) {
                is Pure -> this.a
                is Bind -> f(ff.fmap(this.free) { free: Free<F, A> -> free.iter(ff, f) })
            }

    /**
     * Like iter but for applicative values.
     */
    fun <P> iterA(ap: Applicative<P>, ff: Functor<F>, f: (Kind1<F, Kind1<P, A>>) -> Kind1<P, A>): Kind1<P, A> =
            when (this) {
                is Pure -> ap.pure(this.a)
                is Bind -> f(ff.fmap(this.free) { free: Free<F, A> -> free.iterA(ap, ff, f) })
            }

    /**
     * Like iter but for monadic values.
     */
    fun <M> iterM(mm: Monad<M>, ff: Functor<F>, f: (Kind1<F, Kind1<M, A>>) -> Kind1<M, A>): Kind1<M, A> =
            when (this) {
                is Pure -> mm.inject(this.a)
                is Bind -> f(ff.fmap(this.free) { free: Free<F, A> -> free.iterM(mm, ff, f) })
            }

    /**
     * Lift a natural transformation from f to g into a natural transformation
     *   from Free f to Free g.
     */
    fun <G, B> hoistFree(fg: Functor<G>, f: (Kind1<F, *>) -> Kind1<G, *>, fb: Free<F, B>): Free<G, B> {
        fun <B> recHoistFree(g: (Kind1<F, *>) -> Kind1<G, *>, fb: Free<F, B>): Free<G, B> {
            val recHoistFreeC: ((Kind1<F, *>) -> Kind1<G, *>) -> (Free<F, B>) -> Free<G, B> =
                    { fff -> { ffb -> recHoistFree(fff, ffb) } }
            return when (fb) {
                is Pure -> Pure(fb.a)
                is Bind -> {
                    @Suppress("UNCHECKED_CAST")
                    val gfb: Kind1<G, Free<F, B>> = g(fb.free) as Kind1<G, Free<F, B>>
                    Bind(fg.fmap(gfb, recHoistFreeC(g)))
                }
            }
        }   // recHoistFree

        return recHoistFree(f, fb)
    }   // hoistFree

    /**
     * The very definition of a free monad is that given a natural transformation
     *   you get a monad homomorphism.
     */
    fun <M> foldFree(mm: Monad<M>, f: (Kind1<F, *>) -> Kind1<M, *>, fa: Free<F, A>): Kind1<M, A> {
        fun recFoldFree(g: (Kind1<F, *>) -> Kind1<M, *>, fa: Free<F, A>): Kind1<M, A> {
            val recFoldFreeC: ((Kind1<F, *>) -> Kind1<M, *>) -> (Free<F, A>) -> Kind1<M, A> =
                    { ffm -> { fa -> recFoldFree(ffm, fa) } }
            return when (fa) {
                is Pure -> mm.inject(fa.a)
                is Bind -> {
                    @Suppress("UNCHECKED_CAST")
                    val gfa: Kind1<M, Free<F, A>> = g(fa.free) as Kind1<M, Free<F, A>>
                    mm.bind(gfa, recFoldFreeC(g))
                }
            }
        }   // recFoldFree

        return recFoldFree(f, fa)
    }   // foldFree

    /**
     * Cuts off a tree of computations at a given depth. If the depth is 0 or
     *   less, no computation nor monadic effects will take place.
     */
    fun cutoff(mf: Monad<F>, n: Int): Free<F, Option<A>> {
        val self: Free<F, A> = this
        @Suppress("UNCHECKED_CAST")
        return mf.run{
            if (n <= 0)
                Pure(none())
            else when (self) {
                is Pure -> Pure(some(self.a))
                is Bind -> {
                    val cutoffC: (Int) -> (FreeOf<F, A>) -> Free<F, Option<A>> =
                            { n ->
                                { ffa ->
                                    ffa.narrow().cutoff(mf, n)
                                }
                            }
                    val ffoa: Kind1<F, Free<F, Option<A>>> = fmap(self.free, cutoffC(n - 1))
                    val bind: Free<F, Option<A>> = Bind(ffoa)
                    bind
                }
            }
        }
    }   // cutoff

    /**
     * Unfold a free monad from a seed.
     */
    fun <B> unfold(ff: Functor<F>, f: (B) -> Either<A, Kind1<F, B>>, b: B): Free<F, A> {
        val unfoldC: (Functor<F>) -> ((B) -> Either<A, Kind1<F, B>>) -> (B) -> Free<F, A> =
                { fff -> { f -> { b -> unfold(fff, f, b) } } }
        return forwardCompose(f,
                { either: Either<A, Kind1<F, B>> ->
                    either.fold({ a: A -> Pure(a) }, { fb: Kind1<F, B> -> Bind(ff.fmap(fb, unfoldC(ff)(f))) })
                }
        )(b)
    }   // unfold

    /**
     * Unfold a free monad from a seed, monadically.
     */
    fun <M, B> unfoldM(tf: Traversable<F>, am: Applicative<M>, mm: Monad<M>, f: (B) -> Kind1<M, Either<A, Kind1<F, B>>>, b: B): Kind1<M, Free<F, A>> {
        val unfoldMC: ((B) -> Kind1<M, Either<A, Kind1<F, B>>>) -> (B) -> Kind1<M, Free<F, A>> =
                {g -> {b -> unfoldM(tf, am, mm, g, b)}}
        val bindC: (Kind1<F, Free<F, A>>) -> Free<F, A> = {ffa -> Bind(ffa)}

        fun <X, Y, Z> then(f: (X) -> Kind1<M, Y>, g: (Y) -> Kind1<M, Z>): ((X) -> Kind1<M, Z>) =
                {x: X -> mm.bind(f(x)){y: Y -> g(y)} }
        fun <G, X, Y> traverseC(f: (X) -> Kind1<G, Y>, ag: Applicative<G>): (Kind1<F, X>) -> Kind1<G, Kind1<F, Y>> =
                {fx -> tf.traverse(fx, ag, f)}
        fun <G, X, Y> fmapC(f: (X) -> Y, ff: Functor<G>): (Kind1<G, X>) -> Kind1<G, Y> = {gx -> ff.fmap(gx, f)}

        fun leftFold(a: A): Kind1<M, Free<F, A>> {
            val ffa: Free<F, A> = Pure(a)
            val mfa: Kind1<M, Free<F, A>> = mm.inject(ffa)
            return mfa
        }   // leftFold

        fun rightFold(fb: Kind1<F, B>): Kind1<M, Free<F, A>> {
            val fmapBind: (Kind1<M, Kind1<F, Free<F, A>>>) -> Kind1<M, Free<F, A>> = fmapC(bindC, am)
            val traverseUnfold: (Kind1<F, B>) -> Kind1<M, Kind1<F, Free<F, A>>> = traverseC(unfoldMC(f), am)
            val composed: (Kind1<F, B>) -> Kind1<M, Free<F, A>> = compose(fmapBind, traverseUnfold)
            return composed(fb)
        }   // rightFold

        fun fold(either: Either<A, Kind1<F, B>>): Kind1<M, Free<F, A>> =
                either.fold({a: A -> leftFold(a)}, {fb: Kind1<F, B> -> rightFold(fb)})
        val fb: (B) -> Kind1<M, Free<F, A>> = then(f){either: Either<A, Kind1<F, B>> -> fold(either)}
        return fb(b)
    }   // unfoldM



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F> functor(ff: Functor<F>): Functor<Kind1<FreeProxy, F>> = object: FreeFunctor<F> {
            override fun functor(): Functor<F> = ff
        }

        /**
         * Create an instance of this applicative.
         */
        fun <F> applicative(ff: Functor<F>): Applicative<Kind1<FreeProxy, F>> = object: FreeApplicative<F> {
            override fun functor(): Functor<F> = ff
        }

        /**
         * Create an instance of this monad.
         */
        fun <F> monad(ff: Functor<F>): Monad<Kind1<FreeProxy, F>> = object: FreeMonad<F> {
            override fun functor(): Functor<F> = ff
        }

        /**
         * Create an instance of this foldable.
         */
        fun <F> foldable(ff: Foldable<F>): Foldable<Kind1<FreeProxy, F>> = object: FreeFoldable<F> {
            override fun foldable(): Foldable<F> = ff
        }

        /**
         * Create an instance of this traversable.
         */
        fun <F> traversable(tf: Traversable<F>): Traversable<Kind1<FreeProxy, F>> = object: FreeTraversable<F> {
            override fun traversable(): Traversable<F> = tf
        }

        /**
         * Entry point for monad bindings which enables for comprehension.
         */
        fun <F, A> forC(ff: Functor<F>, block: suspend MonadSyntax<Kind1<FreeProxy, F>>.() -> A): Free<F, A> =
                monad(ff).forC.monad(block).narrow()

    }

}   // Free
