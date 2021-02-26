package com.adt.kotlin.kats.control.data.writert



/**
 * Since class WriterT<F, W, A> is the only implementation for
 *   Kind1<Kind1<Kind1<WriterTProxy, F>, W>, A> we define this extension
 *   function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, W, A> WriterTOf<F, W, A>.narrow(): WriterT<F, W, A> = this as WriterT<F, W, A>
