package com.adt.kotlin.kats.data.immutable.hamt

/**
 * The HamtMap is a persistent version of the classical hash table data structure.
 *   The structure supports efficient, non-destructive operations.
 *
 * The algebraic data type declaration is:
 *
 * datatype Node[A, B] = EmptyNode[A, B]
 *                     | LeafNode[A, B] of Int * A * B
 *                     | ArrayNode[A, B] of Int * [Node[A, B]]  where [...] is an array
 *                     | BitmapIndexedNode[A, B] of Int * [Node{A, B]]
 *                     | HashCollisionNode[A, B] of Int * List[Pair[A, B]]
 *
 * This implementation is modelled after the Haskell version described in the talk
 *   Faster persistent data structures through hashing by Johan Tibell at:
 *   https://www.haskell.org/wikiupload/6/65/HIW2011-Talk-Tibell.pdf. The Haskell
 *   code follows the Clojure implementation by Rich Hickey.
 *
 * @param A                     the type of keys in the map
 * @param B                     the type of values in the map
 *
 * @author	                    Ken Barclay
 * @since                       December 2014
 */

import com.adt.kotlin.kats.data.immutable.hamt.Map.MapProxy
import com.adt.kotlin.kats.data.immutable.hamt.node.Node
import com.adt.kotlin.kats.data.immutable.hamt.node.NodeF

import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.list.List
import com.adt.kotlin.kats.data.immutable.list.narrow
import com.adt.kotlin.kats.data.instances.foldable.HAMTFoldable
import com.adt.kotlin.kats.data.instances.functor.HAMTFunctor
import com.adt.kotlin.kats.data.instances.traversable.HAMTTraversable

import com.adt.kotlin.kats.hkfp.fp.FunctionF.C2
import com.adt.kotlin.kats.hkfp.kind.Kind1
import com.adt.kotlin.kats.hkfp.typeclass.Applicative
import com.adt.kotlin.kats.hkfp.typeclass.Foldable
import com.adt.kotlin.kats.hkfp.typeclass.Functor
import com.adt.kotlin.kats.hkfp.typeclass.Traversable


typealias MapOf<K, V> = Kind1<Kind1<MapProxy, K>, V>

class Map<K : Comparable<K>, V> internal constructor(val root: Node<K, V>) : Kind1<Kind1<MapProxy, K>, V> {

    class MapProxy private constructor()



    /**
     * Determine if the map contains the given key.
     *
     * @param key               search key
     * @return                  true if the map contains this key
     */
    fun containsKey(key: K): Boolean = root.containsKey(key)

    /**
     * Delete the key and its value from the map. If the key is not in the map
     *   then the original map is returned.
     *
     * @param key               look up key in the map
     * @return                  the updated map
     */
    fun delete(key: K): Map<K, V> = Map(root.delete(key))

    /**
     * Are two maps equal?
     *
     * @param other             the other map
     * @return                  true if both maps are the same; false otherwise
     */
    override fun equals(other: Any?): Boolean {
        return if (this === other)
            true
        else if (other == null || this::class.java != other::class.java)
            false
        else {
            @Suppress("UNCHECKED_CAST") val otherMap: Map<K, V> = other as Map<K, V>
            (this.size() == otherMap.size()) && (this.toAscendingList() == otherMap.toAscendingList())
        }
    }   // equals

    /**
     * Filter all values that satisfy the predicate.
     *
     * Examples:
     *   <[Jessie: 22, John: 31, Ken: 25]>.filter{v -> (v % 2 == 0)} = <[Jessie: 22]>
     *   <[]>.filter{v -> (v % 2 == 0)} = <[]>
     *
     * @param predicate         search criteria
     * @return                  resulting map
     */
    fun filter(predicate: (V) -> Boolean): Map<K, V> = Map(root.filter(predicate))

    /**
     * foldLeft is a higher-order function that folds a left associative binary
     *   function into the values of a map.
     *
     * Examples:
     *   <[Jessie: 22, John: 31, Ken: 25]>.foldLeft(0){res -> {age -> res + age}} = 78
     *   <[]>.foldLeft(0){res -> {age -> res + age}} = 0
     *   <[Jessie: 22, John: 31, Ken: 25]>.foldLeft([]){res -> {age -> res.append(age)}} = [22, 31, 25]
     *
     * @param e           	    initial value
     * @param f         		curried binary function:: W -> V -> W
     * @return            	    folded result
     */
    fun <W> foldLeft(e: W, f: (W) -> (V) -> W): W = root.foldLeft(e, f)

    fun <W> foldLeft(e: W, f: (W, V) -> W): W = this.foldLeft(e, C2(f))

    /**
     * foldRight is a higher-order function that folds a right associative binary
     *   function into the values of a map.
     *
     * Examples:
     *   <[Jessie: 22, John: 31, Ken: 25]>.foldRight(0){age -> {res -> res + age}} = 78
     *   <[]>.foldRight(0){age -> {res -> res + age}} = 0
     *   <[Jessie: 22, John: 31, Ken: 25]>.foldRight([]){age -> {res -> res.append(age)}} = [25, 31, 22]
     *
     * @param e           	    initial value
     * @param f         		curried binary function:: B -> C -> C
     * @return            	    folded result
     */
    fun <W> foldRight(e: W, f: (V) -> (W) -> W): W = root.foldRight(e, f)

    fun <W> foldRight(e: W, f: (V, W) -> W): W = this.foldRight(e, C2(f))

