package com.adt.kotlin.kats.hkfp.laws

/**
 * Functors must preserve identity and composition laws. When performing the
 *   mapping operation, if the values in the functor are mapped to themselves,
 *   the result will be an unmodified functor. If two sequential mapping
 *   operations are performed one after the other using two functions, the
 *   result should be the same as a single mapping operation with one
 *   function that is equivalent to applying the first function to the result
 *   of the second.
 *
 * @author	                    Ken Barclay
 * @since                       January 2019
 */

import com.adt.kotlin.kats.hkfp.fp.FunctionF.id
import com.adt.kotlin.kats.hkfp.fp.FunctionF.o

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



class FunctorLaws<F>(val functor: Functor<F>) {

    /**
     * The first functor law states that if we map the identity function id
     *   over a functor, the functor that we get back should be the same as
     *   the original functor. It means that fmap id = id. So essentially,
     *   this says that if we do fmap id over a functor, it should be the
     *   same as just calling id on the functor.
     */
    fun <A> identityLaw(v: Kind1<F, A>): Boolean {
        val id: (A) -> A = {a: A -> a}
        return functor.run{ fmap(v, id) == id<Kind1<F, A>>()(v) }
    }

    /**
     * The second law says that composing two functions and then mapping the
     *   resulting function over a functor should be the same as first mapping
     *   one function over the functor and then mapping the other one. Formally
     *   written, this means that fmap (f . g) = fmap f . fmap g.
     */
    fun <A, B, C> compositionLaw(v: Kind1<F, A>, f: (B) -> C, g: (A) -> B): Boolean =
            functor.run{
                fmap(v, f o g) == (lift(f) o lift(g))(v)
            }

}   // FunctorLaws
