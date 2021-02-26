package com.adt.kotlin.kats.hkfp.laws

import com.adt.kotlin.kats.control.data.compose.Compose
import com.adt.kotlin.kats.control.data.compose.ComposeF.compose
import com.adt.kotlin.kats.control.data.compose.narrow
import com.adt.kotlin.kats.control.data.naturaltransformation.NaturalTransformation
import com.adt.kotlin.kats.data.immutable.identity.Identity

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



class TraversableLaws<F>(val traversable: Traversable<F>) {

    /**
     * The law says that all traversing with the Identity constructor does is
     *   wrap the structure with Identity, which amounts to doing nothing
     *   (as the original structure can be trivially recovered).
     */
    fun <A, B> identityLaw(fa: Kind1<F, A>, f: (A) -> Identity<B>): Boolean =
            traversable.run{
                traverse(fa, Identity.applicative(), f) == sequenceA(fmap(fa, f), Identity.applicative())
            }

    /**
     * The composition law states that it does not matter whether we perform two
     *   traversals separately (right side of the equation) or compose them in
     *   order to walk across the structure only once (left side). It is analogous,
     *   for instance, to the second functor law. The fmaps are needed because
     *   the second traversal (or the second part of the traversal, for the
     *   left side of the equation) happens below the layer of structure added
     *   by the first (part). Compose is needed so that the composed traversal
     *   is applied to the correct layer.
     */
    fun <G, H, A, B, C> compositionLaw(fa: Kind1<F, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>, ah: Applicative<H>, g: (B) -> Kind1<H, C>): Boolean {
        val tF: Traversable<F> = traversable

        val cgh = Compose.applicative(ag, ah)
        val lhs: Compose<G, H, Kind1<F, C>> = compose(ag.fmap(tF.traverse(fa, ag, f)){fb -> tF.traverse(fb, ah, g)})
        val rhs: Compose<G, H, Kind1<F, C>> = tF.traverse(fa, cgh){a -> compose(ag.fmap(f(a), g))}.narrow()

        return (lhs == rhs)
    }   // compositionLaw

    fun <G, H, A> naturalityLaw(nat: NaturalTransformation<G, H>, fga: Kind1<F, Kind1<G, A>>, ag: Applicative<G>, ah: Applicative<H>): Boolean {
        val tF: Traversable<F> = traversable

        val lhs: Kind1<H, Kind1<F, A>> = nat(tF.sequenceA(fga, ag))
        val rhs: Kind1<H, Kind1<F, A>> = tF.sequenceA(tF.fmap(fga){ma -> nat(ma)}, ah)

        return (lhs == rhs)
    }   // naturalityLaw

}   // TraversableLaws
