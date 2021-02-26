package com.adt.kotlin.kats.hkfp.typeclass

import com.adt.kotlin.kats.hkfp.kind.Kind1

interface Arrow<F> {

    fun <A, B> arr(f: (A) -> B): Kind1<Kind1<F, A>, B>

        // >>>
    fun <A, B, C> compose(f: Kind1<Kind1<F, A>, B>, arrBC: Kind1<Kind1<F, B>, C>): Kind1<Kind1<F, A>, C>

    fun <A, B, C> first(f: Kind1<Kind1<F, A>, B>): Kind1<Kind1<F, Pair<A, C>>, Pair<B, C>>

    fun <A, B, C> second(f: Kind1<Kind1<F, A>, B>): Kind1<Kind1<F, Pair<C, A>>, Pair<C, B>>

        // ***
    fun <A, B, C, D> split(f: Kind1<Kind1<F, A>, B>, g: Kind1<Kind1<F, C>, D>): Kind1<Kind1<F, Pair<A, C>>, Pair<B, D>> =
            compose(first(f), second(g))

        // &&&
    fun <A, B, C> fanout(f: Kind1<Kind1<F, A>, B>, g: Kind1<Kind1<F, A>, C>): Kind1<Kind1<F, A>, Pair<B, C>> =
            compose(split(), split(f, g))

    fun <A> split(): Kind1<Kind1<F, A>, Pair<A, A>> =
            arr{a: A -> Pair(a, a)}

}   // Arrow
