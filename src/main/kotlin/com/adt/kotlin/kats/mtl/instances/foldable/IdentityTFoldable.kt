package com.adt.kotlin.kats.mtl.instances.foldable

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.mtl.data.identity.IdentityT
import com.adt.kotlin.kats.mtl.data.identity.IdentityTOf
import com.adt.kotlin.kats.mtl.data.identity.narrow



interface IdentityTFoldable<F> : Foldable<Kind1<IdentityT.IdentityTProxy, F>> {

    fun foldable(): Foldable<F>

    override fun <A, B> foldLeft(v: IdentityTOf<F, A>, e: B, f: (B) -> (A) -> B): B {
        val ff: Foldable<F> = foldable()
        val vIdentityT: IdentityT<F, A> = v.narrow()

        return vIdentityT.foldLeft(ff, e, f)
    }   // foldLeft

    override fun <A, B> foldRight(v: IdentityTOf<F, A>, e: B, f: (A) -> (B) -> B): B {
        val ff: Foldable<F> = foldable()
        val vIdentityT: IdentityT<F, A> = v.narrow()

        return vIdentityT.foldRight(ff, e, f)
    }   // foldRight

}   // IdentityTFoldable
