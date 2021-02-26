package com.adt.kotlin.kats.control.data.state

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.IdentityF.identity


object StateF {

    /**
     * Factory functions to create the base instance.
     */
    fun <S, A> state(f: (S) -> Identity<Pair<S, A>>): State<S, A> = State(f)

    fun <S, A> state(pair: Pair<S, A>): State<S, A> = State{_: S -> identity(pair)}

    fun <S, A> state(s: S, a: A): State<S, A> = state(Pair(s, a))

}   // StateF
