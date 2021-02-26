package com.adt.kotlin.kats.control.data.readert

import com.adt.kotlin.kats.hkfp.kind.Kind1



object ReaderTF {

    /**
     * Factory constructor function.
     */
    fun <F, A, B> readert(f: (A) -> Kind1<F, B>): ReaderT<F, A, B> = ReaderT(f)

}   // ReaderTF
