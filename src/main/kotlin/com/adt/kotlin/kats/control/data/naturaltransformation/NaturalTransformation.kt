package com.adt.kotlin.kats.control.data.naturaltransformation

/** A universally quantified function, often written as F ~> G.
 *
 * Can be used to encode first-class functor transformations in the
 *   same way functions encode first-class concrete value morphisms.
 */

import com.adt.kotlin.kats.hkfp.kind.Kind1



interface NaturalTransformation<F, G> {

    operator fun <A> invoke(fa: Kind1<F, A>): Kind1<G, A>

    fun <E> compose(ef: NaturalTransformation<E, F>): NaturalTransformation<E, G> {
        val self: NaturalTransformation<F, G> = this
        return object: NaturalTransformation<E, G> {
            override fun <A> invoke(fa: Kind1<E, A>): Kind1<G, A> =
                    self(ef(fa))
        }
    }   // compose

}   // NaturalTransformation
