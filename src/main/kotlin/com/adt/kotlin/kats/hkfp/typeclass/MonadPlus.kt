package com.adt.kotlin.kats.hkfp.typeclass

/**
 * Monads that also support choice and failure.
 *
 * The minimal complete definition is NOT provided.
 *
 * @author	                    Ken Barclay
 * @since                       December 2018
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1



interface MonadPlus<F> : Alternative<F>, Monad<F> {

    /**
     * An empty result.
     */
    fun <A> mzero(): Kind1<F, A> = empty()

    /**
     * Combine two results into one.
     */
    fun <A> mplus(fa1: Kind1<F, A>, fa2: Kind1<F, A>): Kind1<F, A> =
            combine(fa1, fa2)

}   // MonadPlus
