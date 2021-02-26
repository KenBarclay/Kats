package com.adt.kotlin.kats.hkfp.laws

/**
 * The identity law says that applying the pure id morphism does nothing,
 *   exactly like with the plain id function. The homomorphism law says
 *   that applying a pure function to a pure value is the same as applying
 *   the function to the value in the normal way and then using pure on the
 *   result. In a sense, that means pure preserves function application. The
 *   interchange law says that applying a morphism to a pure value pure y is
 *   the same as applying pure ($ y) to the morphism. The composition law
 *   says that pure (.) composes morphisms similarly to how (.) composes
 *   functions: applying the composed morphism pure (.) <*> u <*> v to w gives
 *   the same result as applying u to the result of applying v to w.
 *
 * @author	                    Ken Barclay
 * @since                       January 2019
 */

import com.adt.kotlin.kats.hkfp.fp.FunctionF.compose

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative



class ApplicativeLaws<F>(val applicative: Applicative<F>) {

    /**
     * ap(v, pure(id)) must always equal v, where id is the identity
     *   function x => x and v is an applicative functor. This is called
     *   the identity law. Here, v is a value already in a context. The
     *   identity law tells us that applying the identity function preserves
     *   values. This law also shows that the context created by pure does
     *   not change the context of v when they are combined using ap.
     */
    fun <A> identityLaw(fa: Kind1<F, A>): Boolean {
        val id: (A) -> A = {a: A -> a}
        return applicative.run{ ap(fa, pure(id)) == fa }
    }

    /**
     * ap(pure(x), pure(f)) must always equal pure(f(x)). This is called the
     *   homomorphism law. This law means that it does not matter if we apply
     *   functions to values before or after putting them into a pure context;
     *   we get the same result either way.
     */
    fun <A, B> homomorphismLaw(a: A, f: (A) -> B): Boolean {
        return applicative.run{ ap(pure(a), pure(f)) == pure(f(a)) }
    }

    /**
     * ap(u, pure(x)) must always equal ap(u, pure(f => f(x))). This is known
     *   as the interchange law. Here u is an applicative which holds some
     *   function A => B, and x is something of type A. f => f(x) is just
     *   function application to some x, so the interchange law shows us that
     *   function application works the same whether we use ap to apply a
     *   function to an applicative value or pure to lift normal function
     *   application into a context.
     */
    fun <A, B> interchangeLaw(a: A, fab: Kind1<F, (A) -> B>): Boolean {
        return applicative.run{ ap(pure(a), fab) ==  ap(fab, pure{g: (A) -> B -> g(a)})}
    }

    /**
     * Then ap(u, ap(v, ap(w, pure(compose)))) must be equal to ap(ap(u, v), w).
     *   This is called the composition law, and it means that using ap to
     *   compose functions contained within applicatives is the same as using
     *   normal function composition inside of an applicative.
     */
    fun <A, B, C> compositionLaw(fa: Kind1<F, A>, fbc: Kind1<F, (B) -> C>, fab: Kind1<F, (A) -> B>): Boolean {
        return applicative.run{ ap(fa, ap(fab, ap(fbc, pure{bc: (B) -> C -> {ab: (A) -> B -> compose(bc, ab)}}))) == ap(ap(fa, fab), fbc) }
    }

}   // ApplicativeLaws
