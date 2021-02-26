package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.append

import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



/**
 * Semigroup that concatenates lists.
 */
class ListSemigroup<A> : Semigroup<List<A>> {

    override fun combine(a: List<A>, b: List<A>): List<A> = a.append(b)

}   // ListSemigroup
