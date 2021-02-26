package com.adt.kotlin.kats.data.instances.applicative

import com.adt.kotlin.kats.data.immutable.stream.StreamF.singleton
import com.adt.kotlin.kats.data.immutable.stream.Stream
import com.adt.kotlin.kats.data.immutable.stream.Stream.StreamProxy
import com.adt.kotlin.kats.data.immutable.stream.StreamOf
import com.adt.kotlin.kats.data.immutable.stream.narrow

import com.adt.kotlin.kats.data.instances.functor.StreamFunctor
import com.adt.kotlin.kats.hkfp.typeclass.Applicative



/**
 * Applicative over a Stream.
 */
interface StreamApplicative : Applicative<StreamProxy>, StreamFunctor {

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): Stream<A> = singleton(a)

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     *
     * Examples:
     *   let applicative = List.applicative()
     *   let functions = [{ _ -> 0 }, { n -> 100 + n }, { n -> n * n }]
     *
     *   applicative.run{ ap([], functions) } == []
     *   applicative.run{ ap([1, 2, 3, 4], functions) } == [0, 0, 0, 0, 101, 102, 103, 104, 1, 4, 9, 16]
     *   applicative.run{ ap([1, 2, 3, 4], [] } == []
     */
    override fun <A, B> ap(v: StreamOf<A>, f: StreamOf<(A) -> B>): Stream<B> {
        val vStream: Stream<A> = v.narrow()
        val fStream: Stream<(A) -> B> = f.narrow()
        return vStream.ap(fStream)
    }   // ap

}   // StreamApplicative
