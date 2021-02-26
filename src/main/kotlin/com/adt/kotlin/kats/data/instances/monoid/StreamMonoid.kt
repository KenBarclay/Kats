package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.stream.Stream
import com.adt.kotlin.kats.data.immutable.stream.StreamF
import com.adt.kotlin.kats.hkfp.typeclass.Monoid



class StreamMonoid<A> : Monoid<Stream<A>> {

    override val empty: Stream<A> = StreamF.nil()
    override fun combine(a: Stream<A>, b: Stream<A>): Stream<A> = a.append(b)

}   //StreamMonoid
