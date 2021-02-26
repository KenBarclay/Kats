package com.adt.kotlin.kats.data.instances.traversable

//import com.adt.kotlin.kats.data.immutable.list.ListF.cons
//import com.adt.kotlin.kats.data.immutable.list.ListF.nil
import com.adt.kotlin.kats.data.immutable.stream.Stream
import com.adt.kotlin.kats.data.immutable.stream.Stream.StreamProxy
import com.adt.kotlin.kats.data.immutable.stream.StreamOf
import com.adt.kotlin.kats.data.immutable.stream.narrow
import com.adt.kotlin.kats.data.instances.foldable.StreamFoldable
import com.adt.kotlin.kats.data.instances.functor.StreamFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface StreamTraversable : Traversable<StreamProxy>, StreamFoldable, StreamFunctor {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     *
     * Examples:
     *   [].traverse(listApplicative()){n -> [n, n + 1]} = [[]]
     *   [0, 1, 2, 3].traverse(listApplicative()){n -> [n, n + 1]} = [
     *       [0, 1, 2, 3], [0, 1, 2, 4], [0, 1, 3, 3], [0, 1, 3, 4],
     *       [0, 2, 2, 3], [0, 2, 2, 4], [0, 2, 3, 3], [0, 2, 3, 4],
     *       [1, 1, 2, 3], [1, 1, 2, 4], [1, 1, 3, 3], [1, 1, 3, 4],
     *       [1, 2, 2, 3], [1, 2, 2, 4], [1, 2, 3, 3], [1, 2, 3, 4]
     *   ]
     *
     *   [].traverse(optionApplicative()){n -> some(n % 2 == 0} = some([])
     *   [0, 1, 2, 3].traverse(optionApplicative()){n -> some(n % 2 == 0} = some([true, false, true, false])
     */
    override fun <G, A, B> traverse(v: StreamOf<A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Stream<B>> {
        val vStream: Stream<A> = v.narrow()
        return vStream.traverse(ag, f)
    }

    /**
     * Evaluate each action in the structure from left to right, and and collect the results.
     *
     * Examples:
     *   [[0, 1], [2, 3, 4]].sequenceA(listApplicative()) = [[0, 2], [0, 3], [0, 4], [1, 2], [1, 3], [1, 4]]
     *   [[]].sequenceA(listApplicative()) = []
     */
    override fun <G, A> sequenceA(v: StreamOf<Kind1<G, A>>, ag: Applicative<G>): Kind1<G, Stream<A>> =
            traverse(v, ag){kga: Kind1<G, A> -> kga}

    /**
     * Map each element of a structure to a monadic action, evaluate these actions from left to right,
     *   and collect the results
     *
     * Examples:
     *   [0, 1, 2, 3].mapM(listMonad()){n -> [n % 2 == 0]} = [[true, false, true, false]]
     *   [].mapM(listMonad()){n -> [n % 2 == 0]} = [[]]
     *   [0, 1, 2, 3].mapM(optionMonad()){n -> [n % 2 == 0]} = some([true, false, true, false])
     *   [].mapM(optionMonad()){n -> [n % 2 == 0]} = some([])
     */
    override fun <M, A, B> mapM(v: StreamOf<A>, md: Monad<M>, f: (A) -> Kind1<M, B>): Kind1<M, Stream<B>> =
            traverse(v, md, f)

    /**
     * Evaluate each monadic action in the structure from left to right, and collect the results.
     *
     * Examples:
     *   [[0, 1], [2, 3, 4]].sequence(listMonad()) = [[0, 2], [0, 3], [0, 4], [1, 2], [1, 3], [1, 4]]
     *   [[]].sequence(listMonad()) = []
     */
    override fun <M, A> sequence(v: StreamOf<Kind1<M, A>>, md: Monad<M>): Kind1<M, Stream<A>> =
            sequenceA(v, md)

}   // StreamTraversable
