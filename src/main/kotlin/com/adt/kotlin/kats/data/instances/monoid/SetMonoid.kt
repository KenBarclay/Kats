package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.set.Set
import com.adt.kotlin.kats.data.immutable.set.SetF
import com.adt.kotlin.kats.hkfp.typeclass.Monoid



class SetMonoid<A : Comparable<A>> : Monoid<Set<A>> {

    override val empty: Set<A> = SetF.empty()

    override fun combine(a: Set<A>, b: Set<A>): Set<A> =
            a.union(b)

}   // SetMonoid
