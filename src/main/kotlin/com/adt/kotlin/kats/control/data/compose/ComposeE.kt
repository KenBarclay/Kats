package com.adt.kotlin.kats.control.data.compose



/**
 * Since class Compose<F, G, A> is the only implementation for
 *   Kind1<Kind1<Kind1<ComposeProxy, F>, G>, A> we define this extension
 *   function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, G, A> ComposeOf<F, G, A>.narrow(): Compose<F, G, A> = this as Compose<F, G, A>
