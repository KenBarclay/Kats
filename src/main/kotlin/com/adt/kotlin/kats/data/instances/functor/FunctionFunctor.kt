package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.function.Function
import com.adt.kotlin.kats.data.immutable.function.Function.FunctionProxy
import com.adt.kotlin.kats.data.immutable.function.FunctionOf
import com.adt.kotlin.kats.data.immutable.function.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface FunctionFunctor<A> : Functor<Kind1<FunctionProxy, A>> {

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <B, C> fmap(v: FunctionOf<A, B>, f: (B) -> C): Function<A, C> {
        val vFunction: Function<A, B> = v.narrow()
        return vFunction.forwardCompose(f)
    }   // fmap

    /**
     * Replace all locations in the input with the given value.
     */
    override fun <B, C> replaceAll(v: FunctionOf<A, B>, b: C): Function<A, C> = this.fmap(v){_ -> b}

    /**
     * Distribute the Function<A, (B, C)> over the pair to get (Function<A, B>, Function<A, C>).
     */
    override fun <B, C> distribute(v: FunctionOf<A, Pair<B, C>>): Pair<Function<A, B>, Function<A, C>> {
        val vFunction: Function<A, Pair<B, C>> = v.narrow()
        return Pair(fmap(vFunction){pr -> pr.first}, fmap(vFunction){pr -> pr.second})
    }   // distribute

}   // Function1Functor