    /**
     * Insert a new key and the value into the map. If the key is already present,
     *   the associated value is replaced with the given value.
     *
     * @param key               new key
     * @param value             new associated value
     * @return                  updated map
     */
    fun insert(key: K, value: V): Map<K, V> = Map(root.insert(key, value))

    fun insert(pr: Pair<K, V>): Map<K, V> = insert(pr.first, pr.second)

    /**
     * Test whether the map is empty.
     *
     * @return                  true if the map contains zero elements
     */
    fun isEmpty(): Boolean = root.isEmpty()

    /**
     * Returns a List view of the keys contained in this map.
     *
     * Examples:
     *   <[Jessie: 22, John: 31, Ken: 25]>.keyList() = [Jessie, John, Ken]
     *   <[]>.keyList() = []
     *
     * @return    		        the keys for this map
     */
    fun keyList(): List<K> = root.keyList()

    /**
     * Obtain the size of the map.
     *
     * @return                  the number of elements in the map
     */
    fun length(): Int = this.size()

    /**
     * Look up the value at the key in the map. Return the corresponding
     *   value if the key is present otherwise throws an exception.
     *
     * @param key               search key
     * @return                  corresponding value, if key is present
     */
    fun lookUp(key: K): V = root.lookUp(key)

    /**
     * Look up the value at the key in the map.
     *
     * @param key               search key
     * @return                  corresponding value, if key is present
     */
    fun lookUpKey(key: K): Option<V> = root.lookUpKey(key)

    /**
     * Look up the given key in the map. Return defaultValue if absent, otherwise
     *   return the corresponding value.
     *
     * @param key               search key
     * @param defaultValue      default value to use if key is absent
     * @return                  matching value or default if key is absent
     */
    fun lookUpKeyWithDefault(key: K, defaultValue: V): V = root.lookUpKeyWithDefault(key, defaultValue)

    /**
     * Map a function over all the values in the map.
     *
     * Examples:
     *   <[Jessie: 22, John: 31, Ken: 25]>.map{v -> v + 1} = <[Jessie: 23, John: 32, Ken: 26]>
     *   <[Jessie: 22, John: 31, Ken: 25]>.map{v -> (v % 2 == 0)} = <[Jessie: true, John: false, Ken: false]>
     *
     * @param f                 mapping function
     * @return                  updated map
     */
    fun <W> map(f: (V) -> W): Map<K, W> = Map(root.map(f))

    fun <W> fmap(f: (V) -> W): Map<K, W> = this.map(f)

    /**
     * Apply the function to the key and its corresponding value as a replacement for the value.
     *
     * @param f                 the transformation function
     * @return                  map of same type with the replaced value
     */
    fun <W> mapWithKey(f: (K) -> (V) -> W): Map<K, W> =
            Map(root.mapWithKey(f))

    /**
     * Obtain the size of the map.
     *
     * @return                  the number of elements in the map
     */
    fun size(): Int = root.size()

    /**
     * Convert the map to a list of key/value pairs where the keys are in ascending order.
     */
    fun toAscendingList(): List<Pair<K, V>> = root.toAscendingList()

    /**
     * Present the map as a graph revealing the left and right subtrees.
     *
     * @return                  the map as a graph
     */
    fun toGraph(): String = root.toGraph()

    /**
     * Convert this map to a list of key/value pairs
     *
     * @return                  list of key/value pairs
     */
    fun toList(): List<Pair<K, V>> = root.toList()

    /**
     * Textual representation of a map.
     *
     * @return                  text representation including node sub-structures
     */
    override fun toString(): String {
        val content: String = root.toString()
        return "<{$content}>"
    }

    /**
     * Map each element of a structure to an action, evaluate these actions from left to right,
     *   and collect the results.
     */
    fun <G, W> traverse(ag: Applicative<G>, f: (V) -> Kind1<G, W>): Kind1<G, Map<K, W>> =
            this.traverseWithKey(ag){_ -> f}

    /**
     * Returns a List view of the values contained in this map.
     *
     * Examples:
     *   <[Jessie: 22, John: 31, Ken: 25]>.valueList() = [22, 31, 25]
     *   <[]>.valueList() = []
     *
     * @return    		        the values for this map
     */
    fun valueList(): List<V> = root.valueList()



// ---------- implementation ------------------------------

    private fun <G, W> traverseWithKey(ag: Applicative<G>, f: (K) -> (V) -> Kind1<G, W>): Kind1<G, Map<K, W>> {
        val vRoot: Node<K, V> = this.root
        val listKV: List<Pair<K, V>> = vRoot.toAscendingList()
        val keys: List<K> = listKV.map{pair: Pair<K, V> -> pair.first}
        val listW: Kind1<G, List<W>> = listKV.traverse(ag){pair: Pair<K, V> -> f(pair.first)(pair.second)}
        return ag.run{
            val x: Kind1<G, List<Pair<K, W>>> = fmap(listW){list: List<W> -> keys.zip(list.narrow())}
            fmap(x){list: List<Pair<K, W>> -> Map(NodeF.from(list))}
        }
    }   // traverseWithKey



    companion object {

        /**
         * Create an instance of this functor.
         */
        fun <K : Comparable<K>> functor(): Functor<Kind1<MapProxy, K>> = object: HAMTFunctor<K> {}

        /**
         * Create an instance of this foldable.
         */
        fun <K : Comparable<K>> foldable(): Foldable<Kind1<MapProxy, K>> = object: HAMTFoldable<K> {}

        /**
         * Create an instance of this traversable.
         */
        fun <K : Comparable<K>> traversable(): Traversable<Kind1<MapProxy, K>> = object: HAMTTraversable<K> {}

    }

}   // Map
