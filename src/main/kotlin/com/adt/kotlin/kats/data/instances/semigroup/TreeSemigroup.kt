package com.adt.kotlin.kats.data.instances.semigroup

import com.adt.kotlin.kats.data.immutable.tree.Tree
import com.adt.kotlin.kats.hkfp.typeclass.Semigroup



interface TreeSemigroup<A : Comparable<A>> : Semigroup<Tree<A>> {

    override fun combine(a: Tree<A>, b: Tree<A>): Tree<A> =
            a.union(b)

}   // TreeSemigroup
