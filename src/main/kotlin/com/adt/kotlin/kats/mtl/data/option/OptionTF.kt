package com.adt.kotlin.kats.mtl.data.option

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor


object OptionTF {

    fun <F> none(af: Applicative<F>): OptionT<F, Nothing> = OptionT(af.pure(none()))

    fun <F, A> some(af: Applicative<F>, a: A): OptionT<F, A> = OptionT(af.pure(some(a)))

    fun <F, A> fromOption(af: Applicative<F>, option: Option<A>): OptionT<F, A> =
            OptionT(af.pure(option))

    fun <F, A> liftF(ff: Functor<F>, fa: Kind1<F, A>): OptionT<F, A> =
            ff.run{
                OptionT(fmap(fa){a: A -> some(a)})
            }   // liftF

    fun <F, G, A, B> mapOptionT(f: (Kind1<F, Option<A>>) -> Kind1<G, Option<B>>, fa: OptionT<F, A>): OptionT<G, B> =
            OptionT(f(fa.runOptionT))

}   // OptionTF
