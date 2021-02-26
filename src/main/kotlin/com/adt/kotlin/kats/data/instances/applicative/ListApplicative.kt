package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.immutable.list.*
import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.ListProxy
import com.adt.kotlin.kats.data.immutable.list.ListF.singleton

import com.adt.kotlin.kats.data.instances.functor.ListFunctor
import com.adt.kotlin.kats.hkfp.typeclass.Applicative



/**
 * Applicative over a List.
 */
interface ListApplicative : Applicative<ListProxy>, ListFunctor {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): List<A> = singleton(a)

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     *
     * Examples:
     *   let applicative = List.applicative()
     *   let functions = [{ _ -> 0 }, { n -> 100 + n }, { n -> n * n }]
     *
     *   applicative.run{ ap([], functions) } == []
     *   applicative.run{ ap([1, 2, 3, 4], functions) } == [0, 0, 0, 0, 101, 102, 103, 104, 1, 4, 9, 16]
     *   applicative.run{ ap([1, 2, 3, 4], [] } == []
     */
    override fun <A, B> ap(v: ListOf<A>, f: ListOf<(A) -> B>): List<B> {
        val vList: List<A> = v.narrow()
        val fList: List<(A) -> B> = f.narrow()
        return vList.ap(fList)
    }   // ap

}   // ListApplicative
