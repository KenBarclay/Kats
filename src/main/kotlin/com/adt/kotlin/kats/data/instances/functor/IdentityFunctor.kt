package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityOf
import com.adt.kotlin.kats.data.immutable.identity.narrow

import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface IdentityFunctor : Functor<IdentityProxy> {

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <A, B> fmap(v: IdentityOf<A>, f: (A) -> B): Identity<B> =
            v.narrow().map(f)



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Option context.
     */
    override fun <A, B> lift(f: (A) -> B): (IdentityOf<A>) -> Identity<B> =
            {ia: IdentityOf<A> ->
                fmap(ia, f)
            }

}   // IdentityFunctor
