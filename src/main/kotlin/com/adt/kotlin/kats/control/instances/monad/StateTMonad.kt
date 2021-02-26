package com.adt.kotlin.kats.control.instances.monad

import com.adt.kotlin.kats.control.data.statet.StateT
import com.adt.kotlin.kats.control.data.statet.StateT.StateTProxy
import com.adt.kotlin.kats.control.data.statet.StateTF.statet
import com.adt.kotlin.kats.control.data.statet.StateTOf
import com.adt.kotlin.kats.control.data.statet.narrow

import com.adt.kotlin.kats.control.instances.applicative.StateTApplicative
import com.adt.kotlin.kats.data.immutable.either.Either

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad


interface StateTMonad<F, S> : Monad<Kind1<Kind1<StateTProxy, F>, S>>, StateTApplicative<F, S> {

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): StateT<F, S, A> = StateT{s -> monad().pure(Pair(s, a))}

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <A, B> bind(v: StateTOf<F, S, A>, f: (A) -> StateTOf<F, S, B>): StateT<F, S, B> {
        val vState: StateT<F, S, A> = v.narrow()

        return StateT{s: S ->
            monad().run{
                bind(vState(s)){pair: Pair<S, A> ->
                    val state: StateT<F, S, B> = f(pair.second).narrow()
                    state(pair.first)
                }
            }
        }
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    ////@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <A, B> tailRecM(a: A, f: (A) -> StateTOf<F, S, Either<A, B>>): StateT<F, S, B> {
        fun recTailRecM(mf: Monad<F>, a: A, f: (A) -> StateT<F, S, Either<A, B>>): StateT<F, S, B> {
            return statet{s: S ->
                mf.tailRecM(Pair(s, a)){pair: Pair<S, A> ->
                    mf.fmap(f(pair.second)(s)){pr: Pair<S, Either<A, B>> ->
                        pr.second.bimap({a: A -> Pair(s, a)}, {b: B -> Pair(s, b)})
                    }
                }
            }
        }   // recTailRecM

        val g: (A) -> StateT<F, S, Either<A, B>> = {aa: A -> f(aa).narrow()}
        return recTailRecM(monad(), a, g)
    }   // tailRecM

}   // StateTMonad
