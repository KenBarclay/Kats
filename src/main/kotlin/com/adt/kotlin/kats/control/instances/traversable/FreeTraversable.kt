package com.adt.kotlin.kats.control.instances.traversable

import com.adt.kotlin.kats.control.data.free.Free
import com.adt.kotlin.kats.control.data.free.Free.Pure
import com.adt.kotlin.kats.control.data.free.Free.Bind
import com.adt.kotlin.kats.control.data.free.Free.FreeProxy
import com.adt.kotlin.kats.control.data.free.FreeOf
import com.adt.kotlin.kats.control.data.free.narrow
import com.adt.kotlin.kats.control.instances.foldable.FreeFoldable
import com.adt.kotlin.kats.control.instances.functor.FreeFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Traversable



interface FreeTraversable<F> : Traversable<Kind1<FreeProxy, F>>, FreeFoldable<F>, FreeFunctor<F> {

    fun traversable(): Traversable<F>
    override fun foldable(): Foldable<F> = traversable()
    override fun functor(): Functor<F> = traversable()

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    override fun <G, A, B> traverse(v: FreeOf<F, A>, ag: Applicative<G>, f: (A) -> Kind1<G, B>): Kind1<G, Free<F, B>> {
        val tf: Traversable<F> = traversable()
        fun go(v: Free<F, A>): Kind1<G, Free<F, B>> {
            return ag.run{
                when (v) {
                    is Pure -> {
                        val gb: Kind1<G, B> = f(v.a)
                        val gfb: Kind1<G, Free<F, B>> = fmap(gb){b: B -> Pure(b)}
                        gfb
                    }
                    is Bind -> {
                        val vfree: Kind1<F, Free<F, A>> = v.free
                        val gffb: Kind1<G, Kind1<F, Free<F, B>>> = tf.traverse(vfree, ag, ::go)
                        ag.fmap(gffb) {ffb: Kind1<F, Free<F, B>> ->
                            val ff: Free<F, B> = Bind(ffb)
                            ff
                        }
                    }
                }
            }
        }   // go

        val vFree: Free<F, A> = v.narrow()
        return go(vFree)
    }   // traverse

}   // FreeTraversable
