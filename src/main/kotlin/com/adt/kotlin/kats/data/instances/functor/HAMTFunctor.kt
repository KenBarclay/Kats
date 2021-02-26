package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.hamt.Map
import com.adt.kotlin.kats.data.immutable.hamt.Map.MapProxy
import com.adt.kotlin.kats.data.immutable.hamt.MapF
import com.adt.kotlin.kats.data.immutable.hamt.MapOf
import com.adt.kotlin.kats.data.immutable.hamt.narrow
import com.adt.kotlin.kats.data.immutable.list.ListOf
import com.adt.kotlin.kats.data.immutable.list.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface HAMTFunctor<K : Comparable<K>> : Functor<Kind1<MapProxy, K>> {

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
        val functor = List.functor()
        val vMap: Map<K, Pair<A, B>> = v.narrow()
        val list: List<Pair<K, Pair<A, B>>> = vMap.toList()
        val x: Pair<ListOf<K>, ListOf<Pair<A, B>>> = functor.distribute(list)
        val xk: List<K> = x.first.narrow()
        val xab: List<Pair<A, B>> = x.second.narrow()
        val y: Pair<ListOf<A>, ListOf<B>> = functor.distribute(x.second)
        val ya: List<A> = y.first.narrow()
        val yb: List<B> = y.second.narrow()
        val ka: List<Pair<K, A>> = xk.zip(ya)
        val kb: List<Pair<K, B>> = xk.zip(yb)
        val mka: Map<K, A> = MapF.from(ka)
        val mkb: Map<K, B> = MapF.from(kb)
        return Pair(mka, mkb)
    }   // distribute



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Map context.
     */
    override fun <V, W> lift(f: (V) -> W): (MapOf<K, V>) -> Map<K, W> =
            {mkv: MapOf<K, V> ->
                fmap(mkv, f)
            }

}   // HAMTFunctor
