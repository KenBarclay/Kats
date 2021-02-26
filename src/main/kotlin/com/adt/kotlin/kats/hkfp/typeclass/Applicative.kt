package com.adt.kotlin.kats.hkfp.typeclass

/**
 * Applicative functors is an abstract characterisation of an applicative style of
 *   effectful programming. An Applicative is also a Functor that supports function
 *   application within their contexts.
 *
 * The minimal complete definition is provided by pure and ap.
 *
 * The three functor laws are:
 *   ap(v, pure(id)) = v
 *   ap(pure(x), pure(f)) = pure(f(x))
 *   ap(u, ap(v, w)) = pure(.) ap u ap v ap w
 *
 * @author	                    Ken Barclay
 * @since                       August 2018
 */

import com.adt.kotlin.kats.data.immutable.tuple.Tuple4
import com.adt.kotlin.kats.data.immutable.tuple.Tuple5
import com.adt.kotlin.kats.hkfp.kind.Kind1



/**
 * Applicative functors is an abstract characterisation of an applicative style of
 *   effectful programming.
 *
 * @param F                     representation for the context
 */
interface Applicative<F> : Functor<F> {

    // ---------- primitive operations --------------------

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    fun <A> pure(a: A): Kind1<F, A>

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    fun <A, B> ap(v: Kind1<F, A>, f: Kind1<F, (A) -> B>): Kind1<F, B>



    // ---------- derived operations ----------------------

    /**
     * Sequence actions, discarding the value of the first argument.
     *   This is equivalent to *> in Haskell.
     */
    fun <A, B> sDF(va: Kind1<F, A>, vb: Kind1<F, B>): Kind1<F, B> =
            ap(vb, replaceAll(va){b: B -> b})

    /**
     * Sequence actions, discarding the value of the second argument.
     *   This is equivalent to <* in Haskell.
     */
    fun <A, B> sDS(va: Kind1<F, A>, vb: Kind1<F, B>): Kind1<F, A> =
            liftA2{a: A -> {_: B -> a}}(va)(vb)

    /**
     * The product of two applicatives.
     */
    fun <A, B> product2(va: Kind1<F, A>, vb: Kind1<F, B>): Kind1<F, Pair<A, B>> =
            ap(va, fmap(vb){b: B -> {a: A -> Pair(a, b)}})

    /**
     * The product of three applicatives.
     */
    fun <A, B, C> product3(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>): Kind1<F, Triple<A, B, C>> {
        val tupled2: Kind1<F, Pair<A, B>> = product2(va, vb)
        return fmap(product2(tupled2, vc)){pair: Pair<Pair<A, B>, C> -> Triple(pair.first.first, pair.first.second, pair.second)}
    }   // product3

    fun <A, B, C, D> product4(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>, vd: Kind1<F, D>): Kind1<F, Tuple4<A, B, C, D>> {
        val tupled3: Kind1<F, Triple<A, B, C>> = product3(va, vb, vc)
        return fmap(product2(tupled3, vd)){pair: Pair<Triple<A, B, C>, D> ->
            Tuple4(pair.first.first, pair.first.second, pair.first.third, pair.second)
        }
    }   // product4

    fun <A, B, C, D, E> product5(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>, vd: Kind1<F, D>, ve: Kind1<F, E>): Kind1<F, Tuple5<A, B, C, D, E>> {
        val tupled4: Kind1<F, Tuple4<A, B, C, D>> = product4(va, vb, vc, vd)
        return fmap(product2(tupled4, ve)){pair: Pair<Tuple4<A, B, C, D>, E> ->
            Tuple5(pair.first.a, pair.first.b, pair.first.c, pair.first.d, pair.second)
        }
    }   // product5

    /**
     * fmap2 is a binary version of fmap.
     */
    fun <A, B, C> fmap2(va: Kind1<F, A>, vb: Kind1<F, B>, f: (A) -> (B) -> C): Kind1<F, C> =
        fmap(product2(va, vb)){pairs: Pair<A, B> -> f(pairs.first)(pairs.second)}

    /**
     * fmap3 is a ternary version of fmap.
     */
    fun <A, B, C, D> fmap3(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>, f: (A) -> (B) -> (C) -> D): Kind1<F, D> =
        fmap(product3(va, vb, vc)){triple: Triple<A, B, C> ->
            f(triple.first)(triple.second)(triple.third)
        }

    fun <A, B, C, D, E> fmap4(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>, vd: Kind1<F, D>, f: (A) -> (B) -> (C) -> (D) -> E): Kind1<F, E> =
            fmap(product4(va, vb, vc, vd)){tuple: Tuple4<A, B, C, D> ->
                f(tuple.a)(tuple.b)(tuple.c)(tuple.d)
            }

