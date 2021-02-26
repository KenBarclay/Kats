package com.adt.kotlin.kats.control.instances.applicative

import com.adt.kotlin.kats.control.data.statet.StateT
import com.adt.kotlin.kats.control.data.statet.StateT.StateTProxy
import com.adt.kotlin.kats.control.data.statet.StateTOf
import com.adt.kotlin.kats.control.data.statet.narrow

import com.adt.kotlin.kats.control.instances.functor.StateTFunctor
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface StateTApplicative<F, S> : Applicative<Kind1<Kind1<StateTProxy, F>, S>>, StateTFunctor<F, S> {

    fun monad(): Monad<F>
    override fun functor(): Functor<F> = monad()

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): StateT<F, S, A> = StateT{s -> monad().inject(Pair(s, a))}

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <A, B> ap(v: StateTOf<F, S, A>, f: StateTOf<F, S, (A) -> B>): StateT<F, S, B> {
        val vState: StateT<F, S, A> = v.narrow()
        val fState: StateT<F, S, (A) -> B> = f.narrow()

        return StateT{s: S ->
            monad().run{
                bind(fState(s)){fpair: Pair<S, (A) -> B> ->
                    bind(vState(fpair.first)){pair: Pair<S, A> ->
                        inject(Pair(pair.first, fpair.second(pair.second)))
                    }
                }
            }
        }
    }   // ap

}   // StateTApplicative
