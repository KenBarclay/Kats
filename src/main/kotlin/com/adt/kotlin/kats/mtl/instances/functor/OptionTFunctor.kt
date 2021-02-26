package com.adt.kotlin.kats.mtl.instances.functor

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.instances.functor.OptionFunctor
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.fp.FunctionF.flip
import com.adt.kotlin.kats.mtl.data.option.OptionT.OptionTProxy

import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.mtl.data.option.OptionT
import com.adt.kotlin.kats.mtl.data.option.OptionTOf
import com.adt.kotlin.kats.mtl.data.option.narrow

import com.adt.kotlin.kats.hkfp.kind.Kind1



interface OptionTFunctor<F> : Functor<Kind1<OptionTProxy, F>> {

    fun functor(): Functor<F>

    /**
     * Apply the function to the content(s) of the context.
     */
    override fun <A, B> fmap(v: OptionTOf<F, A>, f: (A) -> B): OptionT<F, B> {
        val ff: Functor<F> = functor()
        val vOptionT: OptionT<F, A> = v.narrow()
        val opFunctor: OptionFunctor = Option.functor() as OptionFunctor

        val opFmapC: ((A) -> B) -> (OptionOf<A>) -> Option<B> = flip(C2(opFunctor::fmap))
        val opab: (OptionOf<A>) -> Option<B> = opFmapC(f)

        return OptionT(ff.fmap(vOptionT.runOptionT, opab))
    }   // fmap

}   // OptionTFunctor
