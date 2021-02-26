package com.adt.kotlin.kats.data.immutable.lens

/**
 * A PLens is a pair of functions:
 *   get: (S) -> A          from an S extract an A
 *   set; (B, S) -> T       replace an A by a B in an S obtaining a T
 *
 * A PLens is a polymorphic lens as its set and modify functions change
 *   the type A to B and the type S to T.
 *
 * Lens is a type alias for PLens restricted to monomorphic updates.
 *
 * @param S                     the source of a PLens
 * @param T                     the modified source of a PLens
 * @param A                     the target of a PLens
 * @param B                     the modified target of a PLens
 */



typealias Lens<S, A> = PLens<S, S, A, A>

interface PLens<S, T, A, B> {

    /**
     * Get the target of a PLens.
     */
    fun get(s: S): A

    /**
     * Set the target of a PLens.
     */
    fun set(s: S, b: B): T

    /**
     * Modify the target of the PLens using the given function.
     */
    fun modify(s: S, f: (A) -> B): T

    /**
     * Compose this PLens with the given PLens.
     */
    fun <C, D> compose(plens: PLens<A, B, C, D>): PLens<S, T, C, D> {
        val self: PLens<S, T, A, B> = this
        return object: PLens<S, T, C, D> {

            override fun get(s: S): C = plens.get(self.get(s))
            override fun set(s: S, b: D): T = self.modify(s){a: A -> plens.set(a, b)}
            override fun modify(s: S, f: (C) -> D): T = self.modify(s){a: A -> plens.modify(a, f)}
        }
    }   // compose

}   // PLens
