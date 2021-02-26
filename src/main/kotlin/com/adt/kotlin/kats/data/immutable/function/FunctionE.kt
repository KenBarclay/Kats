package com.adt.kotlin.kats.data.immutable.function



/**
 * Since class Function<A, B> is the only implementation for Kind1<Kind1<FunctionProxy, A>, B>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A, B> FunctionOf<A, B>.narrow(): Function<A, B> = this as Function<A, B>
