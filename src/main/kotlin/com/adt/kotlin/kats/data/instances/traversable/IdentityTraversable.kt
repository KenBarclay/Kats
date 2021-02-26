package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityOf
import com.adt.kotlin.kats.data.immutable.identity.narrow
import com.adt.kotlin.kats.data.instances.foldable.IdentityFoldable
import com.adt.kotlin.kats.data.instances.functor.IdentityFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface IdentityTraversable : Traversable<IdentityProxy>, IdentityFoldable, IdentityFunctor {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    override fun <G, A, B> traverse(v: IdentityOf<A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Identity<B>> =
            ag.run{
                val vIdentity: Identity<A> = v.narrow()
                ap(f(vIdentity.value), pure{b: B -> Identity(b)})
            }   // traverse

}   // IdentityTraversable
