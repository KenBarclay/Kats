package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface IdentitySemigroup<A> : Semigroup<Identity<A>> {

    val sga: Semigroup<A>

    override fun combine(a: Identity<A>, b: Identity<A>): Identity<A> =
            Identity(sga.combine(a.value, b.value))

}   // IdentitySemigroup
