package com.adt.kotlin.kats.control.data.compose

import com.adt.kotlin.kats.hkfp.kind.Kind1

object ComposeF {

    /**
     * Factory constructor function.
     */
    fun <F, G, A> compose(compose: Kind1<F, Kind1<G, A>>): Compose<F, G, A> =
            Compose(compose)

}   // ComposeF
