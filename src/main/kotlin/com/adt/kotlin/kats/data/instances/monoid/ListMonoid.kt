package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.append

import com.adt.kotlin.kats.hkfp.typeclass.Monoid



/**
 * Monoid that combines lists.
 */
class ListMonoid<A> : Monoid<List<A>> {

    override val empty: List<A> = List.Nil
    override fun combine(a: List<A>, b: List<A>): List<A> = a.append(b)

}   // ListMonoid
