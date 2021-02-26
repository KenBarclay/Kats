package com.adt.kotlin.kats.data.immutable.set

/**
 * Since class Set<A> is the only implementation for Kind1<SetProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A : Comparable<A>> SetOf<A>.narrow(): Set<A> = this as Set<A>
