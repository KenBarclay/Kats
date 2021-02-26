package com.adt.kotlin.kats.data.instances.comonad

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityF.identity
import com.adt.kotlin.kats.data.immutable.identity.IdentityOf
import com.adt.kotlin.kats.data.immutable.identity.narrow
import com.adt.kotlin.kats.data.instances.functor.IdentityFunctor
import com.adt.kotlin.kats.hkfp.typeclass.Comonad



interface IdentityComonad : Comonad<IdentityProxy>, IdentityFunctor {

    override fun <A> extract(v: IdentityOf<A>): A {
        val vIdentity: Identity<A> = v.narrow()
        return vIdentity.value
    }   // extract

    override fun <A, B> coBind(v: IdentityOf<A>, f: (IdentityOf<A>) -> B): Identity<B> {
        val vIdentity: Identity<A> = v.narrow()
        return identity(f(vIdentity))
    }   // coBind

}   // IdentityComonad
