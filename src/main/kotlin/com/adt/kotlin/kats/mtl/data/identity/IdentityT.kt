package com.adt.kotlin.kats.mtl.data.identity

import com.adt.kotlin.kats.mtl.data.identity.IdentityT.IdentityTProxy
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*
import com.adt.kotlin.kats.mtl.instances.applicative.IdentityTApplicative
import com.adt.kotlin.kats.mtl.instances.foldable.IdentityTFoldable
import com.adt.kotlin.kats.mtl.instances.functor.IdentityTFunctor
import com.adt.kotlin.kats.mtl.instances.monad.IdentityTMonad
import com.adt.kotlin.kats.mtl.instances.traversable.IdentityTTraversable


typealias IdentityTOf<F, A> = Kind1<Kind1<IdentityTProxy, F>, A>

data class IdentityT<F, A>(val runIdentity: Kind1<F, A>) : Kind1<Kind1<IdentityTProxy, F>, A> {

    class IdentityTProxy private constructor()           // proxy for the IdentityT context



    fun <B> ap(af: Applicative<F>, f: IdentityT<F, (A) -> B>): IdentityT<F, B> =
            IdentityT(af.ap(runIdentity, f.runIdentity))

    fun <B> bind(mf: Monad<F>, f: (A) -> IdentityT<F, B>): IdentityT<F, B> =
            IdentityT(mf.bind(runIdentity){a: A -> f(a).runIdentity})

    fun <B> flatMap(mf: Monad<F>, f: (A) -> IdentityT<F, B>): IdentityT<F, B> =
            bind(mf, f)

    fun <B> foldLeft(ff: Foldable<F>, b: B, f: (B) -> (A) -> B): B =
            ff.foldLeft(runIdentity, b, f)

    fun <B> foldRight(ff: Foldable<F>, b: B, f: (A) -> (B) -> B): B =
            ff.foldRight(runIdentity, b, f)

    fun <B> map(ff: Functor<F>, f: (A) -> B): IdentityT<F, B> =
            IdentityT(ff.fmap(runIdentity, f))

    fun <G, B> traverse(tf: Traversable<F>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, IdentityT<F, B>> =
            ag.fmap(tf.traverse(runIdentity, ag, f)){run: Kind1<F, B> -> IdentityT(run)}



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F> functor(ff: Functor<F>): Functor<Kind1<IdentityTProxy, F>> = object: IdentityTFunctor<F> {
            override fun functor(): Functor<F> = ff
        }

        /**
         * Create an instance of this applicative.
         */
        fun <F> applicative(af: Applicative<F>): Applicative<Kind1<IdentityTProxy, F>> = object: IdentityTApplicative<F> {
            override fun applicative(): Applicative<F> = af
        }

        /**
         * Create an instance of this monad.
         */
        fun <F> monad(mf: Monad<F>): Monad<Kind1<IdentityTProxy, F>> = object: IdentityTMonad<F> {
            override fun monad(): Monad<F> = mf
        }

        /**
         * Create an instance of this foldable.
         */
        fun <F> foldable(ff: Foldable<F>): Foldable<Kind1<IdentityT.IdentityTProxy, F>> = object: IdentityTFoldable<F> {
            override fun foldable(): Foldable<F> = ff
        }

        /**
         * Create an instance of this traversable.
         */
        fun <F> traversable(tf: Traversable<F>): Traversable<Kind1<IdentityT.IdentityTProxy, F>> = object: IdentityTTraversable<F> {
            override fun traversable(): Traversable<F> = tf
        }

    }

}   // IdentityT
