package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.immutable.map.Map
import com.adt.kotlin.kats.data.immutable.map.Map.MapProxy
import com.adt.kotlin.kats.data.immutable.map.MapOf
import com.adt.kotlin.kats.data.immutable.map.narrow

import com.adt.kotlin.kats.data.instances.foldable.MapFoldable
import com.adt.kotlin.kats.data.instances.functor.MapFunctor
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface MapTraversable<K : Comparable<K>> : Traversable<Kind1<MapProxy, K>>, MapFoldable<K>, MapFunctor<K> {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    override fun <G, V, W> traverse(v: MapOf<K, V>, ag: Applicative<G>, f: (V) -> Kind1<G, W>): Kind1<G, Map<K, W>> {
        val vMap: Map<K, V> = v.narrow()
        return vMap.traverse(ag, f)
    }

}   // MapTraversable
