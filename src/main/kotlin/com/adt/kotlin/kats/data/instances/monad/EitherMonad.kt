package com.adt.kotlin.kats.data.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.either.Either.Left
import com.adt.kotlin.kats.data.immutable.either.Either.Right
import com.adt.kotlin.kats.data.immutable.either.EitherOf
import com.adt.kotlin.kats.data.immutable.either.bind
import com.adt.kotlin.kats.data.immutable.either.narrow

import com.adt.kotlin.kats.data.instances.applicative.EitherApplicative

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.hkfp.typeclass.MonadForC
import com.adt.kotlin.kats.hkfp.typeclass.MonadSyntax


interface EitherMonad<A> : Monad<Kind1<EitherProxy, A>>, EitherApplicative<A> {

    /**
     * Inject a value into the monadic type.
     */
    override fun <B> inject(a: B): Either<A, B> = Right(a)

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <B, C> bind(v: EitherOf<A, B>, f: (B) -> EitherOf<A, C>): Either<A, C> {
        val vEither: Either<A, B> = v.narrow()
        val g: (B) -> Either<A, C> = {b: B -> f(b).narrow()}
        return vEither.bind(g)
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun <B, C> tailRecM(b: B, f: (B) -> EitherOf<A, Either<B, C>>): Either<A, C> {
        tailrec
        fun recTailRecM(b: B, f: (B) -> Either<A, Either<B, C>>): Either<A, C> {
            val fb: Either<A, Either<B, C>> = f(b)
            return when (fb) {
                is Left -> Left(fb.value)
                is Right -> {
                    val rightE: Either<B, C> = fb.value
                    when (rightE) {
                        is Left -> recTailRecM(rightE.value, f)
                        is Right -> Right(rightE.value)
                    }
                }
            }
        }   // recTailRecM

        val g: (B) -> Either<A, Either<B, C>> = {bb: B -> f(bb).narrow()}
        return recTailRecM(b, g)
    }   // tailRecM



    /**
     * Entry point for monad bindings which enables for comprehension.
     */
    @Suppress("UNCHECKED_CAST")
    override val forC: MonadForC<Kind1<EitherProxy, A>>
        get() = EitherForCMonad as MonadForC<Kind1<EitherProxy, A>>


}   // EitherMonad



internal object EitherForCMonad : MonadForC<Kind1<EitherProxy, Any>> {

    override val mf: Monad<Kind1<EitherProxy, Any>> = Either.monad()
    override fun <A> monad(block: suspend MonadSyntax<Kind1<EitherProxy, Any>>.() -> A): Either<Any, A> =
            super.monad(block).narrow()

}   // EitherForCMonad
