package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.hkfp.typeclass.Monoid



class IdentityMonoid<A>(val ma: Monoid<A>) : Monoid<Identity<A>> {

    override val empty: Identity<A> = Identity(ma.empty)

    override fun combine(a: Identity<A>, b: Identity<A>): Identity<A> =
            Identity(ma.combine(a.value, b.value))

}   // IdentityMonoid
