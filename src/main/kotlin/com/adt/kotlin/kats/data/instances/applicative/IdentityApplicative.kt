package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityOf
import com.adt.kotlin.kats.data.immutable.identity.narrow
import com.adt.kotlin.kats.data.instances.functor.IdentityFunctor

import com.adt.kotlin.kats.hkfp.typeclass.Applicative



interface IdentityApplicative : Applicative<IdentityProxy>, IdentityFunctor {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): Identity<A> = Identity(a)

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <A, B> ap(v: IdentityOf<A>, f: IdentityOf<(A) -> B>): Identity<B> {
        val vIdentity: Identity<A> = v.narrow()
        val fIdentity: Identity<(A) -> B> = f.narrow()
        return Identity(fIdentity.value(vIdentity.value))
    }   // ap

}   // IdentityApplicative
