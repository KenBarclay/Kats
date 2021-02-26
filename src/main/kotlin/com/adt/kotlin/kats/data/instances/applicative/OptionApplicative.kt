package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.instances.functor.OptionFunctor
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.data.immutable.option.OptionF
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.narrow

import com.adt.kotlin.kats.hkfp.typeclass.Applicative



/**
 * Applicative over an Option.
 */
interface OptionApplicative : Applicative<OptionProxy>, OptionFunctor {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): Option<A> = OptionF.some(a)

    /**
     * Apply the function wrapped in the Option context to the content of the
     *   value also wrapped in a Option context.
     *
     * Examples:
     *   let applicative = Option.applicative()
     *
     *   applicative.run{ ap(none(), some{n: Int -> n + 1}) } == none()
     *   applicative.run{ ap(some(5), some{n: Int -> n + 1}) } == some(6)
     *   applicative.run{ ap(some(5), none()) } == none()
     */
    override fun <A, B> ap(v: OptionOf<A>, f: OptionOf<(A) -> B>): Option<B> {
        val vOption: Option<A> = v.narrow()
        val fOption: Option<(A) -> B> = f.narrow()
        return vOption.ap(fOption)
    }   // ap

}   // OptionApplicative
