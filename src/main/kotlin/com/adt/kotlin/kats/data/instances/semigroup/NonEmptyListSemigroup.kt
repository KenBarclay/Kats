package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.list.ListF.cons
import com.adt.kotlin.kats.data.immutable.list.append
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList

import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



class NonEmptyListSemigroup<A> : Semigroup<NonEmptyList<A>> {

    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> =
            NonEmptyList(a.hd, a.tl.append(cons(b.hd, b.tl)))

}   // NonEmptyListSemigroup
