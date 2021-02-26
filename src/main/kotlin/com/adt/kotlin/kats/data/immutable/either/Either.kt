package com.adt.kotlin.kats.data.immutable.either

/**
 * Either[A, B] = Left of A
 *              | Right of B
 *
 * This Either type is inspired by the Haskell Either data type. The Either type represents
 *   values with two possibilities: a value of type Either[A, B] is either Left[A] or Right[B].
 *
 * The Either type is sometimes used to represent a value which is either correct or an error;
 *   by convention, the Left constructor is used to hold an error value and the Right constructor
 *   is used to hold a correct value (mnemonic: "right" also means "correct").
 *
 * This Either type is right-biased, so functions such as map and bind apply only to the Right
 *   case. This right-bias makes this Either more convenient to use in a monadic context than
 *   the either/Either type avoiding the need for a right projection.
 *
 * @param A                     the type of Left elements
 * @param B                     the type of Right elements
 *
 * @author	                    Ken Barclay
 * @since	                    October 2019
 */

import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.instances.applicative.EitherApplicative
import com.adt.kotlin.kats.data.instances.bifoldable.EitherBifoldable
import com.adt.kotlin.kats.data.instances.bifunctor.EitherBifunctor
import com.adt.kotlin.kats.data.instances.foldable.EitherFoldable
import com.adt.kotlin.kats.data.instances.functor.EitherFunctor
import com.adt.kotlin.kats.data.instances.monad.EitherMonad
import com.adt.kotlin.kats.data.instances.semigroup.EitherSemigroup
import com.adt.kotlin.kats.data.instances.traversable.EitherTraversable
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.*



typealias EitherOf<A, B> = Kind1<Kind1<EitherProxy, A>, B>

sealed class Either<out A, out B> : Kind1<Kind1<EitherProxy, A>, B> {

    class EitherProxy private constructor()           // proxy for the Option context



    class Left<out A, out B>internal constructor(val value: A) : Either<A, B>() {

        /**
         * Return a string representation of the object.
         *
         * @return                  string representation
         */
        override fun toString(): String = "Left(${value})"

    }   // Left



    class Right<out A, out B>internal constructor(val value: B) : Either<A, B>() {

        /**
         * Return a string representation of the object.
         *
         * @return                  string representation
         */
        override fun toString(): String = "Right(${value})"

    }   // Right


