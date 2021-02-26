package com.adt.kotlin.kats.data.instances.alternative

import com.adt.kotlin.kats.data.immutable.list.*
import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.ListProxy
import com.adt.kotlin.kats.data.instances.applicative.ListApplicative

import com.adt.kotlin.kats.hkfp.typeclass.Alternative



interface ListAlternative : Alternative<ListProxy>, ListApplicative {

    /**
     * The identity of combine.
     */
    override fun <A> empty(): List<A> = ListF.empty()

    /**
     * An associative binary operation.
     */
    override fun <A> combine(a: ListOf<A>, b: ListOf<A>): List<A> {
        val aList: List<A> = a.narrow()
        val bList: List<A> = b.narrow()
        return aList.append(bList)
    }   // combine

}   // ListAlternative
