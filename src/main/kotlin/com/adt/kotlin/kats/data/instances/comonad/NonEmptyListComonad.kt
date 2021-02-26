package com.adt.kotlin.kats.data.instances.comonad

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.Nil
import com.adt.kotlin.kats.data.immutable.list.List.Cons
import com.adt.kotlin.kats.data.immutable.list.ListBuffer
import com.adt.kotlin.kats.data.immutable.list.ListBufferIF
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList.NonEmptyListProxy
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyListF.nonEmptyList
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyListF.singleton
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyListOf
import com.adt.kotlin.kats.data.immutable.nel.narrow
import com.adt.kotlin.kats.data.instances.functor.NonEmptyListFunctor
import com.adt.kotlin.kats.hkfp.typeclass.Comonad



interface NonEmptyListComonad : Comonad<NonEmptyListProxy>, NonEmptyListFunctor {

    override fun <A> extract(v: NonEmptyListOf<A>): A {
        val vNonEmptyList: NonEmptyList<A> = v.narrow()
        return vNonEmptyList.head()
    }   // extract

    override fun <A, B> coBind(v: NonEmptyListOf<A>, f: (NonEmptyListOf<A>) -> B): NonEmptyList<B> {
        val vNonEmptyList: NonEmptyList<A> = v.narrow()
        return singleton(f(vNonEmptyList))
    }   // coBind

    override fun <A, B> extend(v: NonEmptyListOf<A>, f: (NonEmptyListOf<A>) -> B): NonEmptyList<B> {
        tailrec
        fun recExtend(fa: List<A>, f: (NonEmptyListOf<A>) -> B, acc: ListBufferIF<B>): List<B> {
            return when (fa) {
                is Nil -> acc.toList()
                is Cons -> {
                    val tl: List<A> = fa.tail()
                    recExtend(tl, f, acc.append(f(nonEmptyList(fa.head(), tl))))
                }
            }
        }   // recExtend

        val vNonEmptyList: NonEmptyList<A> = v.narrow()
        return nonEmptyList(f(vNonEmptyList), recExtend(vNonEmptyList.tail(), f, ListBuffer()))
    }

}   // NonEmptyListComonad
