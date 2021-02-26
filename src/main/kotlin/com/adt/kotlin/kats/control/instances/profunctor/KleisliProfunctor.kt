package com.adt.kotlin.kats.control.instances.profunctor

import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.kleisli.KleisliF.kleisli
import com.adt.kotlin.kats.control.data.kleisli.KleisliOf
import com.adt.kotlin.kats.control.data.kleisli.narrow
import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.Profunctor

interface KleisliProfunctor<F, A> : Profunctor<Kind1<Kind1<Kleisli.KleisliProxy, F>, A>> {

    fun monad(): Monad<F>

    /**
     * Map over both arguments at the same time.
     */
    //fun <A, B, C, D> dimap(v: Kind1<Kind1<F, B>, C>, f: (A) -> B, g: (C) -> D): Kind1<Kind1<F, A>, D>
    /*****
    override fun <B, C, D, E> dimap(v: KleisliOf<F, C, D>, f: (B) -> C, g: (D) -> E): Kleisli<F, B, E> {
        val mf: Monad<F> = monad()
        val vKleisli: Kleisli<F, C, D> = v.narrow()
        return kleisli(compose(compose(mf.liftM(g), vKleisli.run), f))
    }   // dimap
    *****/

}   // KleisliProfunctor
