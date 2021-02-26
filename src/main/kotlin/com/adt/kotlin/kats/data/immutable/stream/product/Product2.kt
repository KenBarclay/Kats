package com.adt.kotlin.kats.data.immutable.stream.product

abstract class Product2<A, B> {

    /**
     * Access the first element of the product.
     *
     * @return                  the first element of the product
     */
    abstract fun first(): A

    /**
     * Access the second element of the product.
     *
     * @return                  the second element of the product
     */
    abstract fun second(): B

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
            @Suppress("UNCHECKED_CAST") val otherProduct2: Product2<A, B> = other as Product2<A, B>
            (this.first() == otherProduct2.first()) && (this.second() == otherProduct2.second())
        }
    }   // equals

    override fun toString(): String = "Product2(${this.first()}, ${this.second()})"

    /**
     * Swap the elements around in this product.
     *
     * @return                  a new product-2 with the elements swapped
     */
    fun swap(): Product2<B, A> {
        val self: Product2<A, B> = this
        return object: Product2<B, A>() {
            override fun first(): B = self.second()
            override fun second(): A = self.first()
        }
    }   // swap

    /**
     * Map the first element of the product.
     *
     * @param f                 the function to map with
     * @return                  a product with the given function applied
     */
    fun <C> map1(f: (A) -> C): Product2<C, B> {
        val self: Product2<A, B> = this
        return object: Product2<C, B>() {
            override fun first(): C = f(self.first())
            override fun second(): B = self.second()
        }
    }   // map2

    /**
     * Map the second element of the product.
     *
     * @param f                 the function to map with
     * @return                  a product with the given function applied
     */
    fun <C> map2(f: (B) -> C): Product2<A, C> {
        val self: Product2<A, B> = this
        return object: Product2<A, C>() {
            override fun first(): A = self.first()
            override fun second(): C = f(self.second())
        }
    }   // map2

}   // Product2
