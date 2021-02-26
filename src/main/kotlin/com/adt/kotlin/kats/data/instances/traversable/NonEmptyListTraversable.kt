package com.adt.kotlin.kats.data.instances.traversable

import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList.NonEmptyListProxy
import com.adt.kotlin.kats.data.immutable.nel.narrow
import com.adt.kotlin.kats.data.instances.foldable.NonEmptyListFoldable
import com.adt.kotlin.kats.data.instances.functor.NonEmptyListFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface NonEmptyListTraversable : Traversable<NonEmptyListProxy>, NonEmptyListFoldable, NonEmptyListFunctor {

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     *
     * Examples:
     *   [0 :| 1, 2, 3].traverse(nonEmptyListApplicative()){n -> [n, n + 1]} = [
     *       [0 :| 1, 2, 3] :| [0 :| 1, 2, 4], [0 :| 1, 3, 3], [0 :| 1, 3, 4],
     *       [0 :| 2, 2, 3], [0 :| 2, 2, 4], [0 :| 2, 3, 3], [0 :| 2, 3, 4],
     *       [1 :| 1, 2, 3], [1 :| 1, 2, 4], [1 :| 1, 3, 3], [1 :| 1, 3, 4],
     *       [1 :| 2, 2, 3], [1 :| 2, 2, 4], [1 :| 2, 3, 3], [1 :| 2, 3, 4]
     *   ]
     *
     *   [0 :| 1, 2, 3].traverse(optionApplicative()){n -> some(n % 2 == 0} = some([true :| false, true, false])
     */
    override fun <G, A, B> traverse(v: Kind1<NonEmptyListProxy, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, NonEmptyList<B>> {
        val vNonEmptyList: NonEmptyList<A> = v.narrow()
        return vNonEmptyList.traverse(ag, f)
    }

    /**
     * Evaluate each action in the structure from left to right, and and collect the results.
     *
     * Examples:
     *   [[0 :| 1] :| [2 :| 3, 4]].sequenceA(listApplicative()) = [[0 :| 2] :| [0 :| 3], [0 :| 4], [1 :| 2], [1 :| 3], [1 :| 4]]
     */
    override fun <G, A> sequenceA(v: Kind1<NonEmptyListProxy, Kind1<G, A>>, ag: Applicative<G>): Kind1<G, NonEmptyList<A>> =
            traverse(v, ag){kga: Kind1<G, A> -> kga}

    /**
     * Map each element of a structure to a monadic action, evaluate these actions from left to right,
     *   and collect the results
     *
     * Examples:
     *   [0 :| 1, 2, 3].mapM(nonEmptyListMonad()){n -> [n % 2 == 0]} = [[true :| false, true, false]]
     *   [0 :| 1, 2, 3].mapM(optionMonad()){n -> [n % 2 == 0]} = some([true :| false, true, false])
     */
    override fun <M, A, B> mapM(v: Kind1<NonEmptyListProxy, A>, md: Monad<M>, f: (A) -> Kind1<M, B>): Kind1<M, NonEmptyList<B>> =
            traverse(v, md, f)

    /**
     * Evaluate each monadic action in the structure from left to right, and collect the results.
     *
     * Examples:
     *   [[0 :| 1] :| [2 :| 3, 4]].sequence(nonEmptyListMonad()) = [[0 :| 2] :| [0 :| 3], [0 :| 4], [1 :| 2], [1 :| 3], [1 :| 4]]
     */
    override fun <M, A> sequence(v: Kind1<NonEmptyListProxy, Kind1<M, A>>, md: Monad<M>): Kind1<M, NonEmptyList<A>> =
            sequenceA(v, md)

}   // NonEmptyListTraversable
