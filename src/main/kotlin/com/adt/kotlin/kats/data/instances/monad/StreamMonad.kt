package com.adt.kotlin.kats.data.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.list.ListBuffer
import com.adt.kotlin.kats.data.immutable.list.ListBufferIF

import com.adt.kotlin.kats.data.immutable.stream.Stream
import com.adt.kotlin.kats.data.immutable.stream.Stream.StreamProxy
import com.adt.kotlin.kats.data.immutable.stream.StreamF
import com.adt.kotlin.kats.data.immutable.stream.StreamF.singleton
import com.adt.kotlin.kats.data.immutable.stream.StreamOf
import com.adt.kotlin.kats.data.immutable.stream.narrow

import com.adt.kotlin.kats.data.instances.applicative.StreamApplicative
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadForC
import com.adt.kotlin.kats.hkfp.typeclass.MonadSyntax


/**
 * Monad over a Stream.
 */
interface StreamMonad : Monad<StreamProxy>, StreamApplicative {

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): Stream<A> = singleton(a)

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
    override fun <A, B> bind(v: StreamOf<A>, f: (A) -> StreamOf<B>): Stream<B> {
        val vStream: Stream<A> = v.narrow()
        val g: (A) -> Stream<B> = {a: A -> f(a).narrow()}
        return vStream.bind(g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> StreamOf<Either<B, C>>): Stream<C> {
        tailrec
        fun recTailRecM(v: Stream<Either<B, C>>, f: (B) -> Stream<Either<B, C>>, acc: ListBufferIF<C>): Stream<C> {
            return if (v.toList().isEmpty())
                StreamF.from(acc.toList())
            else {
                val hd: Either<B, C> = v.head()
                when (hd) {
                    is Left -> {
                        if (v.size() == 1)
                            recTailRecM(f(hd.value), f, acc)
                        else
                            recTailRecM(f(hd.value).append(v.drop(1)), f, acc)
                    }
                    is Right -> recTailRecM(v.drop(1), f, acc.append(hd.value))
                }
            }
        }   // recTailRecM

        val g: (B) -> Stream<Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(f(b).narrow(), g, ListBuffer())
    }   // tailRecM



    /**
     * Entry point for monad bindings which enables for comprehension.
     */
    override val forC: MonadForC<StreamProxy>
        get() = StreamForCMonad

}   // StreamMonad



internal object StreamForCMonad : MonadForC<StreamProxy> {

    override val mf: Monad<StreamProxy> = Stream.monad()
    override fun <A> monad(block: suspend MonadSyntax<StreamProxy>.() -> A): Stream<A> =
            super.monad(block).narrow()

}   // StreamForCMonad
