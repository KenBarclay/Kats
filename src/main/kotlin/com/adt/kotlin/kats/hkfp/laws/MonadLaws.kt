package com.adt.kotlin.kats.hkfp.laws

/**
 * The left identity law states that if we take a value, put it in a default
 *   context with inject and then feed it to a function using bind, it is the
 *   same as applying the function to the value. The right identity law that
 *   if we have a monadic value and we use bind to feed it to inject, the result
 *   is the original monadic value. The associativity law says that when we have
 *   a chain of monadic function applications with bind, it does not matter how
 *   they are nested.
 *
 * @author	                    Ken Barclay
 * @since                       January 2019
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad



class MonadLaws<F>(val monad: Monad<F>) {

    /**
     * The first monad law states that if we take a value, put it in a default
     *   context with inject and then feed it to a function by using bind, it
     *   is the same as just taking the value and applying the function to it.
     */
    fun <A, B> leftIdentityLaw(a: A, f: (A) -> Kind1<F, B>): Boolean =
            monad.run{ bind(inject(a), f) == f(a) }

    /**
     * The second law states that if we have a monadic value and we use bind
     *   to feed it to inject, the result is our original monadic value.
     */
    fun <A, B> rightIdentityLaw(fa: Kind1<F, A>, @Suppress("UNUSED_PARAMETER") f: (A) -> Kind1<F, B>): Boolean =
            monad.run{ bind(fa){a: A -> inject(a)} == fa }

    /**
     * The third monad law says that when we have a chain of monadic function
     *   applications with bind, it should not matter how they are nested.
     */
    fun <A, B, C> associativityLaw(fa: Kind1<F, A>, f: (A) -> Kind1<F, B>, g: (B) -> Kind1<F, C>): Boolean =
            monad.run{ bind(bind(fa, f), g) == bind(fa){a: A -> bind(f(a), g)} }

}   // MonadLaws
