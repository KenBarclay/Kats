package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.map.Map
import com.adt.kotlin.kats.data.immutable.map.union

import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface MapSemigroup<K : Comparable<K>, V> : Semigroup<Map<K, V>> {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun combine(map1: Map<K, V>, map2: Map<K, V>): Map<K, V> =
            map1.union(map2)

}   // MapSemigroup
