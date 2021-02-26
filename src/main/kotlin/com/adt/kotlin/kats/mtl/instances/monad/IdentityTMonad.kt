package com.adt.kotlin.kats.mtl.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.mtl.data.identity.IdentityT
import com.adt.kotlin.kats.mtl.data.identity.IdentityT.IdentityTProxy
import com.adt.kotlin.kats.mtl.data.identity.IdentityTOf
import com.adt.kotlin.kats.mtl.data.identity.narrow
import com.adt.kotlin.kats.mtl.instances.applicative.IdentityTApplicative



interface IdentityTMonad<F> : Monad<Kind1<IdentityTProxy, F>>, IdentityTApplicative<F> {

    fun monad(): Monad<F>
    override fun applicative(): Applicative<F> = monad()

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): IdentityT<F, A> {
        val mf: Monad<F> = monad()
        return IdentityT(mf.inject(a))
    }   // inject

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <A, B> bind(v: IdentityTOf<F, A>, f: (A) -> IdentityTOf<F, B>): IdentityT<F, B> {
        val mf: Monad<F> = monad()
        val vIdentityT: IdentityT<F, A> = v.narrow()
        val g: (A) -> IdentityT<F, B> = {a: A -> f(a).narrow()}

        return vIdentityT.bind(mf, g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    override fun <A, B> tailRecM(a: A, f: (A) -> IdentityTOf<F, Either<A, B>>): IdentityT<F, B> {
        val mf: Monad<F> = monad()

        return IdentityT(mf.tailRecM(a){aa: A -> f(aa).narrow().runIdentity})
    }   // tailRecM

}   // IdentityTMonad
