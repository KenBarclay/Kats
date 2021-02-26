package com.adt.kotlin.kats.data.instances.functor

import com.adt.kotlin.kats.data.immutable.validation.Validation
import com.adt.kotlin.kats.data.immutable.validation.Validation.ValidationProxy
import com.adt.kotlin.kats.data.immutable.validation.ValidationOf
import com.adt.kotlin.kats.data.immutable.validation.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface ValidationFunctor<E> : Functor<Kind1<ValidationProxy, E>> {

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <A, C> fmap(v: ValidationOf<E, A>, f: (A) -> C): Validation<E, C> {
        val vValidation: Validation<E, A> = v.narrow()
        return vValidation.fmap(f)
    }   // fmap



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the Validation context.
     */
    override fun <A, C> lift(f: (A) -> C): (ValidationOf<E, A>) -> Validation<E, C> =
            {eab: ValidationOf<E, A> ->
                fmap(eab, f)
            }

}   // ValidationFunctor
