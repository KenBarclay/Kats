package com.adt.kotlin.kats.control.instances.traversable

import com.adt.kotlin.kats.control.data.compose.Compose
import com.adt.kotlin.kats.control.data.compose.Compose.ComposeProxy
import com.adt.kotlin.kats.control.data.compose.ComposeOf
import com.adt.kotlin.kats.control.data.compose.narrow
import com.adt.kotlin.kats.control.instances.foldable.ComposeFoldable
import com.adt.kotlin.kats.control.instances.functor.ComposeFunctor
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C3
import com.adt.kotlin.kats.hkfp.fp.FunctionF.rotateRight2
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface ComposeTraversable<F, G> : Traversable<Kind1<Kind1<ComposeProxy, F>, G>>, ComposeFoldable<F, G>, ComposeFunctor<F, G> {

    fun traversableF(): Traversable<F>
    fun traversableG(): Traversable<G>

    override fun foldableF(): Foldable<F> = traversableF()
    override fun foldableG(): Foldable<G> = traversableG()

    override fun functorF(): Functor<F> = traversableF()
    override fun functorG(): Functor<G> = traversableG()

    override fun <GG, A, B> traverse(v: ComposeOf<F, G, A>, ag: Applicative<GG>, f: (A) -> Kind1<GG, B>): Kind1<GG, Compose<F, G, B>> {
        val tF: Traversable<F> = traversableF()
        val tG: Traversable<G> = traversableG()

        val vCompose: Compose<F, G, A> = v.narrow()
        val vc: Kind1<F, Kind1<G, A>> = vCompose.compose

        val traverseGC: ((A) -> Kind1<GG, B>) -> (Applicative<GG>) -> (Kind1<G, A>) -> Kind1<GG, Kind1<G, B>> = rotateRight2(C3(tG::traverse))
        val traverseFC: ((Kind1<G, A>) -> Kind1<GG, Kind1<G, B>>) -> (Applicative<GG>) -> (Kind1<F, Kind1<G, A>>) -> Kind1<GG, Kind1<F, Kind1<G, B>>> = rotateRight2(C3(tF::traverse))

        val tGC: (Kind1<G, A>) -> Kind1<GG, Kind1<G, B>> = traverseGC(f)(ag)
        val tFC: (Kind1<F, Kind1<G, A>>) -> Kind1<GG, Kind1<F, Kind1<G, B>>> = traverseFC(tGC)(ag)
        return ag.fmap(tFC(vc)){fgb: Kind1<F, Kind1<G, B>> -> Compose(fgb)}
    }   // traverse

}   // ComposeTraversable
