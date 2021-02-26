package com.adt.kotlin.kats.control.instances.applicative

import com.adt.kotlin.kats.control.data.free.Free
import com.adt.kotlin.kats.control.data.free.Free.Pure
import com.adt.kotlin.kats.control.data.free.Free.Bind
import com.adt.kotlin.kats.control.data.free.Free.FreeProxy
import com.adt.kotlin.kats.control.data.free.FreeOf
import com.adt.kotlin.kats.control.data.free.narrow
import com.adt.kotlin.kats.control.instances.functor.FreeFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor


interface FreeApplicative<F> : Applicative<Kind1<FreeProxy, F>>, FreeFunctor<F> {

    //fun applicative(): Applicative<F>
    //override fun functor(): Functor<F> = applicative()

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <A> pure(a: A): Free<F, A> = Pure(a)

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <A, B> ap(v: FreeOf<F, A>, f: FreeOf<F, (A) -> B>): Free<F, B> {
        //val app: Applicative<F> = applicative()
        val ff: Functor<F> = functor()
        val vFree: Free<F, A> = v.narrow()
        val fFree: Free<F, (A) -> B> = f.narrow()
        return when (fFree) {
            is Pure -> when (vFree) {
                is Pure -> Pure(fFree.a(vFree.a))
                is Bind -> {
                    val fmapC: ((A) -> B) -> (FreeOf<F, A>) -> Free<F, B> =
                            {g -> {ffa -> fmap(ffa, g)}}
                    Bind(ff.fmap(vFree.free, fmapC(fFree.a)))
                    //Bind(app.fmap(vFree.free, fmapC(fFree.a)))
                }
            }
            is Bind -> {
                val ma: Kind1<F, Free<F, (A) -> B>> = fFree.free
                val b: Free<F, A> = vFree
                val apbC: (FreeOf<F, (A) -> B>) -> Free<F, B> =
                        {fab -> ap(b, fab)}
                val fma: Kind1<F, Free<F, B>> = ff.fmap(ma, apbC)
                //val fma: Kind1<F, Free<F, B>> = app.fmap(ma, apbC)
                Bind(fma)
            }
        }
    }   // ap

}   // FreeApplicative
