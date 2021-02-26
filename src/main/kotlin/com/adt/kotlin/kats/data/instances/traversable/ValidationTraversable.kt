package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.immutable.validation.Validation
import com.adt.kotlin.kats.data.immutable.validation.Validation.ValidationProxy
import com.adt.kotlin.kats.data.immutable.validation.ValidationOf
import com.adt.kotlin.kats.data.immutable.validation.narrow
import com.adt.kotlin.kats.data.instances.foldable.ValidationFoldable
import com.adt.kotlin.kats.data.instances.functor.ValidationFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface ValidationTraversable<E> : Traversable<Kind1<ValidationProxy, E>>, ValidationFoldable<E>, ValidationFunctor<E> {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    override fun <G, A, B> traverse(v: ValidationOf<E, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Validation<E, B>> {
        val vValidation: Validation<E, A> = v.narrow()
        return vValidation.traverse(ag, f)
    }   // traverse

}   // ValidationTraversable
