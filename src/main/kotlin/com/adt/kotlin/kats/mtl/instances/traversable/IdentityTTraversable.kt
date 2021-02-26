package com.adt.kotlin.kats.mtl.instances.traversable

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Traversable
import com.adt.kotlin.kats.mtl.data.identity.IdentityT
import com.adt.kotlin.kats.mtl.data.identity.IdentityTOf
import com.adt.kotlin.kats.mtl.data.identity.narrow
import com.adt.kotlin.kats.mtl.instances.foldable.IdentityTFoldable
import com.adt.kotlin.kats.mtl.instances.functor.IdentityTFunctor



interface IdentityTTraversable<F> : Traversable<Kind1<IdentityT.IdentityTProxy, F>>, IdentityTFoldable<F>, IdentityTFunctor<F> {

    fun traversable(): Traversable<F>
    override fun foldable(): Foldable<F> = traversable()
    override fun functor(): Functor<F> = traversable()

    override fun <G, A, B> traverse(v: IdentityTOf<F, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, IdentityT<F, B>> {
        val tf: Traversable<F> = traversable()
        val vIdentityT: IdentityT<F, A> = v.narrow()

        return vIdentityT.traverse(tf, ag, f)
    }   // traverse

}   // IdentityTTraversable
