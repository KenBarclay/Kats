package com.adt.kotlin.kats.mtl.typeclass

/**
 * The Reader monad (also called the Environment monad) represents a computation,
 *   which can read values from a shared environment, pass values from function
 *   to function, and execute sub-computations in a modified environment. Using
 *   Reader monad for such computations is often clearer and easier than using
 *   the State monad.
 *
 * The minimal complete definition is provided by ask or reader, together with local.
 *
 * @author	                    Ken Barclay
 * @since                       Feburary 2021
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface MonadReader<F, A> : Monad<F> {

    /**
     * Get the monad environment.
     *
     * @return                  the current environment
     */
    fun ask(): Kind1<F, A>

    /**
     * Execute a computation id a modified environment.
     *
     * @param v                 reader to run in the modified environment
     * @param f                 the function to modify the environment
     * @return                  the modified environment
     */
    fun <B> local(v: Kind1<F, B>, f: (A) -> A): Kind1<F, B>

    /**
     * Retrieve a function of the current environment.
     *
     * @param f                 the selector function to apply to the environment
     * @return                  the current environment
     */
    fun <B> reader(f: (A) -> B): Kind1<F, B> = fmap(ask(), f)

}   // MonadReader
