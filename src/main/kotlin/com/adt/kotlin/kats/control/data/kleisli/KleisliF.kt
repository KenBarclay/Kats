package com.adt.kotlin.kats.control.data.kleisli

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative



object KleisliF {

    /**
     * Factory constructor function.
     */
    fun <F, A, B> kleisli(f: (A) -> Kind1<F, B>): Kleisli<F, A, B> = Kleisli(f)

    /**
     * Wrap the applicative pure function in a Kleisli context.
     */
    fun <A, F> ask(af: Applicative<F>): Kleisli<F, A, A> =
            af.run{
                Kleisli(::pure)
            }   // ask

    /**
     * Take a value of any type and returns a Kleisli context enclosing the value.
     *
     * Examples:
     *   pure(5, optionApplicative())(5) = some(5)
     *   pure(5, listApplicative())(5) = [5]
     *   pure(5, optionApplicative())(5) = ask(optionApplicative())(5)
     */
    fun <A, F, B> pure(b: B, af: Applicative<F>): Kleisli<F, A, B> =
            af.run{
                Kleisli { _: A -> pure(b) }
            }   // pure

}   // KleisliF
