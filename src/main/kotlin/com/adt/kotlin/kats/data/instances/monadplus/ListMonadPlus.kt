package com.adt.kotlin.kats.data.instances.monadplus

import com.adt.kotlin.kats.data.immutable.list.*
import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.ListProxy
import com.adt.kotlin.kats.data.instances.alternative.ListAlternative
import com.adt.kotlin.kats.data.instances.monad.ListMonad

import com.adt.kotlin.kats.hkfp.typeclass.MonadPlus



interface ListMonadPlus : MonadPlus<ListProxy>, ListAlternative, ListMonad {

    /**
     * An empty result.
     */
    override fun <A> mzero(): List<A> = ListF.empty()

    /**
     * Combine two results into one.
     */
    override fun <A> mplus(fa1: ListOf<A>, fa2: ListOf<A>): List<A> {
        val fa1List: List<A> = fa1.narrow()
        val fa2List: List<A> = fa2.narrow()
        return fa1List.append(fa2List)
    }   // mplus

}   // ListMonadPlus
