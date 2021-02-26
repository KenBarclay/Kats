package com.adt.kotlin.kats.data.immutable.identity

/**
 * The identity functor and monad.
 */

import com.adt.kotlin.kats.data.immutable.identity.Identity.IdentityProxy
import com.adt.kotlin.kats.data.instances.applicative.IdentityApplicative
import com.adt.kotlin.kats.data.instances.comonad.IdentityComonad
import com.adt.kotlin.kats.data.instances.foldable.IdentityFoldable
import com.adt.kotlin.kats.data.instances.functor.IdentityFunctor
import com.adt.kotlin.kats.data.instances.monad.IdentityMonad
import com.adt.kotlin.kats.data.instances.monoid.IdentityMonoid
import com.adt.kotlin.kats.data.instances.semigroup.IdentitySemigroup
import com.adt.kotlin.kats.data.instances.traversable.IdentityTraversable

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*


typealias IdentityOf<A> = Kind1<IdentityProxy, A>

class Identity<A> internal constructor(val value: A) : Kind1<IdentityProxy, A> {

    class IdentityProxy private constructor()         // representation for the Identity context



    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param other             the other object
     * @return                  true if "equal", false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val otherIdentity: Identity<A> = other as Identity<A>
            (this.value == otherIdentity.value)
        }
    }   // equals

    /**
     * Transform this Identity with the tgransformer function.
     *
     */
    fun <B> map(f: (A) -> B): Identity<B> = Identity(f(value))

    /**
     * Apply the function wrapped in this context to the content of this
     *   value.
     */
    fun <B> ap(f: Identity<(A) -> B>): Identity<B> {
        val g: (A) -> B = f.value
        return Identity(g(this.value))
    }   // ap

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    fun <B> bind(f: (A) -> Identity<B>): Identity<B> = f(value)

    fun <B> flatMap(f: (A) -> Identity<B>): Identity<B> = this.bind(f)

    /**
     * Produce a string representation.
     */
    override fun toString(): String = "Identity($value)"



    companion object {

        /**
         * Create an instance of this semigroup.
         */
        fun <A> semigroup(sg: Semigroup<A>): Semigroup<Identity<A>> = object: IdentitySemigroup<A> {
            override val sga: Semigroup<A> = sg
        }

        /**
         * Create an instance of this monoid.
         */
        fun <A> monoid(ma: Monoid<A>): Monoid<Identity<A>> = IdentityMonoid(ma)

        /**
         * Create an instance of this functor.
         */
        fun functor(): Functor<IdentityProxy> = object: IdentityFunctor {}

        /**
         * Create an instance of this applicative.
         */
        fun applicative(): Applicative<IdentityProxy> = object: IdentityApplicative {}

        /**
         * Create an instance of this monad.
         */
        fun monad(): Monad<IdentityProxy> = object: IdentityMonad {}

        /**
         * Create an instance of this foldable.
         */
        fun foldable(): Foldable<IdentityProxy> = object: IdentityFoldable {}

        /**
         * Create an instance of this traversable.
         */
        fun traversable(): Traversable<IdentityProxy> = object: IdentityTraversable {}

        /**
         * Create an instance of this comonad.
         */
        fun comonad(): Comonad<IdentityProxy> = object: IdentityComonad {}

        /**
         * Entry point for monad bindings which enables for comprehension.
         */
        fun <A> forC(block: suspend MonadSyntax<IdentityProxy>.() -> A): Identity<A> =
                Identity.monad().forC.monad(block).narrow()

    }

}   // Identity
