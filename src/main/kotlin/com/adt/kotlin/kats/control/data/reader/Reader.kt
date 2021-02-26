package com.adt.kotlin.kats.control.data.reader

/**
 * The Reader monad (also called the Environment monad) represents a computation,
 *   which can read values from a shared environment, pass values from function
 *   to function, and execute sub-computations in a modified environment
 *
 * @author	                    Ken Barclay
 * @since                       November 2018
 */

import com.adt.kotlin.kats.control.data.readert.ReaderT
import com.adt.kotlin.kats.control.data.readert.ReaderTF.readert
import com.adt.kotlin.kats.control.data.readert.ReaderTOf

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityF.identity
import com.adt.kotlin.kats.data.immutable.identity.narrow


/**
 * Alias for ReaderTOf.
 */
typealias ReaderOf<A, B> = ReaderTOf<IdentityProxy, A, B>

/**
 * Reader represents a computation that has a dependency on A.
 *   Reader<A, B> is an alias for ReaderT<IdentityProxy, A, B>.
 *
 * @param A                     the dependency or environment we depend on
 * @param B                     resulting type of the computation
 * @see                         ReaderT
 */
typealias Reader<A, B> = ReaderT<IdentityProxy, A, B>

/**
 * Constructor for Reader.
 *
 * @param run                   the dependency dependent computation
 */
fun <A, B> Reader(run: (A) -> B): Reader<A, B> = readert{a: A -> identity(run(a))}

/**
 * Execute the Reader arrow against the given value.
 *
 * @param a                 parameter value for the arrow
 * @return                  computed value
 */
fun <A, B> Reader<A, B>.execute(a: A): B = this(a).narrow().value

/**
 * Map this Reader with the given function.
 */
fun <A, B, C> Reader<A, B>.map(f: (B) -> C): Reader<A, C> =
        Reader{a: A ->
            val idB: Identity<B> = this(a).narrow()
            identity(f(idB.value))
        }

fun <A, B, C> Reader<A, B>.ap(f: Reader<A, (B) -> C>): Reader<A, C> {
    val self: Reader<A, B> = this
    return Reader{a: A ->
        val idB: Identity<B> = self(a).narrow()
        val idF: Identity<(B) -> C> = f(a).narrow()
        identity(idF.value(idB.value))
    }
}

/**
 * Sequentially compose two actions, passing any value produced by the first
 *   as an argument to the second.
 */
fun <A, B, C> Reader<A, B>.bind(f: (B) -> Reader<A, C>): Reader<A, C> {
    val self: Reader<A, B> = this
    return Reader{a: A ->
        val idB: Identity<B> = self(a).narrow()
        val rac: Reader<A, C> = f(idB.value)
        rac(a)
    }
}   // bind

fun <A, B, C> Reader<A, B>.flatMap(f: (B) -> Reader<A, C>): Reader<A, C> =
        bind(f)
