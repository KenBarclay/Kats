package com.adt.kotlin.kats.hkfp.laws

import com.adt.kotlin.kats.data.immutable.identity.IdentityF.identity
import com.adt.kotlin.kats.data.instances.monoid.DualMonoid
import com.adt.kotlin.kats.data.instances.monoid.EndoMonoid
import com.adt.kotlin.kats.data.instances.monoid.IdentityMonoid
import com.adt.kotlin.kats.hkfp.fp.FunctionF.flip
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*


class FoldableLaws<F>(val foldable: Foldable<F>) {

    fun <A, B> foldRightLaw(v: Kind1<F, A>, e: B, f: (A) -> (B) -> B): Boolean {
        return foldable.foldRight(v, e, f) == appEndo(foldable.foldMap(v, EndoMonoid()){a: A -> endo(f(a))})(e)
    }   // foldRightLaw

    fun <A, B> foldLeftLaw(v: Kind1<F, A>, e: B, f: (B) -> (A) -> B): Boolean {
        return foldable.foldLeft(v, e, f) == appEndo(appDual(foldable.foldMap(v, DualMonoid(EndoMonoid())){a: A -> dual(endo(flip(f)(a)))}))(e)
    }   // foldLeftLaw

    fun <A> foldLaw(v: Kind1<F, A>, ma: Monoid<A>): Boolean {
        return foldable.fold(v, ma) == foldable.foldMap(v, IdentityMonoid(ma)){a: A -> identity(a)}.value
    }   // foldLaw

}   // FoldableLaws
