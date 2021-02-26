package com.adt.kotlin.kats.control.instances.foldable

import com.adt.kotlin.kats.control.data.free.Free
import com.adt.kotlin.kats.control.data.free.Free.Pure
import com.adt.kotlin.kats.control.data.free.Free.Bind
import com.adt.kotlin.kats.control.data.free.Free.FreeProxy
import com.adt.kotlin.kats.control.data.free.FreeOf
import com.adt.kotlin.kats.control.data.free.narrow

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.fp.FunctionF.flip
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Foldable



interface FreeFoldable<F> : Foldable<Kind1<FreeProxy, F>> {

    fun foldable(): Foldable<F>

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: B -> A -> B
     * @return                  folded result
     */
    override fun <A, B> foldLeft(v: FreeOf<F, A>, e: B, f: (B) -> (A) -> B): B {
        val ff: Foldable<F> = foldable()
        fun recFoldLeft(v: Free<F, A>, e: B): B {
            return when (v) {
                is Pure -> f(e)(v.a)
                is Bind -> ff.foldLeft(v.free, e, flip(C2(::recFoldLeft)))
            }
        }   // recFoldLeft

        val vFree: Free<F, A> = v.narrow()
        return recFoldLeft(vFree, e)
    }   // foldLeft

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * @param v                 the context
     * @param e                 initial value
     * @param f                 curried binary function:: A -> B -> B
     * @return                  folded result
     */
    override fun <A, B> foldRight(v: FreeOf<F, A>, e: B, f: (A) -> (B) -> B): B {
        val ff: Foldable<F> = foldable()
        fun recFoldRight(v: Free<F, A>, e: B): B {
            return when (v) {
                is Pure -> f(v.a)(e)
                is Bind -> ff.foldRight(v.free, e, C2(::recFoldRight))
            }
        }   // recFoldRight

        val vFree: Free<F, A> = v.narrow()
        return recFoldRight(vFree, e)
    }   // foldRight

}   // FreeFoldable
