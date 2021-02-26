package com.adt.kotlin.kats.control.data.writer

/**
 * Writer is a writer monad parameterized by the type W of output to accumulate,
 *   and the result type A.
 *
 * @author	                    Ken Barclay
 * @since                       November 2018
 *
 * @param W                     the output type
 * @param A                     the result type
 * @param run                   the output/result pair
 */

import com.adt.kotlin.kats.control.data.writert.WriterT
import com.adt.kotlin.kats.control.data.writert.WriterTF
import com.adt.kotlin.kats.control.data.writert.WriterTOf
import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityF.identity
import com.adt.kotlin.kats.data.immutable.identity.narrow


/**
 * Alias for WriterTOf.
 */
typealias WriterOf<W, A> = WriterTOf<IdentityProxy, W, A>

/**
 * Writer represents a computation that has ...
 */
typealias Writer<W, A> = WriterT<IdentityProxy, W, A>

/**
 * Constructor for Writer.
 *
 * @param run                   the dependency dependent computation
 */
fun <W, A> Writer(run: Pair<W, A>): Writer<W, A> = WriterTF.writert(identity(run))

fun <W, A> Writer(w: W, a: A): Writer<W, A> = WriterTF.writert(identity(Pair(w, a)))

fun <W, A> Writer<W, A>.show(): String = "Writer(${run})"

/**
 * Map this Writer with the given function.
 */
fun <W, A, B> Writer<W, A>.map(f: (A) -> B): Writer<W, B> = this.map(Identity.functor(), f)

/**
 * Apply the function f to the output part of this Writer delivering a new
 *   Writer.
 */
fun <W, A, U> Writer<W, A>.mapWritten(f: (W) -> U): Writer<U, A> =
        this.mapWritten(Identity.monad(), f)

/**
 * Apply the function f to the tuple enclosed by the Writer delivering a new
 *   Writer.
 */
fun <W, A, U, B> Writer<W, A>.mapBoth(f: (Pair<W, A>) -> Pair<U, B>): Writer<U, B> =
        this.mapBoth(Identity.monad(), f)

/**
 * Obtain the output of the Writer.
 */
fun <W, A> Writer<W, A>.written(): W {
    val idW: Identity<W> = this.written(Identity.functor()).narrow()
    return idW.value
}   // written

/**
 * Obtain the result of the Writer.
 */
fun <W, A> Writer<W, A>.result(): A {
    val idA: Identity<A> = this.result(Identity.functor()).narrow()
    return idA.value
}   // result

/**
 * Apply the two functions f and g to respectively the output part and the result part
 *   of this Writer producing a new Writer.
 */
fun <W, A, U, B> Writer<W, A>.bimap(f: (W) -> U, g: (A) -> B): Writer<U, B> =
        this.bimap(Identity.monad(), f, g)

/**
 * Deliver a new Writer with the output and result pair interchanged.
 */
fun <W, A> Writer<W, A>.swap(): Writer<A, W> = this.swap(Identity.monad())