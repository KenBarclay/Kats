package com.adt.kotlin.kats.mtl.instances.foldable

import com.adt.kotlin.kats.control.data.compose.Compose
import com.adt.kotlin.kats.control.data.compose.Compose.ComposeProxy
import com.adt.kotlin.kats.control.data.compose.ComposeF.compose
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.mtl.data.option.OptionT
import com.adt.kotlin.kats.mtl.data.option.OptionT.OptionTProxy
import com.adt.kotlin.kats.mtl.data.option.OptionTOf
import com.adt.kotlin.kats.mtl.data.option.narrow



interface OptionTFoldable<F> : Foldable<Kind1<OptionTProxy, F>> {

    fun foldable(): Foldable<F>

    override fun <A, B> foldLeft(v: OptionTOf<F, A>, e: B, f: (B) -> (A) -> B): B {
        val ff: Foldable<F> = foldable()
        val vOptionT: OptionT<F, A> = v.narrow()
        val optFoldable: Foldable<OptionProxy> = Option.foldable()
        val ffco: Foldable<Kind1<Kind1<ComposeProxy, F>, OptionProxy>> = Compose.foldable(ff, optFoldable)
        return ffco.foldLeft(compose(vOptionT.runOptionT), e, f)
    }   // foldLeft

    override fun <A, B> foldRight(v: OptionTOf<F, A>, e: B, f: (A) -> (B) -> B): B {
        val ff: Foldable<F> = foldable()
        val vOptionT: OptionT<F, A> = v.narrow()
        val optFoldable: Foldable<OptionProxy> = Option.foldable()
        val ffco: Foldable<Kind1<Kind1<ComposeProxy, F>, OptionProxy>> = Compose.foldable(ff, optFoldable)
        return ffco.foldRight(compose(vOptionT.runOptionT), e, f)
    }   // foldRight

}   // OptionTFoldable
