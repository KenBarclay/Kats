package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.stream.Stream
import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface StreamSemigroup<A> : Semigroup<Stream<A>> {

    override fun combine(a: Stream<A>, b: Stream<A>): Stream<A> = a.append(b)

}   // StreamSemigroup
