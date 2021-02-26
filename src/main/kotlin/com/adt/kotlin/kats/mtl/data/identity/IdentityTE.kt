package com.adt.kotlin.kats.mtl.data.identity



/**
 * Since class IdentityT<A> is the only implementation for Kind1<IdentityTProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, A> IdentityTOf<F, A>.narrow(): IdentityT<F, A> = this as IdentityT<F, A>
