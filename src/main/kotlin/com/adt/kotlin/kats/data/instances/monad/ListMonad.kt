package com.adt.kotlin.kats.data.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.list.*
import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.ListProxy
import com.adt.kotlin.kats.data.immutable.list.ListF.singleton

import com.adt.kotlin.kats.data.instances.applicative.ListApplicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadForC
import com.adt.kotlin.kats.hkfp.typeclass.MonadSyntax


/**
 * Monad over a List.
 */
interface ListMonad : Monad<ListProxy>, ListApplicative {

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): List<A> = singleton(a)

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     *
     * Examples:
     *   let monad = List.monad()
     *
     *   monad.bind([]){ n -> [n, -n]} == []
     *   monad.bind([1, 2, 3, 4]){n ->[n, -n]} == [1, -1, 2, -2, 3, -3, 4, -4]
     */
    override fun <A, B> bind(v: ListOf<A>, f: (A) -> ListOf<B>): List<B> {
        val vList: List<A> = v.narrow()
        val g: (A) -> List<B> = {a: A -> f(a).narrow()}
        return vList.bind(g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> ListOf<Either<B, C>>): List<C> {
        tailrec
        fun recTailRecM(v: List<Either<B, C>>, f: (B) -> List<Either<B, C>>, acc: ListBufferIF<C>): List<C> {
            return if (v.isEmpty())
                acc.toList()
            else {
                val hd: Either<B, C> = v.head()
                when (hd) {
                    is Left -> recTailRecM(f(hd.value).append(v.drop(1)), f, acc)
                    is Right -> recTailRecM(v.drop(1), f, acc.append(hd.value))
                }
            }
        }   // recTailRecM

        val g: (B) -> List<Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(f(b).narrow(), g, ListBuffer())
    }   // tailRecM



    /**
     * Entry point for monad bindings which enables for comprehension.
     */
    override val forC: MonadForC<ListProxy>
        get() = ListForCMonad

}   // ListMonad



internal object ListForCMonad : MonadForC<ListProxy> {

    override val mf: Monad<ListProxy> = List.monad()
    override fun <A> monad(block: suspend MonadSyntax<ListProxy>.() -> A): List<A> =
            super.monad(block).narrow()

}   // ListForCMonad
