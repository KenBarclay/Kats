package com.adt.kotlin.kats.control.instances.applicative

import com.adt.kotlin.kats.control.data.kleisli.Kleisli
import com.adt.kotlin.kats.control.data.kleisli.Kleisli.KleisliProxy
import com.adt.kotlin.kats.control.data.kleisli.KleisliOf
import com.adt.kotlin.kats.control.data.kleisli.narrow
import com.adt.kotlin.kats.control.instances.functor.KleisliFunctor

import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Functor



interface KleisliApplicative<F, A> : Applicative<Kind1<Kind1<KleisliProxy, F>, A>>, KleisliFunctor<F, A> {

    fun applicative(): Applicative<F>

    override fun functor(): Functor<F> = applicative()

    /**
     * Take a value of any type and returns a context enclosing the value.
     */
    override fun <B> pure(a: B): Kleisli<F, A, B> = Kleisli {_: A -> applicative().pure(a)}

    /**
     * Apply the function wrapped in a context to the content of the
     *   value also wrapped in a matching context.
     */
    override fun <B, C> ap(v: KleisliOf<F, A, B>, f: KleisliOf<F, A, (B) -> C>): Kleisli<F, A, C> {
        val vKleisli: Kleisli<F, A, B> = v.narrow()
        val fKleisli: Kleisli<F, A, (B) -> C> = f.narrow()
        return Kleisli { a: A -> applicative().ap(vKleisli.run(a), fKleisli.run(a)) }
    }   // ap

}   // KleisliApplicative
