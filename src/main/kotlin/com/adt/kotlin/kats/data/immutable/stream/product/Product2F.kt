package com.adt.kotlin.kats.data.immutable.stream.product

object Product2F {

    fun <A, B> product2(a: A, b: B): Product2<A, B> = object: Product2<A, B>() {
        override fun first(): A = a
        override fun second(): B = b
    }

}   // Product2F
