package com.adt.kotlin.kats.control.instances.foldable

import com.adt.kotlin.kats.control.data.compose.Compose
import com.adt.kotlin.kats.control.data.compose.Compose.ComposeProxy
import com.adt.kotlin.kats.control.data.compose.ComposeOf
import com.adt.kotlin.kats.control.data.compose.narrow
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C3
import com.adt.kotlin.kats.hkfp.fp.FunctionF.flip
import com.adt.kotlin.kats.hkfp.fp.FunctionF.rotateRight
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface ComposeFoldable<F, G> : Foldable<Kind1<Kind1<ComposeProxy, F>, G>> {

    fun foldableF(): Foldable<F>
    fun foldableG(): Foldable<G>

    override fun <A, B> foldLeft(v: ComposeOf<F, G, A>, e: B, f: (B) -> (A) -> B): B {
        val fF: Foldable<F> = foldableF()
        val fG: Foldable<G> = foldableG()

        val vCompose: Compose<F, G, A> = v.narrow()
        val vc: Kind1<F, Kind1<G, A>> = vCompose.compose

        val foldLeftGC: ((B) -> (A) -> B) -> (Kind1<G, A>) -> (B) -> B = rotateRight(C3(fG::foldLeft))
        val foldLeftFC: ((B) -> (Kind1<G, A>) -> B) -> (Kind1<F, Kind1<G, A>>) -> (B) -> B = rotateRight(C3(fF::foldLeft))

        return foldLeftFC(flip(foldLeftGC(f)))(vc)(e)
    }   // foldLeft

    override fun <A, B> foldRight(v: ComposeOf<F, G, A>, e: B, f: (A) -> (B) -> B): B {
        val fF: Foldable<F> = foldableF()
        val fG: Foldable<G> = foldableG()

        val vCompose: Compose<F, G, A> = v.narrow()
        val vc: Kind1<F, Kind1<G, A>> = vCompose.compose

        val foldRightGC: ((A) -> (B) -> B) -> (Kind1<G, A>) -> (B) -> B = rotateRight(C3(fG::foldRight))
        val foldRightFC: ((Kind1<G, A>) -> (B) -> B) -> (Kind1<F, Kind1<G, A>>) -> (B) -> B = rotateRight(C3(fF::foldRight))

        return foldRightFC(foldRightGC(f))(vc)(e)
    }   // foldRight

}   // ComposeFoldable
