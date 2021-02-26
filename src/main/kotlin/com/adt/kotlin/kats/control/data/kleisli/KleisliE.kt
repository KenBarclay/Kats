package com.adt.kotlin.kats.control.data.kleisli



/**
 * Since class Either<A, B> is the only implementation for Kind1<Kind1<EitherProxy, A>, B>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, A, B> KleisliOf<F, A, B>.narrow(): Kleisli<F, A, B> = this as Kleisli<F, A, B>
