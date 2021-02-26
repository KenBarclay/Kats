package com.adt.kotlin.kats.data.instances.foldable

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityOf
import com.adt.kotlin.kats.data.immutable.identity.narrow

import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface IdentityFoldable : Foldable<IdentityProxy> {

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <A, B> foldLeft(v: IdentityOf<A>, e: B, f: (B) -> (A) -> B): B {
        val vIdentity: Identity<A> = v.narrow()
        return f(e)(vIdentity.value)
    }   // foldLeft

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <A, B> foldRight(v: IdentityOf<A>, e: B, f: (A) -> (B) -> B): B {
        val vIdentity: Identity<A> = v.narrow()
        return f(vIdentity.value)(e)
    }

}   // IdentityFoldable
