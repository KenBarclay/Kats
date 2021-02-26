package com.adt.kotlin.kats.data.immutable.identity


object IdentityF {

    /**
     * Factory functions to create the base instances.
     */
    fun <A> identity(value: A): Identity<A> = Identity(value)



    // Functor utility functions:

    /**
     * Lift a function into the Option context.
     */
    fun <A, B> lift(f: (A) -> B): (Identity<A>) -> Identity<B> =
            {ia: Identity<A> ->
                ia.map(f)
            }   // lift



    // Applicative utility functions:

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    fun <A> pure(a: A): Identity<A> = identity(a)

    /**
     * Lift a binary function to actions.
     */
    fun <A, B, C> liftA2(f: (A) -> (B) -> C): (Identity<A>) -> (Identity<B>) -> Identity<C> =
            {ia: Identity<A> ->
                {ib: Identity<B> ->
                    ib.ap(ia.map(f))
                }
            }   // liftA2

    /**
     * Lift a ternary function to actions.
     */
    fun <A, B, C, D> liftA3(f: (A) -> (B) -> (C) -> D): (Identity<A>) -> (Identity<B>) -> (Identity<C>) -> Identity<D> =
            {ia: Identity<A> ->
                {ib: Identity<B> ->
                    {ic: Identity<C> ->
                        ic.ap(ib.ap(ia.map(f)))
                    }
                }
            }   // liftA3



    // Monad utility functions:

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    fun <A> inject(a: A): Identity<A> = identity(a)

    /**
     * Promote a function to a monad.
     */
    fun <A, B> liftM(f: (A) -> B): (Identity<A>) -> Identity<B> =
            {ia: Identity<A> ->
                ia.bind{a: A -> identity(f(a))}
            }   // liftM

    /**
     * Promote a function to a monad, scanning the monadic arguments from left to right.
     */
    fun <A, B, C> liftM2(f: (A) -> (B) -> C): (Identity<A>) -> (Identity<B>) -> Identity<C> =
            {ia: Identity<A> ->
                {ib: Identity<B> ->
                    ia.bind{a: A -> ib.bind{b: B -> identity(f(a)(b))}}
                }
            }   // liftM2

    /**
     * Promote a function to a monad, scanning the monadic arguments from left to right.
     */
    fun <A, B, C, D> liftM3(f: (A) -> (B) -> (C) -> D): (Identity<A>) -> (Identity<B>) -> (Identity<C>) -> Identity<D> =
            {ia: Identity<A> ->
                {ib: Identity<B> ->
                    {ic: Identity<C> ->
                        ia.bind{a: A -> ib.bind{b: B -> ic.bind{c: C -> identity(f(a)(b)(c))}}}
                    }
                }
            }   // liftM3

}   // IdentityF
