package com.adt.kotlin.kats.mtl.data.option



/**
 * Since class OptionT<A> is the only implementation for Kind1<OptionProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, A> OptionTOf<F, A>.narrow(): OptionT<F, A> = this as OptionT<F, A>
