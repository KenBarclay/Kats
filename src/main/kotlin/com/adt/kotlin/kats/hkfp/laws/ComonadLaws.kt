package com.adt.kotlin.kats.hkfp.laws

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose
import com.adt.kotlin.kats.hkfp.fp.FunctionF.flip
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Comonad



class ComonadLaws<F>(val comonad: Comonad<F>) {

    fun <A> identityLaw(fa: Kind1<F, A>): Boolean {
        return comonad.run{
            extend(fa, ::extract) == fa
        }
    }   // identityLaw

    fun <A, B> extractExtendLaw(fa: Kind1<F, A>, f: (Kind1<F, A>) -> B): Boolean {
        return comonad.run{
            extract(extend(fa, f)) == f(fa)
        }
    }   // extractExtendLaw

    fun <A, B, C> extendExtendLaw(fa: Kind1<F, A>, f: (Kind1<F, B>) -> C, g: (Kind1<F, A>) -> B): Boolean {
        val extendC: ((Kind1<F, A>) -> B) -> (Kind1<F, A>) -> Kind1<F, B> = flip(C2(comonad::extend))
        return comonad.run{
            extend(extend(fa, g), f) == extend(fa, compose(f, extendC(g)))
        }
    }   // extendExtendLaw

}   // ComonadLaws
