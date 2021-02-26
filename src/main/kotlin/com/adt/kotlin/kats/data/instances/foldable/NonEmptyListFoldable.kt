package com.adt.kotlin.kats.data.instances.foldable

import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList.NonEmptyListProxy
import com.adt.kotlin.kats.data.immutable.nel.narrow

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface NonEmptyListFoldable : Foldable<NonEmptyListProxy> {

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <A, B> foldLeft(v: Kind1<NonEmptyListProxy, A>, e: B, f: (B) -> (A) -> B): B = v.narrow().foldLeft(e, f)

    override fun <A, B> foldLeft(v: Kind1<NonEmptyListProxy, A>, e: B, f: (B, A) -> B): B = foldLeft(v, e, C2(f))

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <A, B> foldRight(v: Kind1<NonEmptyListProxy, A>, e: B, f: (A) -> (B) -> B): B = v.narrow().foldRight(e, f)

    override fun <A, B> foldRight(v: Kind1<NonEmptyListProxy, A>, e: B, f: (A, B) -> B): B = foldRight(v, e, C2(f))

}   // NonEmptyListFoldable
