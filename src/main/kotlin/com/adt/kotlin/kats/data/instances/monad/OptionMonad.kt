package com.adt.kotlin.kats.data.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.instances.applicative.OptionApplicative
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.Option.OptionProxy
import com.adt.kotlin.kats.data.immutable.option.OptionF
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some
import com.adt.kotlin.kats.data.immutable.option.OptionOf
import com.adt.kotlin.kats.data.immutable.option.narrow

import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadForC
import com.adt.kotlin.kats.hkfp.typeclass.MonadSyntax


interface OptionMonad : Monad<OptionProxy>, OptionApplicative {

    /**
     * Inject a value into the monadic type.
     *
     * Examples:
     *   let monad = Option.monad()
     *
     *   monad.run{ inject(3) } == some(3)
     */
    override fun <A> inject(a: A): Option<A> = OptionF.some(a)

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     *
     * Examples:
     *   let monad = Option.monad()
     *
     *   monad.run{ bind(none()){n -> some(1 + n)} } == none()
     *   monad.run{ bind(some(5)){n -> some(1 + n)} } == some(6)
     */
    override fun <A, B> bind(v: OptionOf<A>, f: (A) -> OptionOf<B>): Option<B> {
        val vOption: Option<A> = v.narrow()
        val g: (A) -> Option<B> = {a: A -> f(a).narrow()}
        return vOption.bind(g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> OptionOf<Either<B, C>>): Option<C> {
        tailrec
        fun recTailRecM(b: B, f: (B) -> Option<Either<B, C>>): Option<C> {
            val op: Option<Either<B, C>> = f(b)
            return when (op) {
                is None -> none()
                is Some -> {
                    val either: Either<B, C> = op.value
                    when (either) {
                        is Left -> recTailRecM(either.value, f)
                        is Right -> some(either.value)
                    }
                }
            }
        }   // recTailRecM

        val g: (B) -> Option<Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(b, g)
    }   // tailRecM



    /**
     * Entry point for monad bindings which enables for comprehension.
     */
    override val forC: MonadForC<OptionProxy>
        get() = OptionForCMonad

}   // OptionMonad



internal object OptionForCMonad : MonadForC<OptionProxy> {

    override val mf: Monad<OptionProxy> = Option.monad()
    override fun <A> monad(block: suspend MonadSyntax<OptionProxy>.() -> A): Option<A> =
            super.monad(block).narrow()

}   // OptionForCMonad

