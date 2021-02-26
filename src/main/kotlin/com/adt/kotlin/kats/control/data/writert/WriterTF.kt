package com.adt.kotlin.kats.control.data.writert

import com.adt.kotlin.kats.hkfp.kind.Kind1



object WriterTF {

    /**
     * Factory constructor function.
     */
    fun <F, W, A> writert(run: Kind1<F, Pair<W, A>>): WriterT<F, W, A> = WriterT(run)

}   // WriterTF