    /**
     * Map over Left and Right of this Either.
     *
     * Examples:
     *   left("Ken").bimap({str: String -> str.length}, {n: Int -> n * n}) == left(3)
     *   right(2).bimap({str: String -> str.length}, {n: Int -> n * n}) == right(4)
     */
    fun <C, D> bimap(f: (A) -> C, g: (B) -> D): Either<C, D> =
            this.fold({a: A -> Left(f(a))}, {b: B -> Right(g(b))})

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param other             the other object
     * @return                  true if "equal", false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val otherEither: Either<A, B> = other as Either<A, B>
            when(this) {
                is Left -> {
                    when(otherEither) {
                        is Left -> (this.value == otherEither.value)
                        is Right -> false
                    }
                }
                is Right -> {
                    when(otherEither) {
                        is Left -> false
                        is Right -> (this.value == otherEither.value)
                    }
                }
            }
        }
    }   // equals

    /**
     * Return true if this either is a Right and the predicate returns true
     *   when applied to its value. Otherwise, returns false.
     *
     * Examples:
     *   left("Ken").exists{n: Int -> isEven(n)} == false
     *   right(2).exists{n: Int -> isEven(n)} == true
     */
    fun exists(predicate: (B) -> Boolean): Boolean =
            when (this) {
                is Left -> false
                is Right -> predicate(this.value)
            }   // exists

    /**
     * Apply function fa if this is a Left or function fb if this is a Right.
     *
     * Examples:
     *   Left("Ken").fold({str -> str.length()}, {n -> 2 * n}) = 3
     *   Right(5).fold({str -> str.length()}, {n -> 2 * n}) = 10
     *
     * @param fa                the function to apply if this is a Left
     * @param fb                the function to apply if this is a Right
     * @return                  the result of applying whichever function
     */
    fun <C> fold(fa: (A) -> C, fb: (B) -> C): C =
            when(this) {
                is Left -> fa(this.value)
                is Right -> fb(this.value)
            }   // fold

    /**
     * foldLeft is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *   left("Ken").foldLeft(0){m: Int -> {n: Int -> m + n}} == 10
     *   right(2).foldLeft(0){m: Int -> {n: Int -> m + n}} == 12
     *
     * @param e                 initial value
     * @param f                 curried binary function:: C -> B -> C
     * @return                  folded result
     */
    fun <C> foldLeft(e: C, f: (C) -> (B) -> C): C =
            when (this) {
                is Left -> e
                is Right -> f(e)(this.value)
            }   // foldLeft

    fun <C> foldLeft(e: C, f: (C, B) -> C): C = this.foldLeft(e, C2(f))

    /**
     * foldRight is a higher-order function that folds a binary function into this
     *   context.
     *
     * Examples:
     *   left("Ken").foldRight(0){m: Int -> {n: Int -> m + n}} == 10
     *   right(2).foldRight(0){m: Int -> {n: Int -> m + n}} == 12
     *
     * @param e                 initial value
     * @param f                 curried binary function:: B -> C -> C
     * @return                  folded result
     */
    fun <C> foldRight(e: C, f: (B) -> (C) -> C): C =
            when (this) {
                is Left -> e
                is Right -> f(this.value)(e)
            }   // foldRight

    fun <C> foldRight(e: C, f: (B, C) -> C): C = this.foldRight(e, C2(f))

    /**
     * Deliver the default value on a Left and the result of applying the function
     *   to a Right.
     */
    fun <C> getOrElse(defaultValue: C, f: (B) -> C): C {
        return when (this) {
            is Left -> defaultValue
            is Right -> f(this.value)
        }
    }

    /**
     * Return true if this is a Left.
     */
    fun isLeft(): Boolean =
            when (this) {
                is Left -> true
                is Right -> false
            }

    /**
     * Return true if this is a Right.
     */
    fun isRight(): Boolean =
            when (this) {
                is Left -> false
                is Right -> true
            }

    /**
     * Map on the right of this disjunction.
     *
     * Examples:
     *   Left("Ken").map{n -> 2 * n} = Left("Ken")
     *   Right(5).map{n -> 2 * n} = Right(10)
     *
     * @param f                 the function to apply if this is a Right
     * @return                  this if a left; otherwise a Right wrapping the function application
     */
    fun <C> map(f: (B) -> C): Either<A, C> =
            when(this) {
                is Left -> Left(this.value)
                is Right -> Right(f(this.value))
            }   // map

    fun <C> fmap(f: (B) -> C): Either<A, C> = this.map(f)

    /**
     * Apply the given function on the Left value.
     */
    fun <C> mapLeft(f: (A) -> C): Either<C, B> =
            when (this) {
                is Left -> Left(f(this.value))
                is Right -> Right(this.value)
            }   // mapLeft

    /**
     * Flip the left/right values in this disjunction.
     */
    fun swap(): Either<B, A> =
            when (this) {
                is Left -> Right(this.value)
                is Right -> Left(this.value)
            }   // swap

    /**
     * Return a Some containing the Right value if it exists or a None if this is a Left.
     */
    fun toOption(): Option<B> =
            when (this) {
                is Left -> None
                is Right -> Some(this.value)
            }   // toOption

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    fun <G, C> traverse(ag: Applicative<G>, f: (B) -> Kind1<G, C>): Kind1<G, Either<A, C>> {
        val self: Either<A, B> = this
        return when (self) {
            is Left -> ag.run{ pure(Left(self.value)) }
            is Right -> ag.run{ ap(f(self.value), pure{c: C -> EitherF.right(c) }) }
        }
    }   // traverse



    companion object {

        /**
         * Create an instance of this semigroup.
         */
        fun <A, B> semigroup(): Semigroup<Either<A, B>> = object: EitherSemigroup<A, B> {}

        /**
         * Create an instance of this functor.
         */
        fun <A> functor(): Functor<Kind1<EitherProxy, A>> = object: EitherFunctor<A> {}

        /**
         * Create an instance of this applicative.
         */
        fun <A> applicative(): Applicative<Kind1<EitherProxy, A>> = object: EitherApplicative<A> {}

        /**
         * Create an instance of this monad.
         */
        fun <A> monad(): Monad<Kind1<EitherProxy, A>> = object: EitherMonad<A> {}

        /**
         * Create an instance of this foldable.
         */
        fun <A> foldable(): Foldable<Kind1<EitherProxy, A>> = object: EitherFoldable<A> {}

        /**
         * Create an instance of this traversable.
         */
        fun <A> traversable(): Traversable<Kind1<EitherProxy, A>> = object: EitherTraversable<A> {}

        /**
         * Create an instance of this bifunctor.
         */
        fun bifunctor(): Bifunctor<EitherProxy> = object: EitherBifunctor {}

        /**
         * Create an instance of this bifoldable.
         */
        fun bifoldable(): Bifoldable<EitherProxy> = object: EitherBifoldable {}

        /**
         * Entry point for monad bindings which enables for comprehension.
         */
        fun <A, B> forC(block: suspend MonadSyntax<Kind1<EitherProxy, A>>.() -> B): Either<A, B> =
                monad<A>().forC.monad(block).narrow()

        /*****
        /**
         * Create an instance of this bifunctor.
         */
        fun bifunctor(): Bifunctor<EitherProxy> = object: EitherBifunctor {}

        /**
         * Create an instance of this bifoldable.
         */
        fun bifoldable(): Bifoldable<EitherProxy> = object: EitherBifoldable {}

        /**
         * Create an instance of this bitraversable.
         */
        fun bitraversable(): Bitraversable<EitherProxy> = object: EitherBitraversable {}
        *****/

    }

}   // Either
