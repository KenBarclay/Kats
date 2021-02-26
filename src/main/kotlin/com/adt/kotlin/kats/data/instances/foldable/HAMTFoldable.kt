package com.adt.kotlin.kats.data.instances.foldable

import com.adt.kotlin.kats.data.immutable.hamt.Map
import com.adt.kotlin.kats.data.immutable.hamt.Map.MapProxy
import com.adt.kotlin.kats.data.immutable.hamt.MapOf
import com.adt.kotlin.kats.data.immutable.hamt.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface HAMTFoldable<K : Comparable<K>> : Foldable<Kind1<MapProxy, K>> {

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <V, W> foldLeft(v: MapOf<K, V>, e: W, f: (W) -> (V) -> W): W {
        val vMap: Map<K, V> = v.narrow()
        return vMap.foldLeft(e, f)
    }   // foldLeft

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <V, W> foldRight(v: MapOf<K, V>, e: W, f: (V) -> (W) -> W): W {
        val vMap: Map<K, V> = v.narrow()
        return vMap.foldRight(e, f)
    }   // foldRight

}   // HAMTFoldable
