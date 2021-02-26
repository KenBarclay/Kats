package com.adt.kotlin.kats.data.immutable.identity



/**
 * Since class Identity<A> is the only implementation for Kind1<IdentityProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A> IdentityOf<A>.narrow(): Identity<A> = this as Identity<A>
