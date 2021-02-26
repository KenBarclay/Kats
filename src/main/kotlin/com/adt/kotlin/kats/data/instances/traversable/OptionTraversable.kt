package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.instances.foldable.OptionFoldable
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.narrow
import com.adt.kotlin.kats.data.instances.functor.OptionFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface OptionTraversable : Traversable<OptionProxy>, OptionFoldable, OptionFunctor {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     *
     * Examples:
     *   none.traverse(optionApplicative()){n -> (n % 2 == 0)} = none
     *   some(5).traverse(optionApplicative()){n -> (n % 2 == 0)} = some(false)
     *   some(6).traverse(optionApplicative()){n -> (n % 2 == 0)} = some(true)
     *
     *   none.traverse(listApplicative()){n -> [n % 2 == 0]} = [none]
     *   some(5).traverse(listApplicative()){n -> [n % 2 == 0]} = [some(false)]
     *   some(6).traverse(listApplicative()){n -> [n % 2 == 0]} = [some(true)]
     */
    override fun <G, A, B> traverse(v: OptionOf<A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Option<B>> {
        val vOption: Option<A> = v.narrow()
        return vOption.traverse(ag, f)
    }

}   // OptionTraversable
