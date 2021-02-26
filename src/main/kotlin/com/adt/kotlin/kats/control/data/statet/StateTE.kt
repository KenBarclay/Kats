package com.adt.kotlin.kats.control.data.statet



/**
 * Since class StateT<F, A, B> is the only implementation for
 *   Kind1<Kind1<Kind1<ReaderT.ReaderTProxy, F>, S>, A> we define this extension
 *   function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, A, B> StateTOf<F, A, B>.narrow(): StateT<F, A, B> = this as StateT<F, A, B>
