package com.adt.kotlin.kats.mtl.typeclass

/**
 * XXX
 *
 * The minimal complete definition is writer or pass together with listen
 *   and pass.
 *
 * @author	                    Ken Barclay
 * @since                       Feburary 2021
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad



interface MonadWriter<F, W> : Monad<F> {

    /**
     * Modify the accumulator. censor v f is an action that executes the
     *   action v and applies the function f to its output, leaving the
     *   return value unchanged.
     */
    fun <A> censor(v: Kind1<F, A>, f: (W) -> W): Kind1<F, A> =
            bind(listen(v)){p: Pair<W, A> -> writer(Pair(f(p.first), p.second))}

    /**
     * Run the effect and pair the accumulator with the result. listen v is
     *   an action that executes the action v and adds its output to the value
     *   of the computation.
     */
    fun <A> listen(v: Kind1<F, A>): Kind1<F, Pair<W, A>>

    /**
     * Pair the value with an inspection of the accumulator. listens v f is an
     *   action that executes the action v and adds the result of applying f to
     *   the output to the value of the computation.
     */
    fun <A, B> listens(v: Kind1<F, A>, f: (W) -> B): Kind1<F, Pair<B, A>> =
            fmap(listen(v)){p: Pair<W, A> -> Pair(f(p.first), p.second)}

    /**
     * Apply the effectful function to the accumulator. pass v is an action
     *   that executes the action v, which returns a value and a function,
     *   and returns the value, applying the function to the output.
     */
    fun <A> pass(v: Kind1<F, Pair<(W) -> W, A>>): Kind1<F, A>

    /**
     * Lift the log into the effect. tell w is an action that produces the
     *   output w.
     */
    fun tell(w: W): Kind1<F, Unit> = writer(Pair(w, Unit))

    /**
     * Lift a writer action into the effect. writer (w, a) embeds a simple
     *   writer action.
     */
    fun <A> writer(wa: Pair<W, A>): Kind1<F, A>

}   // MonadWriter
