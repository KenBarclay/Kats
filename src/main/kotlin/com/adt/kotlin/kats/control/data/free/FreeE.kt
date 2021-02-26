package com.adt.kotlin.kats.control.data.free



/**
 * Since class Free<F, A> is the only implementation for Kind1<Kind1<FreeProxy, F>, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, A> FreeOf<F, A>.narrow(): Free<F, A> = this as Free<F, A>
