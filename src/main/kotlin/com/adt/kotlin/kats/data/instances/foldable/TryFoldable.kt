package com.adt.kotlin.kats.data.instances.foldable

import com.adt.kotlin.kats.data.immutable.tri.Try
import com.adt.kotlin.kats.data.immutable.tri.Try.TryProxy
import com.adt.kotlin.kats.data.immutable.tri.narrow
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable


interface TryFoldable : Foldable<TryProxy> {

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <A, B> foldLeft(v: Kind1<TryProxy, A>, e: B, f: (B) -> (A) -> B): B {
        val vTry: Try<A> = v.narrow()
        return vTry.foldLeft(e, f)
    }   // foldLeft

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <A, B> foldRight(v: Kind1<TryProxy, A>, e: B, f: (A) -> (B) -> B): B {
        val vTry: Try<A> = v.narrow()
        return vTry.foldRight(e, f)
    }   // foldRight

    override fun <A, B> foldRight(v: Kind1<TryProxy, A>, e: B, f: (A, B) -> B): B = foldRight(v, e, C2(f))

}   // TryFoldable
