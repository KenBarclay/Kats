package com.adt.kotlin.kats.data.immutable.stream.product

object Product1F {

    /**
     * A function that puts an element in a product-1.
     *
     * @param a                 the element
     * @return                  the product-1
     */
    fun <A> product1(a: A): Product1<A> =
            object: Product1<A>() {
                override fun first(): A = a
            }

    /**
     * Convert a function into a product-1
     */
    fun <A> product1(f: () -> A): Product1<A> =
            object: Product1<A>() {
                override fun first(): A = f()
            }

}   // Product1F
