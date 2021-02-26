package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.immutable.tri.Try
import com.adt.kotlin.kats.data.immutable.tri.Try.TryProxy
import com.adt.kotlin.kats.data.immutable.tri.TryF.success
import com.adt.kotlin.kats.data.immutable.tri.narrow

import com.adt.kotlin.kats.data.instances.functor.TryFunctor
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative



interface TryApplicative : Applicative<TryProxy>, TryFunctor {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): Try<A> = success(a)

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <A, B> ap(v: Kind1<TryProxy, A>, f: Kind1<TryProxy, (A) -> B>): Try<B> {
        val vTry: Try<A> = v.narrow()
        val fTry: Try<(A) -> B> = f.narrow()
        return vTry.ap(fTry)
    }   // ap

}   // TryApplicative
