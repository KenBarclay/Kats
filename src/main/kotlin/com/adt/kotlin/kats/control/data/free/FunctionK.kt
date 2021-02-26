package com.adt.kotlin.kats.control.data.free

import com.adt.kotlin.kats.hkfp.kind.Kind1

interface FunctionK<F, G> {

    operator fun <A> invoke(fa: Kind1<F, A>): Kind1<G, A>

    companion object {

        fun <H> id(): FunctionK<H, H> = object: FunctionK<H, H> {
            override fun <A> invoke(fa: Kind1<H, A>): Kind1<H, A> = fa
        }
    }

}   // FunctionK
