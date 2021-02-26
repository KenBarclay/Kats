package com.adt.kotlin.kats.data.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.immutable.identity.IdentityF.identity
import com.adt.kotlin.kats.data.immutable.identity.IdentityOf
import com.adt.kotlin.kats.data.immutable.identity.narrow
import com.adt.kotlin.kats.data.instances.applicative.IdentityApplicative

import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadForC
import com.adt.kotlin.kats.hkfp.typeclass.MonadSyntax


interface IdentityMonad : Monad<IdentityProxy>, IdentityApplicative {

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): Identity<A> = Identity(a)

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <A, B> bind(v: IdentityOf<A>, f: (A) -> IdentityOf<B>): Identity<B> {
        val vIdentity: Identity<A> = v.narrow()
        val g: (A) -> Identity<B> = {a: A -> f(a).narrow()}
        return vIdentity.bind(g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> IdentityOf<Either<B, C>>): Identity<C> {
        tailrec
        fun recTailRecM(b: B, f: (B) -> Identity<Either<B, C>>): Identity<C> {
            val fb: Either<B, C> = f(b).value
            return when (fb) {
                is Left -> recTailRecM(fb.value, f)
                is Right -> identity(fb.value)
            }
        }   // recTailRecM

        val g: (B) -> Identity<Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(b, g)
    }   // tailRecM



    /**
     * Entry point for monad bindings which enables for comprehension.
     */
    override val forC: MonadForC<IdentityProxy>
        get() = IdentityForCMonad

}   // IdentityMonad



internal object IdentityForCMonad : MonadForC<IdentityProxy> {

    override val mf: Monad<IdentityProxy> = Identity.monad()
    override fun <A> monad(block: suspend MonadSyntax<IdentityProxy>.() -> A): Identity<A> =
            super.monad(block).narrow()

}   // IdentityForCMonad
