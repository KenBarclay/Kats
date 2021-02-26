package com.adt.kotlin.kats.data.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.ListBuffer
import com.adt.kotlin.kats.data.immutable.list.ListBufferIF
import com.adt.kotlin.kats.data.immutable.nel.*
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyList.NonEmptyListProxy
import com.adt.kotlin.kats.data.immutable.nel.NonEmptyListF.singleton
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some
import com.adt.kotlin.kats.data.instances.applicative.NonEmptyListApplicative

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadForC
import com.adt.kotlin.kats.hkfp.typeclass.MonadSyntax


interface NonEmptyListMonad : Monad<NonEmptyListProxy>, NonEmptyListApplicative {

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): NonEmptyList<A> = singleton(a)

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     *
     * Examples:
     *   [0 :| 1, 2, 3].bind{n -> [n, n + 1]} = [0 :| 1, 1, 2, 2, 3, 3, 4]
     */
    override fun <A, B> bind(v: Kind1<NonEmptyListProxy, A>, f: (A) -> Kind1<NonEmptyListProxy, B>): NonEmptyList<B> {
        val vNel: NonEmptyList<A> = v.narrow()
        val g: (A) -> NonEmptyList<B> = {a: A -> f(a).narrow()}
        return vNel.bind(g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> NonEmptyListOf<Either<B, C>>): NonEmptyList<C> {
        fun <T> fromList(list: List<T>): Option<NonEmptyList<T>> =
                if (list.isEmpty()) none() else some(NonEmptyListF.nonEmptyList(list.head(), list.tail()))

        tailrec
        fun recTailRecM(v: NonEmptyList<Either<B, C>>, f: (B) -> NonEmptyList<Either<B, C>>, acc: ListBufferIF<C>): NonEmptyList<C> {
            val hd: Either<B, C> = v.head()
            return when (hd) {
                is Left -> recTailRecM(f(hd.value).narrow().append(v.tail()), f, acc)
                is Right -> {
                    //acc.append(hd.value)
                    val op: Option<NonEmptyList<Either<B, C>>> = fromList(v.tail())
                    when (op) {
                        is None -> {
                            val list: List<C> = acc.append(hd.value).toList()
                            NonEmptyListF.nonEmptyList(list.head(), list.tail())
                        }
                        is Some -> recTailRecM(op.value, f, acc.append(hd.value))
                    }
                }
            }
        }   // recTailRecM

        val g: (B) -> NonEmptyList<Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(f(b).narrow(), g, ListBuffer())
    }   // tailRecM



    /**
     * Entry point for monad bindings which enables for comprehension.
     */
    override val forC: MonadForC<NonEmptyListProxy>
        get() = NonEmptyListForCMonad

}   // NonEmptyListMonad



internal object NonEmptyListForCMonad : MonadForC<NonEmptyListProxy> {

    override val mf: Monad<NonEmptyListProxy> = NonEmptyList.monad()
    override fun <A> monad(block: suspend MonadSyntax<NonEmptyListProxy>.() -> A): NonEmptyList<A> =
            super.monad(block).narrow()

}   // NonEmptyListForCMonad
