package com.adt.kotlin.kats.mtl.data.either

import com.adt.kotlin.kats.mtl.data.option.OptionT


/**
 * Since class EitherT<F, A, B> is the only implementation for Kind1<Kind1<Kind1<EitherTProxy, F>, A>, B>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <F, A, B> EitherTOf<F, A, B>.narrow(): EitherT<F, A, B> = this as EitherT<F, A, B>
