package com.adt.kotlin.kats.mtl.instances.applicative

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.mtl.data.identity.IdentityT
import com.adt.kotlin.kats.mtl.data.identity.IdentityT.IdentityTProxy
import com.adt.kotlin.kats.mtl.data.identity.IdentityTOf
import com.adt.kotlin.kats.mtl.data.identity.narrow
import com.adt.kotlin.kats.mtl.instances.functor.IdentityTFunctor



interface IdentityTApplicative<F> : Applicative<Kind1<IdentityTProxy, F>>, IdentityTFunctor<F> {

    fun applicative(): Applicative<F>
    override fun functor(): Functor<F> = applicative()

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): IdentityT<F, A> {
        val af: Applicative<F> = applicative()
        return IdentityT(af.pure(a))
    }   // pure

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <A, B> ap(v: IdentityTOf<F, A>, f: IdentityTOf<F, (A) -> B>): IdentityT<F, B> {
        val af: Applicative<F> = applicative()
        val vIdentityT: IdentityT<F, A> = v.narrow()
        val fIdentityT: IdentityT<F, (A) -> B> = f.narrow()
        return vIdentityT.ap(af, fIdentityT)
    }   // ap

}   // IdentityTApplicative
