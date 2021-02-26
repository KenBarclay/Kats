package com.adt.kotlin.kats.control.data.state



/**
 * Since class State<S, A> is the only implementation for Kind1<Kind1<StateProxy, S>, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <S, A> StateOf<S, A>.narrow(): State<S, A> = this as State<S, A>
