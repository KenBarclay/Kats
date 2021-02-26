package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList.NonEmptyListProxy
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyListF.singleton
import com.adt.kotlin.kats.data.immutable.nel.narrow
import com.adt.kotlin.kats.data.instances.functor.NonEmptyListFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative



interface NonEmptyListApplicative : Applicative<NonEmptyListProxy>, NonEmptyListFunctor {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): NonEmptyList<A> = singleton(a)

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     *
     * Examples:
     *   [1 :| 2, 3, 4].ap([{n -> (n % 2 == 0)}]) = [false :| true, false, true]
     */
    override fun <A, B> ap(v: Kind1<NonEmptyListProxy, A>, f: Kind1<NonEmptyListProxy, (A) -> B>): NonEmptyList<B> {
        val vNonEmptyList: NonEmptyList<A> = v.narrow()
        val fNonEmptyList: NonEmptyList<(A) -> B> = f.narrow()
        return vNonEmptyList.ap(fNonEmptyList)
    }   // ap

}   // NonEmptyListApplicative
