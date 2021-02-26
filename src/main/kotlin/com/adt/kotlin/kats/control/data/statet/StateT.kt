package com.adt.kotlin.kats.control.data.statet

import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.statet.StateT.StateTProxy
import com.adt.kotlin.kats.control.instances.applicative.StateTApplicative
import com.adt.kotlin.kats.control.instances.functor.StateTFunctor
import com.adt.kotlin.kats.control.instances.monad.StateTMonad

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadSyntax


/**
 * StateT<F, S, A> is basically a function S -> (S, A), where S is the type
 *   that represents your state and A is the result the function produces.
 *
 * @param F                     the context
 * @param S                     the state type
 * @param A                     the result type
 * @param run                   the state function
 */
typealias StateTOf<F, S, A> = Kind1<Kind1<Kind1<StateTProxy, F>, S>, A>

class StateT<F, S, out A> internal constructor(val run: (S) -> Kind1<F, Pair<S, A>>) : Kind1<Kind1<Kind1<StateTProxy, F>, S>, A> {

    class StateTProxy                     // proxy for the State context



    /**
     * Execute the State against the given value.
     *
     * @param s                 parameter value for the state
     * @return                  computed context
     */
    operator fun invoke(s: S): Kind1<F, Pair<S, A>> = run(s)

    /**
     * Evaluate a state computation with the given initial state and
     *   return the final value, discarding the final state.
     */
    fun evaluate(mf: Monad<F>, s: S): Kind1<F, S> =
            mf.bind(run(s)){pair: Pair<S, A> -> mf.inject(pair.first)}

    /**
     * Evaluate a state computation with the given initial state and
     *   return the final state, discarding the final value.
     */
    fun execute(mf: Monad<F>, s: S): Kind1<F, A> =
            mf.bind(run(s)){pair: Pair<S, A> -> mf.inject(pair.second)}

    /**
     * Apply the function to the content(s) of this context.
     */
    fun <B> map(ff: Functor<F>, f: (A) -> B): StateT<F, S, B> =
            StateTF.statet { s: S -> ff.fmap(this.run(s)) { pair: Pair<S, A> -> Pair(pair.first, f(pair.second)) } }

    /**
     * Apply the function to the content(s) of the context.
     *
    fun <B> map(f: (A) -> B): State<S, B> {
        val self: State<S, A> = this
        return State{s: S ->
            val pair: Pair<S, A> = self(s)
            Pair(pair.first, f(pair.second))
        }
    }
    ***/

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     *
    fun <B> ap(f: State<S, (A) -> B>): State<S, B> {
        val self: State<S, A> = this
        return State{s: S ->
            val pair: Pair<S, A> = self(s)
            val fpair: Pair<S, (A) -> B> = f(s)
            Pair(pair.first, fpair.second(pair.second))
        }
    }
    ***/

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     *
    fun <B> bind(f: (A) -> State<S, B>): State<S, B> {
        val self: State<S, A> = this
        return State{s: S ->
            val pair: Pair<S, A> = self(s)
            val sB: State<S, B> = f(pair.second)
            Pair(pair.first, sB(s).second)
        }
    }
    ***/



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <F, S> functor(ff: Functor<F>): Functor<Kind1<Kind1<StateTProxy, F>, S>> = object: StateTFunctor<F, S> {
            override fun functor(): Functor<F> = ff
        }

        /**
         * Create an instance of this applicative.
         */
        fun <F, S> applicative(mf: Monad<F>): Applicative<Kind1<Kind1<StateTProxy, F>, S>> = object: StateTApplicative<F, S> {
            override fun monad(): Monad<F> = mf
        }

        /**
         * Create an instance of this monad.
         */
        fun <F, S> monad(mf: Monad<F>): Monad<Kind1<Kind1<StateTProxy, F>, S>> = object: StateTMonad<F, S> {
            override fun monad(): Monad<F> = mf
        }

        /**
         * Entry point for monad bindings which enables for comprehension.
         */
        fun <F, S, A> forC(mf: Monad<F>, block: suspend MonadSyntax<Kind1<Kind1<StateTProxy, F>, S>>.() -> A): StateT<F, S, A> =
                monad<F, S>(mf).forC.monad(block).narrow()

    }

}   // StateT
