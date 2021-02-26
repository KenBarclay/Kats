package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.immutable.validation.Validation
import com.adt.kotlin.kats.data.immutable.validation.Validation.ValidationProxy
import com.adt.kotlin.kats.data.immutable.validation.Validation.Failure
import com.adt.kotlin.kats.data.immutable.validation.Validation.Success
import com.adt.kotlin.kats.data.immutable.validation.ValidationF.failure
import com.adt.kotlin.kats.data.immutable.validation.ValidationF.success
import com.adt.kotlin.kats.data.immutable.validation.ValidationOf
import com.adt.kotlin.kats.data.immutable.validation.narrow
import com.adt.kotlin.kats.data.instances.functor.ValidationFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface ValidationApplicative<E> : Applicative<Kind1<ValidationProxy, E>>, ValidationFunctor<E> {

    val se: Semigroup<E>

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): Validation<E, A> = Success(a)

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <A, B> ap(v: ValidationOf<E, A>, f: ValidationOf<E, (A) -> B>): Validation<E, B> {
        val vValidation: Validation<E, A> = v.narrow()
        val fValidation: Validation<E, (A) -> B> = f.narrow()
        return when (vValidation) {
            is Failure -> when (fValidation) {
                is Failure -> failure(se.run{ combine(vValidation.err, fValidation.err) })
                is Success -> failure(vValidation.err)
            }
            is Success -> when (fValidation) {
                is Failure -> failure(fValidation.err)
                is Success -> success(fValidation.value(vValidation.value))
            }
        }
    }   // ap

}   // ValidationApplicative
