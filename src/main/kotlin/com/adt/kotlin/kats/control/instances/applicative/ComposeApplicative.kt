package com.adt.kotlin.kats.control.instances.applicative

import com.adt.kotlin.kats.control.data.compose.Compose
import com.adt.kotlin.kats.control.data.compose.Compose.ComposeProxy
import com.adt.kotlin.kats.control.data.compose.ComposeOf
import com.adt.kotlin.kats.control.data.compose.narrow
import com.adt.kotlin.kats.control.instances.functor.ComposeFunctor
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.fp.FunctionF.flip
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface ComposeApplicative<F, G> : Applicative<Kind1<Kind1<ComposeProxy, F>, G>>, ComposeFunctor<F, G> {

    fun applicativeF(): Applicative<F>
    fun applicativeG(): Applicative<G>

    override fun functorF(): Functor<F> = applicativeF()
    override fun functorG(): Functor<G> = applicativeG()

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): Compose<F, G, A> =
            Compose(applicativeF().pure(applicativeG().pure(a)))

    /**
     * Apply the function wrapped in the Option context to the content of the
     *   value also wrapped in a Option context.
     *
     * Examples:
     *   XXX
     */
    override fun <A, B> ap(v: ComposeOf<F, G, A>, f: ComposeOf<F, G, (A) -> B>): Compose<F, G, B> {
        val aF: Applicative<F> = applicativeF()
        val aG: Applicative<G> = applicativeG()

        val vCompose: Compose<F, G, A> = v.narrow()
        val fCompose: Compose<F, G, (A) -> B> = f.narrow()
        val vc: Kind1<F, Kind1<G, A>> = vCompose.compose
        val fc: Kind1<F, Kind1<G, (A) -> B>> = fCompose.compose

        val aGC: (Kind1<G, (A) -> B>) -> (Kind1<G, A>) -> Kind1<G, B> = flip(C2(aG::ap))

        return Compose(aF.liftA2(aGC)(fc)(vc))
    }   // ap



// ---------- utility functions ---------------------------

    override fun <A, B, C> liftA2(f: (A) -> (B) -> C): (ComposeOf<F, G, A>) -> (ComposeOf<F, G, B>) -> Compose<F, G, C> =
            {fga: ComposeOf<F, G, A> ->
                {fgb: ComposeOf<F, G, B> ->
                    val fgaCompose: Compose<F, G, A> = fga.narrow()
                    val fgbCompose: Compose<F, G, B> = fgb.narrow()
                    val fgac: Kind1<F, Kind1<G, A>> = fgaCompose.compose
                    val fgbc: Kind1<F, Kind1<G, B>> = fgbCompose.compose
                    val appF: Applicative<F> = applicativeF()
                    val appG: Applicative<G> = applicativeG()

                  Compose(appF.liftA2(appG.liftA2(f))(fgac)(fgbc))
                }
            }   // liftA2

}   // ComposeApplicative
