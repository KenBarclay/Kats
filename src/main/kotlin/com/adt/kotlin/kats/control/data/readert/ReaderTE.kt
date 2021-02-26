package com.adt.kotlin.kats.control.data.readert



/**
 * Since class ReaderT<F, A, B> is the only implementation for
 *   Kind1<Kind1<Kind1<ReaderTProxy, F>, A>, B> we define this extension
 *   function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, A, B> ReaderTOf<F, A, B>.narrow(): ReaderT<F, A, B> = this as ReaderT<F, A, B>
