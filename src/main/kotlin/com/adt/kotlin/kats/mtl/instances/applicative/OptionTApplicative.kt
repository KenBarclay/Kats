package com.adt.kotlin.kats.mtl.instances.applicative

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Monad

import com.adt.kotlin.kats.mtl.data.option.OptionT
import com.adt.kotlin.kats.mtl.data.option.OptionT.OptionTProxy
import com.adt.kotlin.kats.mtl.data.option.OptionTOf
import com.adt.kotlin.kats.mtl.data.option.narrow
import com.adt.kotlin.kats.mtl.instances.functor.OptionTFunctor



interface OptionTApplicative<F> : Applicative<Kind1<OptionTProxy, F>>, OptionTFunctor<F> {

    fun monad(): Monad<F>
    override fun functor(): Functor<F> = monad()

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): OptionT<F, A> {
        val mf: Monad<F> = monad()
        return OptionT(mf.inject(some(a)))
    }   // pure

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <A, B> ap(v: OptionTOf<F, A>, f: OptionTOf<F, (A) -> B>): OptionT<F, B> {
        val mf: Monad<F> = monad()
        val vOptionT: OptionT<F, A> = v.narrow()
        val fOptionT: OptionT<F, (A) -> B> = f.narrow()

        return OptionT(
                mf.run{
                    bind(fOptionT.runOptionT){opf: Option<(A) -> B> ->
                        when (opf) {
                            is None -> inject(none())
                            is Some -> {
                                mf.bind(vOptionT.runOptionT){opv: Option<A> ->
                                    when (opv) {
                                        is None -> inject(none())
                                        is Some -> inject(some(opf.value(opv.value)))
                                    }
                                }
                            }
                        }
                    }
                }
        )
    }   // ap

}   // OptionTApplicative
