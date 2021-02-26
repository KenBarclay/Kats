package com.adt.kotlin.kats.mtl.instances.monad

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.EitherF.right
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Monad
import com.adt.kotlin.kats.mtl.data.option.OptionT
import com.adt.kotlin.kats.mtl.data.option.OptionTOf
import com.adt.kotlin.kats.mtl.data.option.narrow
import com.adt.kotlin.kats.mtl.instances.applicative.OptionTApplicative



interface OptionTMonad<F> : Monad<Kind1<OptionT.OptionTProxy, F>>, OptionTApplicative<F> {

    /**
     * Inject a value into the monadic type.
     */
    override fun <A> inject(a: A): OptionT<F, A> {
        val mf: Monad<F> = monad()
        return OptionT(mf.inject(some(a)))
    }   // inject

    /**
     * Sequentially compose two actions, passing any value produced by the first
     *   as an argument to the second.
     */
    override fun <A, B> bind(v: OptionTOf<F, A>, f: (A) -> OptionTOf<F, B>): OptionT<F, B> {
        val mf: Monad<F> = monad()
        val vOptionT: OptionT<F, A> = v.narrow()

        return OptionT(
                mf.bind(vOptionT.runOptionT){op: Option<A> ->
                    when (op) {
                        is None -> mf.inject(none())
                        is Some -> {
                            val opfb: OptionT<F, B> = f(op.value).narrow()
                            opfb.runOptionT
                        }
                    }
                }
        )
    }   // bind



    /**
     * Keep calling f until an Either.Right<B> is returned.
     *   Implementations of this function should use constant
     *   stack space relative to f.
     */
    override fun <A, B> tailRecM(a: A, f: (A) ->OptionTOf<F, Either<A, B>>): OptionT<F, B> {
        val mf: Monad<F> = monad()
        return OptionT(mf.tailRecM(a){aa ->
            mf.run{
                val fab: OptionT<F, Either<A, B>> = f(aa).narrow()
                fmap(fab.runOptionT){option: Option<Either<A, B>> ->
                    option.fold({ right(none()) }, {eab: Either<A, B> -> eab.map{b: B -> some(b)} })
                }
            }
        })
    }   // tailRecM

}   // OptionTMonad
