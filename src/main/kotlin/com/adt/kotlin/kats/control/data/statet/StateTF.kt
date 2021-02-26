package com.adt.kotlin.kats.control.data.statet

import com.adt.kotlin.kats.hkfp.kind.Kind1



object StateTF {

    /**
     * Factory constructor function.
     */
    fun <F, S, A> statet(f: (S) -> Kind1<F, Pair<S, A>>): StateT<F, S, A> = StateT(f)

}   // StateTF
