package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.immutable.function.Function
import com.adt.kotlin.kats.data.immutable.function.Function.FunctionProxy
import com.adt.kotlin.kats.data.immutable.function.FunctionOf
import com.adt.kotlin.kats.data.immutable.function.narrow
import com.adt.kotlin.kats.data.instances.functor.FunctionFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative



interface FunctionApplicative<A> : Applicative<Kind1<FunctionProxy, A>>, FunctionFunctor<A> {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <B> pure(a: B): Function<A, B> = Function<A, B>{_: A -> a}

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <B, C> ap(v: FunctionOf<A, B>, f: FunctionOf<A, (B) -> C>): Function<A, C> {
        val vFunction: Function<A, B> = v.narrow()
        val fFunction: Function<A, (B) -> C> = f.narrow()
        val g: (A) -> C = {a: A ->
            fFunction(a)(vFunction(a))
        }
        return Function(g)
    }   // ap

}   // FunctionApplicative
