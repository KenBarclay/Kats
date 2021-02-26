package com.adt.kotlin.kats.mtl.instances.traversable

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.narrow
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C3
import com.adt.kotlin.kats.hkfp.fp.FunctionF.partial
import com.adt.kotlin.kats.hkfp.fp.FunctionF.rotateLeft
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Traversable

import com.adt.kotlin.kats.mtl.data.option.OptionT
import com.adt.kotlin.kats.mtl.data.option.OptionT.OptionTProxy
import com.adt.kotlin.kats.mtl.data.option.OptionTOf
import com.adt.kotlin.kats.mtl.data.option.narrow
import com.adt.kotlin.kats.mtl.instances.foldable.OptionTFoldable
import com.adt.kotlin.kats.mtl.instances.functor.OptionTFunctor



interface OptionTTraversable<F> : Traversable<Kind1<OptionTProxy, F>>, OptionTFoldable<F>, OptionTFunctor<F> {

    fun traversable(): Traversable<F>
    override fun foldable(): Foldable<F> = traversable()
    override fun functor(): Functor<F> = traversable()

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    override fun <G, A, B> traverse(v: OptionTOf<F, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, OptionT<F, B>> {
        val tf: Traversable<F> = traversable()
        val vOptionT: OptionT<F, A> = v.narrow()
        val opTraversable: Traversable<OptionProxy> = Option.traversable()
        val opTraverseC: ((A) -> Kind1<G, B>) -> (OptionOf<A>) -> Kind1<G, OptionOf<B>> = partial(ag, rotateLeft(C3(opTraversable::traverse)))

        val fafb: (OptionOf<A>) -> Kind1<G, OptionOf<B>> = opTraverseC(f)
        val gfb: Kind1<G, Kind1<F, OptionOf<B>>> = tf.traverse(vOptionT.runOptionT, ag, fafb)

        return ag.fmap(gfb){fb: Kind1<F, OptionOf<B>> ->
            OptionT(tf.fmap(fb){ob: OptionOf<B> -> ob.narrow()})
        }
    }   // traverse

}   // OptionTTraversable
