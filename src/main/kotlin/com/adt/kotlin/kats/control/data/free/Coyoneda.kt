package com.adt.kotlin.kats.control.data.free

import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor

abstract class Coyoneda<F, A, I> {
    abstract val fi: Kind1<F, I>
    abstract val k: (I) -> A

    fun run(ff: Functor<F>): Kind1<F, A> =
            ff.fmap(fi, k)

    fun <B> map(f: (A) -> B): Coyoneda<F, B, I> =
            coyoneda(fi, compose(f, k))



    companion object {
        fun <F, A, B> coyoneda(fa: Kind1<F, A>, kk: (A) -> B): Coyoneda<F, B, A> =
                object: Coyoneda<F, B, A>() {
                    override val fi: Kind1<F, A> = fa
                    override val k: (A) -> B = kk
                }
    }
}   // Coyoneda
