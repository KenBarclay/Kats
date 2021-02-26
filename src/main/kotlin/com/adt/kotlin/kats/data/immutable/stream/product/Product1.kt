package com.adt.kotlin.kats.data.immutable.stream.product

abstract class Product1<A> {

    /**
     * Access the first element of the product.
     *
     * @return                  the first element of the product
     */
    abstract fun first(): A

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param other             the other object
     * @return                  true if "equal", false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val otherProduct1: Product1<A> = other as Product1<A>
            (this.first() == otherProduct1.first())
        }
    }   // equals

    override fun toString(): String = "Product1(${this.first()})"

    /**
     * Map the element of the product.
     *
     * @param f                 the function to map with
     * @return                  a product with the given function applied
     */
    fun <B> map(f: (A) -> B): Product1<B> {
        val self: Product1<A> = this
        return object: Product1<B>() {
            override fun first(): B = f(self.first())
        }
    }   // map

    /**
     * Provides a memoising P1 that remembers its value.
     *
     * @return                  a product that calls this product once and remembers the value for subsequent calls
     */
    fun memo(): Product1<A> {
        val self: Product1<A> = this
        return object: Product1<A>() {
            override fun first(): A = self.first()
        }
    }   // memo

}   // Product1
