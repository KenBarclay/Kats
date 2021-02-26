package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.map.*
import com.adt.kotlin.kats.data.immutable.map.Map
import com.adt.kotlin.kats.data.immutable.map.Map.MapProxy

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface MapFunctor<K : Comparable<K>> : Functor<Kind1<MapProxy, K>> {

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <V, W> fmap(v: MapOf<K, V>, f: (V) -> W): Map<K, W> {
        val vMap: Map<K, V> = v.narrow()
        return vMap.fmap(f)
    }   // fmap

    /**
     * Distribute the Map<K, (A, B)> over the pair to get (Map<K, A>, Map<K, B>).
     */
    override fun <A, B> distribute(v: MapOf<K, Pair<A, B>>): Pair<Map<K, A>, Map<K, B>> {
        val vMap: Map<K, Pair<A, B>> = v.narrow()
        return vMap.foldLeftWithKey(Pair(MapF.empty(), MapF.empty())){maps: Pair<Map<K, A>, Map<K, B>> ->
            {key: K ->
                {pair: Pair<A, B> ->
                    Pair(maps.first.insert(key, pair.first), maps.second.insert(key, pair.second))
                }
            }
        }
    }   // distribute



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Map context.
     */
    override fun <V, W> lift(f: (V) -> W): (MapOf<K, V>) -> Map<K, W> =
            {mkv: MapOf<K, V> ->
                fmap(mkv, f)
            }

}   // MapFunctor
