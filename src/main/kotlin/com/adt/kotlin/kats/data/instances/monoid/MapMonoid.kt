package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.map.Map
import com.adt.kotlin.kats.data.immutable.map.MapF
import com.adt.kotlin.kats.data.immutable.map.union

import com.adt.kotlin.kats.hkfp.typeclass.Monoid



class MapMonoid<K : Comparable<K>, V> : Monoid<Map<K, V>> {

    override val empty: Map<K, V> = MapF.empty()

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun combine(map1: Map<K, V>, map2: Map<K, V>): Map<K, V> =
            map1.union(map2)

}   // MapMonoid
