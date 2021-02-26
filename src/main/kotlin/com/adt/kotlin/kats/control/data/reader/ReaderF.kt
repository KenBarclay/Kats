package com.adt.kotlin.kats.control.data.reader

import com.adt.kotlin.kats.control.data.readert.ReaderT
import com.adt.kotlin.kats.data.immutable.identity.Identity
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Monad


object ReaderF {

    fun <A, B> reader(run: (A) -> B): Reader<A, B> = Reader(run)
    fun <A, B> reader(b: B): Reader<A, B> = reader{_: A -> b}



    /**
     * Create an instance of this functor.
     */
    fun <A> functor(): Functor<Kind1<Kind1<ReaderT.ReaderTProxy, Identity.IdentityProxy>, A>> =
            ReaderT.functor(Identity.functor())

    /**
     * Create an instance of this applicative.
     */
    fun <A> applicative(): Applicative<Kind1<Kind1<ReaderT.ReaderTProxy, Identity.IdentityProxy>, A>> =
            ReaderT.applicative(Identity.applicative())

    /**
     * Create an instance of this monad.
     */
    fun <A> monad(): Monad<Kind1<Kind1<ReaderT.ReaderTProxy, Identity.IdentityProxy>, A>> =
            ReaderT.monad(Identity.monad())



    // Functor utility functions:

    /**
     * Lift a function into the Reader context.
     */
    fun <A, B, C> lift(f: (B) -> C): (Reader<A, B>) -> Reader<A, C> =
            {rab: Reader<A, B> ->
                rab.map(f)
            }   // lift



    // Applicative utility functions:

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    fun <A, B> pure(b: B): Reader<A, B> =reader(b)

    /**
     * Lift a binary function to actions.
     */
    fun <A, B, C, D> liftA2(f: (B) -> (C) -> D): (Reader<A, B>) -> (Reader<A, C>) -> Reader<A, D> =
            {rab: Reader<A, B> ->
                {rac: Reader<A, C> ->
                    rac.ap(rab.map(f))
                }
            }   // liftA2

    /**
     * Lift a ternary function to actions.
     */
    fun <A, B, C, D, E> liftA3(f: (B) -> (C) -> (D) -> E): (Reader<A, B>) -> (Reader<A, C>) -> (Reader<A, D>) -> Reader<A, E> =
            {rab: Reader<A, B> ->
                {rac: Reader<A, C> ->
                    {rad: Reader<A, D> ->
                        rad.ap(rac.ap(rab.map(f)))
                    }
                }
            }   // liftA3



    // Monad utility functions:

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    fun <A, B> inject(b: B): Reader<A, B> = reader(b)

    /**
     * Promote a function to a monad.
     */
    fun <A, B, C> liftM(f: (B) -> C): (Reader<A, B>) -> Reader<A, C> =
            {rab: Reader<A, B> ->
                rab.bind{b: B -> reader<A, C>(f(b))}
            }   // liftM

    /**
     * Promote a function to a monad, scanning the monadic arguments from left to right.
     */
    fun <A, B, C, D> liftM2(f: (B) -> (C) -> D): (Reader<A, B>) -> (Reader<A, C>) -> Reader<A, D> =
            {rab: Reader<A, B> ->
                {rac: Reader<A, C> ->
                    rab.bind{b: B -> rac.bind{c: C -> reader<A, D>(f(b)(c))}}
                }
            }   // liftM2

    /**
     * Promote a function to a monad, scanning the monadic arguments from left to right.
     */
    fun <A, B, C, D, E> liftM3(f: (B) -> (C) -> (D) -> E): (Reader<A, B>) -> (Reader<A, C>) -> (Reader<A, D>) -> Reader<A, E> =
            {rab: Reader<A, B> ->
                {rac: Reader<A, C> ->
                    {rad: Reader<A, D> ->
                        rab.bind{b: B -> rac.bind{c: C -> rad.bind{d: D -> reader<A, E>(f(b)(c)(d)) }}}
                    }
                }
            }   // liftM3

}   // ReaderF
