package com.adt.kotlin.kats.data.immutable.stream

import com.adt.kotlin.kats.hkfp.typeclass.Applicative


/**
 * Since class Stream<A> is the only implementation for Kind1<StreamProxy, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A> StreamOf<A>.narrow(): Stream<A> = this as Stream<A>



// Functor extension functions:

/**
 * An infix symbol for fmap.
 */
infix fun <A, B> ((A) -> B).dollar(v: Stream<A>): Stream<B> = v.map(this)



// Applicative extension functions:

/**
 * An infix symbol for ap.
 */
infix fun <A, B> Stream<(A) -> B>.apply(v: Stream<A>): Stream<B> = this.appliedOver(v)

/**
 * An infix symbol for ap.
 */
infix fun <A, B> Stream<(A) -> B>.appliedOver(v: Stream<A>): Stream<B> {
    val applicative: Applicative<Stream.StreamProxy> = Stream.applicative()
    return applicative.ap(v, this).narrow()
}   // appliedOver
