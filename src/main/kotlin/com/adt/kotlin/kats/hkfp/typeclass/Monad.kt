package com.adt.kotlin.kats.hkfp.typeclass

/**
 * A monad is used to sequentially compose two actions. A monad is also an
 *   Applicative. Monads are a natural extension of applicative functors and
 *   with them we are concerned with applying a function that takes a normal
 *   value of type A and returns a value within the same context to a value
 *   of type F<A>.
 *
 * The minimal complete definition is provided by bind.
 *
 * The four functor laws are:
 *   inject(a).bind(f) = f(a)
 *   m bind inject = m
 *   m bind (\x -> f(x) bind h) = (m bind f) bind h
 *
 *   fmap f xs = xs bind inject . f = liftM f x
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.List.Nil
import com.adt.kotlin.kats.data.immutable.list.List.Cons

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.EitherF.left
import com.adt.kotlin.kats.data.immutable.either.EitherF.right
import com.adt.kotlin.kats.data.immutable.list.ListF
import com.adt.kotlin.kats.hkfp.kind.Kind1

import kotlin.collections.List as KList
import kotlin.coroutines.*

import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED


/**
 * A monad is used to sequentially compose two actions.
 *
 * @param F                     representation for the context
 */
interface Monad<F> : Applicative<F> {

    // ---------- primitive operations --------------------

    /**
     * Inject a value into the monadic type.
     */
    fun <A> inject(a: A): Kind1<F, A>

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    fun <A, B> bind(v: Kind1<F, A>, f: (A) -> Kind1<F, B>): Kind1<F, B>

    fun <A, B> flatMap(v: Kind1<F, A>, f: (A) -> Kind1<F, B>): Kind1<F, B> = this.bind(v, f)



// ---------- derived operations ----------------------

    /**
     * if lifted into the monad.
     */
    fun <A> ifM(condition: Kind1<F, Boolean>, ifTrue: () -> Kind1<F, A>, ifFalse: () -> Kind1<F, A>): Kind1<F, A> =
            bind(condition){b: Boolean -> if (b) ifTrue() else ifFalse()}

    /**
     * Execute an action repeatedly as long as the given boolean expression
     *   returns true. The condition is evaluated before the loop body. Collect
     *   the results into an arbitrary alternative value. This implementation
     *   uses combine on each evaluation result, so avoid data structures with
     *   non-constant append performance, e.g. List.
     */
    fun <G, A> whileM(condition: Kind1<F, Boolean>, body: () -> Kind1<F, A>, ag: Alternative<G>): Kind1<F, Kind1<G, A>> {
        return tailRecM(ag.empty()){ga: Kind1<G, A> ->
            ifM(condition,
                    { fmap(body()){bv -> left(ag.combine(ga, ag.pure(bv)))} },
                    { inject(right(ga)) }
            )
        }
    }   // whileM

    /**
     * Execute an action repeatedly as long as the given boolean expression
     *   returns true. The condition is evaluated before the loop body. Discards
     *   the results.
     */
    fun <A> whileM_(condition: Kind1<F, Boolean>, body: () -> Kind1<F, A>): Kind1<F, Unit> {
        val cont: Either<Unit, Unit> = left(Unit)
        val stop: Kind1<F, Either<Unit, Unit>> = pure(right(Unit))
        return tailRecM(Unit){
            ifM(condition,
                    { replaceAll(body(), cont) },
                    { stop }
            )
        }
    }   // whileM_

    /**
     * Execute an action repeatedly until the boolean condition returns true.
     *   The condition is evaluated after the loop body. Collects results into an
     *   arbitrary alternative value. This implementation uses combine on each
     *   evaluation result, so avoid data structures with non-constant append
     *   performance, e.g. List.
     */
    fun <G, A> untilM(fa: Kind1<F, A>, condition: () -> Kind1<F, Boolean>, ag: Alternative<G>): Kind1<F, Kind1<G, A>> {
        return bind(fa){a: A ->
            fmap(whileM(fmap(condition()){b: Boolean -> !b}, { fa }, ag)){ga: Kind1<G, A> -> ag.combine(ag.pure(a), ga)}
        }
    }   // untilM

    /**
     * Execute an action repeatedly until the boolean condition returns true.
     *   The condition is evaluated after the loop body. Discards the results.
     */
    fun <A> untilM_(fa: Kind1<F, A>, condition: () -> Kind1<F, Boolean>): Kind1<F, Unit> {
        return bind(fa){
            whileM_(fmap(condition()){b: Boolean -> !b}, { fa })
        }
    }   // untilM_

    /**
     * Simulates an if/else-if/else in the context of an F. It evaluates
     *   conditions until one evaluates to true, and returns the associated
     *   F[A]. If no condition is true, returns els.
     */
    fun <A> ifElseM(vararg branches: Pair<Kind1<F, Boolean>, Kind1<F, A>>, els: Kind1<F, A>): Kind1<F, A> {
        fun step(branches: List<Pair<Kind1<F, Boolean>, Kind1<F, A>>>): Kind1<F, Either<List<Pair<Kind1<F, Boolean>, Kind1<F, A>>>, A>> =
                when (branches) {
                    is Nil -> fmap(els){a: A -> right(a)}
                    is Cons -> {
                        val (condition: Kind1<F, Boolean>, consequence: Kind1<F, A>) = branches.head()
                        val rest: List<Pair<Kind1<F, Boolean>, Kind1<F, A>>> = branches.tail()
                        bind(condition){b: Boolean ->
                            if (b) fmap(consequence){a: A -> right(a)} else pure(left(rest))
                        }
                    }
                }   // step

        return tailRecM(ListF.from(*branches)){list: List<Pair<Kind1<F, Boolean>, Kind1<F, A>>> -> step(list)}
    }   // ifElseM

