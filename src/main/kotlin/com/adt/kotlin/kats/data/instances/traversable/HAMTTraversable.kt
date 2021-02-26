package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.immutable.hamt.Map
import com.adt.kotlin.kats.data.immutable.hamt.Map.MapProxy
////import com.adt.kotlin.kats.data.immutable.hamt.Map.Tip
////import com.adt.kotlin.kats.data.immutable.hamt.Map.Bin
import com.adt.kotlin.kats.data.immutable.hamt.MapOf
import com.adt.kotlin.kats.data.immutable.hamt.narrow

import com.adt.kotlin.kats.data.instances.foldable.HAMTFoldable
import com.adt.kotlin.kats.data.instances.functor.HAMTFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface HAMTTraversable<K : Comparable<K>> : Traversable<Kind1<MapProxy, K>>, HAMTFoldable<K>, HAMTFunctor<K> {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    override fun <G, V, W> traverse(v: MapOf<K, V>, ag: Applicative<G>, f: (V) -> Kind1<G, W>): Kind1<G, Map<K, W>> {
        val vMap: Map<K, V> = v.narrow()
        return vMap.traverse(ag, f)
    }

}   // HAMTTraversable
