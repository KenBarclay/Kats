package com.adt.kotlin.kats.mtl.instances.functor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.mtl.data.identity.IdentityT
import com.adt.kotlin.kats.mtl.data.identity.IdentityT.IdentityTProxy
import com.adt.kotlin.kats.mtl.data.identity.IdentityTOf
import com.adt.kotlin.kats.mtl.data.identity.narrow


interface IdentityTFunctor<F> : Functor<Kind1<IdentityTProxy, F>> {

    fun functor(): Functor<F>

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <A, B> fmap(v: IdentityTOf<F, A>, f: (A) -> B): IdentityT<F, B> {
        val ff: Functor<F> = functor()
        val vIdentityT: IdentityT<F, A> = v.narrow()
        return vIdentityT.map(ff, f)
    }   // fmap

}   // IdentityTFunctor
