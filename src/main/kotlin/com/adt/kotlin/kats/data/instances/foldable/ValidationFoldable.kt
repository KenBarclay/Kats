package com.adt.kotlin.kats.data.instances.foldable

import com.adt.kotlin.kats.data.immutable.validation.Validation
import com.adt.kotlin.kats.data.immutable.validation.Validation.ValidationProxy
import com.adt.kotlin.kats.data.immutable.validation.ValidationOf
import com.adt.kotlin.kats.data.immutable.validation.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface ValidationFoldable<E> : Foldable<Kind1<ValidationProxy, E>> {

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <A, B> foldLeft(v: ValidationOf<E, A>, e: B, f: (B) -> (A) -> B): B {
        val vValidation: Validation<E, A> = v.narrow()
        return vValidation.foldLeft(e, f)
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
    override fun <A, B> foldRight(v: ValidationOf<E, A>, e: B, f: (A) -> (B) -> B): B {
        val vValidation: Validation<E, A> = v.narrow()
        return vValidation.foldRight(e, f)
    }   // foldRight

}   // ValidationFoldable
