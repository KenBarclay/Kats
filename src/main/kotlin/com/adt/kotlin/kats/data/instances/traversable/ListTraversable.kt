package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.ListProxy
import com.adt.kotlin.kats.data.immutable.list.ListOf
import com.adt.kotlin.kats.data.immutable.list.narrow
import com.adt.kotlin.kats.data.instances.foldable.ListFoldable
import com.adt.kotlin.kats.data.instances.functor.ListFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface ListTraversable : Traversable<ListProxy>, ListFoldable, ListFunctor {

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
    override fun <G, A, B> traverse(v: ListOf<A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, List<B>> {
        val vList: List<A> = v.narrow()
        return vList.traverse(ag, f)
    }   // traverse

    /**
     * Evaluate each action in the structure from left to right, and and collect the results.
     *
     * Examples:
     *   [[0, 1], [2, 3, 4]].sequenceA(listApplicative()) = [[0, 2], [0, 3], [0, 4], [1, 2], [1, 3], [1, 4]]
     *   [[]].sequenceA(listApplicative()) = []
     */
    override fun <G, A> sequenceA(v: ListOf<Kind1<G, A>>, ag: Applicative<G>): Kind1<G, List<A>> =
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
    override fun <M, A, B> mapM(v: ListOf<A>, md: Monad<M>, f: (A) -> Kind1<M, B>): Kind1<M, List<B>> =
            traverse(v, md, f)

    /**
     * Evaluate each monadic action in the structure from left to right, and collect the results.
     *
     * Examples:
     *   [[0, 1], [2, 3, 4]].sequence(listMonad()) = [[0, 2], [0, 3], [0, 4], [1, 2], [1, 3], [1, 4]]
     *   [[]].sequence(listMonad()) = []
     */
    override fun <M, A> sequence(v: ListOf<Kind1<M, A>>, md: Monad<M>): Kind1<M, List<A>> =
            sequenceA(v, md)

}   // ListTraversable