    fun <A, B, C, D, E, FF> fmap4(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>, vd: Kind1<F, D>, ve: Kind1<F, E>, f: (A) -> (B) -> (C) -> (D) -> (E) -> FF): Kind1<F, FF> =
            fmap(product5(va, vb, vc, vd, ve)){tuple: Tuple5<A, B, C, D, E> ->
                f(tuple.a)(tuple.b)(tuple.c)(tuple.d)(tuple.e)
            }

    /**
     * ap2 is a binary version of ap, defined in terms of ap.
     */
    fun <A, B, C> ap2(va: Kind1<F, A>, vb: Kind1<F, B>, f: Kind1<F, (A) -> (B) -> C>): Kind1<F, C> =
        fmap(product2(va, product2(vb, f))){pairs: Pair<A, Pair<B, (A) -> (B) -> C>> ->
            pairs.second.second(pairs.first)(pairs.second.first)
        }

    /**
     * ap3 is a ternary version of ap, defined in terms of ap.
     */
    fun <A, B, C, D> ap3(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>, f: Kind1<F, (A) -> (B) -> (C) -> D>): Kind1<F, D> =
        fmap(product2(va, product2(vb, product2(vc, f)))){pairs: Pair<A, Pair<B, Pair<C, (A) -> (B) -> (C) -> D>>> ->
            pairs.second.second.second(pairs.first)(pairs.second.first)(pairs.second.second.first)
        }

    fun <A, B, C, D, E> ap4(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>, vd: Kind1<F, D>, f: Kind1<F, (A) -> (B) -> (C) -> (D) -> E>): Kind1<F, E> =
            fmap(product2(va, product2(vb, product2(vc, product2(vd, f))))){pairs ->
                pairs.second.second.second.second(pairs.first)(pairs.second.first)(pairs.second.second.first)(pairs.second.second.second.first)
            }

    fun <A, B, C, D, E, FF> ap5(va: Kind1<F, A>, vb: Kind1<F, B>, vc: Kind1<F, C>, vd: Kind1<F, D>, ve: Kind1<F, E>, f: Kind1<F, (A) -> (B) -> (C) -> (D) -> (E) -> FF>): Kind1<F, FF> =
            fmap(product2(va, product2(vb, product2(vc, product2(vd, product2(ve, f)))))){pairs ->
                pairs.second.second.second.second.second(pairs.first)(pairs.second.first)(pairs.second.second.first)(pairs.second.second.second.first)(pairs.second.second.second.second.first)
            }



// ---------- utility functions ---------------------------

    /**
     * Lift a binary function to actions.
     */
    fun <A, B, C> liftA2(f: (A) -> (B) -> C): (Kind1<F, A>) -> (Kind1<F, B>) -> Kind1<F, C> =
            {fa: Kind1<F, A> ->
                {fb: Kind1<F, B> ->
                    ap(fb, fmap(fa, f))
                }
            }   // liftA2

    /**
     * Lift a ternary function to actions.
     */
    fun <A, B, C, D> liftA3(f: (A) -> (B) -> (C) -> D): (Kind1<F, A>) -> (Kind1<F, B>) -> (Kind1<F, C>) -> Kind1<F, D> =
            {fa: Kind1<F, A> ->
                {fb: Kind1<F, B> ->
                    {fc: Kind1<F, C> ->
                        ap(fc, ap(fb, fmap(fa, f)))
                    }
                }
            }   // liftA3

    fun <A, B, C, D, E> liftA4(f: (A) -> (B) -> (C) -> (D) -> E): (Kind1<F, A>) -> (Kind1<F, B>) -> (Kind1<F, C>) -> (Kind1<F, D>) -> Kind1<F, E> =
            {fa: Kind1<F, A> ->
                {fb: Kind1<F, B> ->
                    {fc: Kind1<F, C> ->
                        {fd: Kind1<F, D> ->
                            ap(fd, ap(fc, ap(fb, fmap(fa, f))))
                        }
                    }
                }
            }   // liftA4

    fun <A, B, C, D, E, FF> liftA5(f: (A) -> (B) -> (C) -> (D) -> (E) -> FF): (Kind1<F, A>) -> (Kind1<F, B>) -> (Kind1<F, C>) -> (Kind1<F, D>) -> (Kind1<F, E>) -> Kind1<F, FF> =
            {fa: Kind1<F, A> ->
                {fb: Kind1<F, B> ->
                    {fc: Kind1<F, C> ->
                        {fd: Kind1<F, D> ->
                            {fe: Kind1<F, E> ->
                                ap(fe, ap(fd, ap(fc, ap(fb, fmap(fa, f)))))
                            }
                        }
                    }
                }
            }   // liftA5

}   // Applicative
