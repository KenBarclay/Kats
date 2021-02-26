package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.set.Set
import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface SetSemigroup<A : Comparable<A>> : Semigroup<Set<A>> {

    override fun combine(a: Set<A>, b: Set<A>): Set<A> =
            a.union(b)

}   // SetSemigroup
