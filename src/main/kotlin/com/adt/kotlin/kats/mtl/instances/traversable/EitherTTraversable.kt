package com.adt.kotlin.kats.mtl.instances.traversable

import com.adt.kotlin.kats.data.immutable.either.Either
import com.adt.kotlin.kats.data.immutable.either.Either.EitherProxy
import com.adt.kotlin.kats.data.immutable.either.EitherOf
import com.adt.kotlin.kats.data.immutable.either.narrow
import com.adt.kotlin.kats.hkfp.fp.FunctionF.C3
import com.adt.kotlin.kats.hkfp.fp.FunctionF.partial
import com.adt.kotlin.kats.hkfp.fp.FunctionF.rotateLeft
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.hkfp.typeclass.Traversable
import com.adt.kotlin.kats.mtl.data.either.EitherT
import com.adt.kotlin.kats.mtl.data.either.EitherT.EitherTProxy
import com.adt.kotlin.kats.mtl.data.either.EitherTOf
import com.adt.kotlin.kats.mtl.data.either.narrow
import com.adt.kotlin.kats.mtl.instances.foldable.EitherTFoldable
import com.adt.kotlin.kats.mtl.instances.functor.EitherTFunctor



interface EitherTTraversable<F, A> : Traversable<Kind1<Kind1<EitherTProxy, F>, A>>, EitherTFoldable<F, A>, EitherTFunctor<F, A> {

    fun traversable(): Traversable<F>
    override fun foldable(): Foldable<F> = traversable()

    override fun <G, B, C> traverse(v: EitherTOf<F, A, B>, ag: Applicative<G>, f: (B) -> Kind1<G, C>): Kind1<G, EitherT<F, A, C>> {
        val tf: Traversable<F> = traversable()
        val vEitherT: EitherT<F, A, B> = v.narrow()
        val eTraversable: Traversable<Kind1<EitherProxy, A>> = Either.traversable()
        val eTraversableC: ((B) -> Kind1<G, C>) -> (EitherOf<A, B>) -> Kind1<G, EitherOf<A, C>> = partial(ag, rotateLeft(C3(eTraversable::traverse)))

        val feabgeac: (EitherOf<A, B>) -> Kind1<G, EitherOf<A, C>> = eTraversableC(f)
        val gfeac: Kind1<G, Kind1<F, EitherOf<A, C>>> = tf.traverse(vEitherT.runEitherT, ag, feabgeac)

        return ag.fmap(gfeac){feac: Kind1<F, EitherOf<A, C>> ->
            EitherT(tf.fmap(feac){eac: EitherOf<A, C> -> eac.narrow()})
        }
    }   // traverse

}   // EitherTTraversable
