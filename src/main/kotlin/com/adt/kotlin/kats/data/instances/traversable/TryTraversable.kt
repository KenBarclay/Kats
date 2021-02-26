package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.immutable.tri.Try
import com.adt.kotlin.kats.data.immutable.tri.Try.TryProxy
import com.adt.kotlin.kats.data.immutable.tri.narrow
import com.adt.kotlin.kats.data.instances.foldable.TryFoldable
import com.adt.kotlin.kats.data.instances.functor.TryFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface TryTraversable : Traversable<TryProxy>, TryFoldable, TryFunctor {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    override fun <G, A, B> traverse(v: Kind1<TryProxy, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Try<B>> {
        val vTry: Try<A> = v.narrow()
        return vTry.traverse(ag, f)
    }   //traverse

}   // TryTraversable
