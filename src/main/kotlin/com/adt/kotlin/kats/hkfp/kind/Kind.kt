package com.adt.kotlin.kats.hkfp.kind

/**
 * Representation for higher-kinded types. For example Kind<F, A> represents
 *   the type F<A> while Kind2<F, A, B> represents the type F<A, B>.
 */

interface Kind<out F, out A>                            // representation for F<A>
typealias Kind1<F, A> = Kind<F, A>                      // representation for F<A>
typealias Kind2<F, A, B> = Kind<Kind<F, A>, B>          // representation for F<A, B>
typealias Kind3<F, A, B, C> = Kind<Kind2<F, A, B>, C>   // representation for F<A, B, C>
