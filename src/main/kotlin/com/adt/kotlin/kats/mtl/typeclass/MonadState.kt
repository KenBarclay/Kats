package com.adt.kotlin.kats.mtl.typeclass

/**
 * XXX
 *
 * The minimal complete definition is state or get and set.
 *
 * @author	                    Ken Barclay
 * @since                       Feburary 2021
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface MonadState<F, S> : Monad<F> {

    /**
     * Return the state from the internals of the monad.
     */
    fun get(): Kind1<F, S> =
            state{s: S -> Pair(s, s)}

    /**
     *
     */
    fun <A> inspect(f: (S) -> A): Kind1<F, A> =
            fmap(get(), f)

    /**
     * Maps an old state to a new state inside a state monad. The old state
     *   is thrown away.
     */
    fun modify(f: (S) -> S): Kind1<F, Unit> =
            bind(get()){s: S -> set(f(s))}

    /**
     * Replace the state inside the monad.
     */
    fun set(s: S): Kind1<F, Unit> =
            state{_: S -> Pair(s, Unit)}

    /**
     * Embed a simple state action into the monad.
     */
    fun <A> state(f: (S) -> Pair<S, A>): Kind1<F, A> =
            bind(get()){s -> f(s).let{p: Pair<S, A> -> fmap(set(p.first)){ p.second } }}

}   // MonadState
