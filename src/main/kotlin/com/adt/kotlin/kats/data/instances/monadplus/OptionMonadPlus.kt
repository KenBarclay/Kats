package com.adt.kotlin.kats.data.instances.monadplus

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.OptionF
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.narrow
import com.adt.kotlin.kats.data.instances.alternative.OptionAlternative
import com.adt.kotlin.kats.data.instances.monad.OptionMonad

import com.adt.kotlin.kats.hkfp.typeclass.MonadPlus



interface OptionMonadPlus : MonadPlus<OptionProxy>, OptionAlternative, OptionMonad {

    /**
     * An empty result.
     */
    override fun <A> mzero(): Option<A> = OptionF.none()

    /**
     * Combine two results into one.
     */
    override fun <A> mplus(fa1: OptionOf<A>, fa2: OptionOf<A>): Option<A> {
        val fa1Option: Option<A> = fa1.narrow()
        val fa2Option: Option<A> = fa2.narrow()
        return when(fa1Option) {
            is None -> fa2Option
            is Some -> fa1Option
        }
    }   // mplus

}   // OptionMonadPlus