    /**
     * Sequentially compose two actions, discarding any value produced by the first,
     *   like sequencing operators (such as the semicolon) in imperative languages.
     */
    fun <A, B> then(va: Kind1<F, A>, vb: Kind1<F, B>): Kind1<F, B> = bind(va){_ -> vb}



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    fun <A, B> tailRecM(a: A, f: (A) -> Kind1<F, Either<A, B>>): Kind1<F, B>



    /**
     * Entry point for monad bindings which enables for comprehension. The
     *   underlying implementation is based on coroutines. A coroutine is
     *   initiated and suspended inside MonadThrowContinuation yielding
     *   to Monad.flatMap. Once all the flatMap binds are completed the
     *   underlying monad is returned from the act of executing the coroutine.
     */
    val forC: MonadForC<F>
        get() = object: MonadForC<F> {
            override val mf: Monad<F> = this@Monad
        }



// ---------- utility functions ---------------------------

    /**
     * Promote a function to a monad.
     */
    fun <A, B> liftM(f: (A) -> B): (Kind1<F, A>) -> Kind1<F, B> =
            {fa: Kind1<F, A> ->
                bind(fa){a: A -> inject(f(a))}
            }   // liftM

    /**
     * Promote a function to a monad, scanning the monadic arguments from left to right.
     */
    fun <A, B, C> liftM2(f: (A) -> (B) -> C): (Kind1<F, A>) -> (Kind1<F, B>) -> Kind1<F, C> =
            {fa: Kind1<F, A> ->
                {fb: Kind1<F, B> ->
                    bind(fa){a: A -> bind(fb){b: B -> inject(f(a)(b))}}
                }
            }   // liftM2

    /**
     * Promote a function to a monad, scanning the monadic arguments from left to right.
     */
    fun <A, B, C, D> liftM3(f: (A) -> (B) -> (C) -> D): (Kind1<F, A>) -> (Kind1<F, B>) -> (Kind1<F, C>) -> Kind1<F, D> =
            {fa: Kind1<F, A> ->
                {fb: Kind1<F, B> ->
                    {fc: Kind1<F, C> ->
                        bind(fa){a: A -> bind(fb){b: B -> bind(fc){c: C -> inject(f(a)(b)(c))}}}
                    }
                }
            }   // liftM3

}   // Monad



private val coroutineImplClass by lazy { Class.forName("kotlin.coroutines.jvm.internal.BaseContinuationImpl") }

private val completionField by lazy { coroutineImplClass.getDeclaredField("completion").apply { isAccessible = true } }

private var <T> Continuation<T>.completion: Continuation<*>?
    get() = completionField.get(this) as Continuation<*>
    set(value) = completionField.set(this@completion, value)

var <T> Continuation<T>.stateStack: KList<Map<String, *>>
    get() {
        if (!coroutineImplClass.isInstance(this)) return emptyList()
        val resultForThis = (this.javaClass.declaredFields)
                .associate { it.isAccessible = true; it.name to it.get(this@stateStack) }
                .let(::listOf)
        val resultForCompletion = completion?.stateStack
        return resultForCompletion?.let { resultForThis + it } ?: resultForThis
    }
    set(value) {
        if (!coroutineImplClass.isInstance(this)) return
        val mapForThis = value.first()
        (this.javaClass.declaredFields).forEach {
            if (it.name in mapForThis) {
                it.isAccessible = true
                val fieldValue = mapForThis[it.name]
                it.set(this@stateStack, fieldValue)
            }
        }
        completion?.stateStack = value.subList(1, value.size)
    }

interface Invoke<F> {
    suspend operator fun <A> Kind1<F, A>.invoke(): A
}

interface MonadSyntax<F> : Monad<F>, Invoke<F>

open class MonadContinuation<F, A>(
        val mf: Monad<F>,
        override val context: CoroutineContext = EmptyCoroutineContext
) : Kind1<F, A>, Continuation<Kind1<F, A>>, Monad<F> by mf, MonadSyntax<F>, Invoke<F> {

    fun resume(value: Kind1<F, A>) {
        returnedMonad = value
    }

    override fun resumeWith(result: Result<Kind1<F, A>>) {
        result.fold(::resume, ::resumeWithException)
    }

    fun resumeWithException(exception: Throwable) {
        throw exception
    }

    open fun returnedMonad(): Kind1<F, A> = returnedMonad

    override suspend operator fun <A> Kind1<F, A>.invoke(): A {
        return suspendCoroutineUninterceptedOrReturn {continuation ->
            val label = continuation.stateStack
            returnedMonad = bind(this){a: A ->
                continuation.stateStack = label
                continuation.resume(a)
                returnedMonad
            }
            COROUTINE_SUSPENDED
        }
    }

// ---------- properties ---------------------------------

    protected lateinit var returnedMonad: Kind1<F, A>

}   // MonadContinuation

interface MonadForC<F> {
    val mf: Monad<F>

    fun <A> monad(block: suspend MonadSyntax<F>.() -> A): Kind1<F, A> {
        val continuation = MonadContinuation<F, A>(mf)
        val wrapReturn: suspend MonadContinuation<F, *>.() -> Kind1<F, A> = { pure(block()) }
        wrapReturn.startCoroutine(continuation, continuation)
        return continuation.returnedMonad()
    }   // monad

}   // MonadForC
