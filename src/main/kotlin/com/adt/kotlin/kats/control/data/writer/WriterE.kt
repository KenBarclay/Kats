package com.adt.kotlin.kats.control.data.writer



/**
 * Since class Writer<W, A> is the only implementation for Kind1<Kind1<WriterProxy, W>, A>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <W, A> WriterOf<W, A>.narrow(): Writer<W, A> = this as Writer<W, A>




/**
 * An infix symbol for fmap.
 */
infix fun <W, A, B> ((A) -> B).dollar(v: Writer<W, A>): Writer<W, B> = v.map(this)
