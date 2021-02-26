package com.adt.kotlin.kats.control.instances.functor

import com.adt.kotlin.kats.control.data.compose.Compose
import com.adt.kotlin.kats.control.data.compose.Compose.ComposeProxy
import com.adt.kotlin.kats.control.data.compose.ComposeOf
import com.adt.kotlin.kats.control.data.compose.narrow
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.fp.FunctionF.flip
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface ComposeFunctor<F, G> : Functor<Kind1<Kind1<ComposeProxy, F>, G>> {

    fun functorF(): Functor<F>
    fun functorG(): Functor<G>

    override fun <A, B> fmap(v: ComposeOf<F, G, A>, f: (A) -> B): Compose<F, G, B> {
        val fF: Functor<F> = functorF()
        val fG: Functor<G> = functorG()
        val vCompose: Compose<F, G, A> = v.narrow()
        val fGC: ((A) -> B) -> (Kind1<G, A>) -> Kind1<G, B> = flip(C2(fG::fmap))

        return Compose(fF.fmap(vCompose.compose, fGC(f)))
    }   // fmap



// ---------- utility functions ---------------------------

    /**
     * Lift a function into the ReaderT context.
     */
    override fun <A, B> lift(f: (A) -> B): (ComposeOf<F, G, A>) -> Compose<F, G, B> =
            {cfga: ComposeOf<F, G, A> ->
                fmap(cfga, f)
            }

}   // ComposeFunctor
