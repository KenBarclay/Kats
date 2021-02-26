package com.adt.kotlin.kats.control.data.compose

import com.adt.kotlin.kats.control.data.compose.Compose.ComposeProxy
import com.adt.kotlin.kats.control.instances.applicative.ComposeApplicative
import com.adt.kotlin.kats.control.instances.foldable.ComposeFoldable
import com.adt.kotlin.kats.control.instances.functor.ComposeFunctor
import com.adt.kotlin.kats.control.instances.traversable.ComposeTraversable

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Traversable


typealias ComposeOf<F, G, A> = Kind1<Kind1<Kind1<ComposeProxy, F>, G>, A>

data class Compose<F, G, A> internal constructor(val compose: Kind1<F, Kind1<G, A>>) : Kind1<Kind1<Kind1<ComposeProxy, F>, G>, A> {

    class ComposeProxy private constructor()           // proxy for the Compose context



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F, G> functor(ff: Functor<F>, fg: Functor<G>): Functor<Kind1<Kind1<ComposeProxy, F>, G>> = object: ComposeFunctor<F, G> {
            override fun functorF(): Functor<F> = ff
            override fun functorG(): Functor<G> = fg

        }

        /**
         * Create an instance of this applicative.
         */
        fun <F, G> applicative(af: Applicative<F>, ag: Applicative<G>): Applicative<Kind1<Kind1<ComposeProxy, F>, G>> = object: ComposeApplicative<F, G> {
            override fun applicativeF(): Applicative<F> = af
            override fun applicativeG(): Applicative<G> = ag
        }

        /**
         * Create an instance of this foldable.
         */
        fun <F, G> foldable(ff: Foldable<F>, fg: Foldable<G>): Foldable<Kind1<Kind1<ComposeProxy, F>, G>> = object: ComposeFoldable<F, G> {
            override fun foldableF(): Foldable<F> = ff
            override fun foldableG(): Foldable<G> = fg
        }

        /**
         * Create an instance of this traversable.
         */
        fun <F, G> traversable(tf: Traversable<F>, tg: Traversable<G>): Traversable<Kind1<Kind1<ComposeProxy, F>, G>> = object: ComposeTraversable<F, G> {
            override fun traversableF(): Traversable<F> = tf
            override fun traversableG(): Traversable<G> = tg
        }

    }

}   // Compose
