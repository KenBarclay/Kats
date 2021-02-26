package com.adt.kotlin.kats.control.instances.functor

import com.adt.kotlin.kats.control.data.statet.StateT
import com.adt.kotlin.kats.control.data.statet.StateT.StateTProxy
import com.adt.kotlin.kats.control.data.statet.StateTF.statet
import com.adt.kotlin.kats.control.data.statet.StateTOf
import com.adt.kotlin.kats.control.data.statet.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor


interface StateTFunctor<F, S> : Functor<Kind1<Kind1<StateTProxy, F>, S>> {

    fun functor(): Functor<F>

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <A, B> fmap(v: StateTOf<F, S, A>, f: (A) -> B): StateT<F, S, B> {
        val vStateT: StateT<F, S, A> = v.narrow()
        return statet{s: S -> functor().fmap(vStateT.run(s)){pair: Pair<S, A> -> Pair(pair.first, f(pair.second))} }
    }   // fmap

}   // StateTFunctor
