package com.adt.kotlin.kats.control.data.free

import com.adt.kotlin.kats.control.data.free.Free.Pure
import com.adt.kotlin.kats.control.data.free.Free.Bind

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Functor


object FreeF {

    /**
     * A factory function for creating a Free.
     */
    fun <F, A> liftF(ff: Functor<F>, fa: Kind1<F, A>): Free<F, A> =
            Bind(ff.fmap(fa){a: A -> Pure(a)})

}   // FreeF
