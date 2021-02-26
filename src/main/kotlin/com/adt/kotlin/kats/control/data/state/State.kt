package com.adt.kotlin.kats.control.data.state

/**
 * StateT is a structure that provides a functional approach to handling application
 *   state. StateT<F, S, A> is basically a function S -> (S, A), where S is the type
 *   that represents the state and A is the result the function produces. In addition
 *   to returning the result of type A, the function returns a new S value, which is
 *   the updated state.
 *
 * @author	                    Ken Barclay
 * @since                       November 2018
 */

/**
 * The Reader monad (also called the Environment monad) represents a computation,
 *   which can read values from a shared environment, pass values from function
 *   to function, and execute sub-computations in a modified environment
 *
 * @author	                    Ken Barclay
 * @since                       November 2018
 */

import com.adt.kotlin.kats.control.data.statet.StateT
import com.adt.kotlin.kats.control.data.statet.StateTF.statet
import com.adt.kotlin.kats.control.data.statet.StateTOf

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityF.identity
import com.adt.kotlin.kats.data.immutable.identity.narrow


/**
 * Alias for StateTOf.
 */
typealias StateOf<S, A> = StateTOf<IdentityProxy, S, A>

/**
 * State<S, A> is basically a function S -> (S, A), where S is the type
 *   that represents the state and A is the result the function produces.
 *
 * @param S                     the type that represents the state
 * @param A                     the result the function produces
 * @see                         StateT
 */
typealias State<S, A> = StateT<IdentityProxy, S, A>

/**
 * Constructor for State.
 *
 * @param run                   the dependency dependent computation
 */
fun <S, A> State(run: (S) -> Identity<Pair<S, A>>): State<S, A> = statet(run)

/**
 * Evaluate a state computation with the given initial state and
 *   return the final value, discarding the final state.
 */
fun <S, A> State<S, A>.evaluate(s: S): S =
        this.evaluate(Identity.monad(), s).narrow().value

/**
 * Evaluate a state computation with the given initial state and
 *   return the final state, discarding the final value.
 */
fun <S, A> State<S, A>.execute(s: S): A =
        this.execute(Identity.monad(), s).narrow().value

/**
 * Map this State with the given function.
 */
fun <S, A, B> State<S, A>.map(f: (A) -> B): State<S, B> =
        this.map(Identity.functor(), f)

fun <S, A, B> State<S, A>.fmap(f: (A) -> B): State<S, B> =
        this.map(Identity.functor(), f)

fun <S, A, B> State<S, A>.ap(f: State<S, (A) -> B>): State<S, B> {
    val self: State<S, A> = this
    return State{s: S ->
        val pair: Pair<S, A> = self(s).narrow().value
        val fpair: Pair<S, (A) -> B> = f(s).narrow().value
        identity(Pair(pair.first, fpair.second(pair.second)))
    }
}   // ap

fun <S, A, B> State<S, A>.bind(f: (A) -> State<S, B>): State<S, B> {
    val self: State<S, A> = this
    return State{s: S ->
        val pair: Pair<S, A> = self(s).narrow().value
        val sB: State<S, B> = f(pair.second)
        identity(Pair(pair.first, sB(s).narrow().value.second))
    }
}   // bind
