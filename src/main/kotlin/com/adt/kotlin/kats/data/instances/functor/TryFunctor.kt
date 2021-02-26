package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.tri.Try
import com.adt.kotlin.kats.data.immutable.tri.Try.TryProxy
import com.adt.kotlin.kats.data.immutable.tri.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface TryFunctor : Functor<TryProxy> {

    /**
     * Apply the function to the content(s) of the List context.
     */
    override fun <A, B> fmap(v: Kind1<TryProxy, A>, f: (A) -> B): Try<B> {
        val vTry: Try<A> = v.narrow()
        return vTry.fmap(f)
    }   // fmap

}   // TryFunctor
