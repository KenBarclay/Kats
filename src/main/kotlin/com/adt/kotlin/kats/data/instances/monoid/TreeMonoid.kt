package com.adt.kotlin.kats.data.instances.monoid

import com.adt.kotlin.kats.data.immutable.tree.Tree
import com.adt.kotlin.kats.data.immutable.tree.TreeF
import com.adt.kotlin.kats.hkfp.typeclass.Monoid



class TreeMonoid<A : Comparable<A>> : Monoid<Tree<A>> {

    override val empty: Tree<A> = TreeF.empty()

    override fun combine(a: Tree<A>, b: Tree<A>): Tree<A> =
            a.union(b)

}   // TreeMonoid
