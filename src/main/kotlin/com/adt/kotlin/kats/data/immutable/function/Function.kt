package com.adt.kotlin.kats.data.immutable.function

/**
 * A class representing a unary function.
 *
 * @param A                     the function argument type
 * @param B                     the function return type
 *
 * @author	                    Ken Barclay
 * @since                       September 2018
 */

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.function.Function.FunctionProxy
import com.adt.kotlin.kats.data.instances.applicative.EitherApplicative
import com.adt.kotlin.kats.data.instances.applicative.FunctionApplicative
import com.adt.kotlin.kats.data.instances.functor.EitherFunctor
import com.adt.kotlin.kats.data.instances.functor.FunctionFunctor
import com.adt.kotlin.kats.data.instances.monad.EitherMonad
import com.adt.kotlin.kats.data.instances.monad.FunctionMonad

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Monad


typealias FunctionOf<A, B> = Kind1<Kind1<FunctionProxy, A>, B>

class Function<A, out B>(val func: (A) -> B) : Kind1<Kind1<FunctionProxy, A>, B> {

    class FunctionProxy private constructor()             // proxy for the Function context

    /**
     * Execute the function.
     */
    operator fun invoke(a: A): B = func(a)

    /**
     * Apply function g to an input c and apply function f to the result as in f(g(x)).
     */
    fun <C> compose(g: (C) -> A): Function<C, B> = Function{c: C -> func(g(c))}

    /**
     * Apply function f to an input a and apply function g to the result as in g(f(x)).
     */
    fun <C> forwardCompose(g: (B) -> C): Function<A, C> = Function{a: A -> g(func(a))}



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <A> functor(): Functor<Kind1<FunctionProxy, A>> = object: FunctionFunctor<A> {}

        /**
         * Create an instance of this applicative.
         */
        fun <A> applicative(): Applicative<Kind1<FunctionProxy, A>> = object: FunctionApplicative<A> {}

        /**
         * Create an instance of this monad.
         */
        fun <A> monad(): Monad<Kind1<FunctionProxy, A>> = object: FunctionMonad<A> {}

    }

}   // Function
