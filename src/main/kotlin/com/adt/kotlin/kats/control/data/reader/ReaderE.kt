package com.adt.kotlin.kats.control.data.reader



/**
 * Since class Reader<A, B> is the only implementation for Kind1<Kind1<ReaderProxy, A>, B>
 *   we define this extension function to perform the downcasting safely.
 */
@Suppress("UNCHECKED_CAST")
fun <A, B> ReaderOf<A, B>.narrow(): Reader<A, B> = this as Reader<A, B>



/**
 * An infix symbol for fmap.
 *
 * Examples:
 *   ({n: Int -> isEven(n)} dollar reader{str: String -> str.length})("") == true
 *   ({n: Int -> isEven(n)} dollar reader{str: String -> str.length})("ken") == false
 *   ({n: Int -> isEven(n)} dollar reader(3))("anything") == false
 */
infix fun <A, B, C> ((B) -> C).dollar(v: Reader<A, B>): Reader<A, C> = v.map(this)

/**
 * An infix symbol for ap.
 *
 * Examples:
 *   (reader{str: String -> {n: Int -> n * n}} apply reader{str: String -> str.length})("") == 0
 *   (reader{str: String -> {n: Int -> n * n}} apply reader{str: String -> str.length})("kenneth") == 49
 *   (reader{str: String -> {n: Int -> n * n}} apply reader(3))("anything") == 9
 */
infix fun <A, B, C> Reader<A, (B) -> C>.apply(v: Reader<A, B>): Reader<A, C> = v.ap(this)

/**
 * An infix symbol for ap.
 *
 * Examples:
 *   let strToInt = reader{str: String -> {n: Int -> n * n}}
 *   let strTo3 = string(3)
 *   ({m: Int -> {n: Int -> m + n}} dollar strToInt appliedOver strToInt)("") == 0
 *   ({m: Int -> {n: Int -> m + n}} dollar strToInt appliedOver strToInt)("kenneth") == 14
 *   ({m: Int -> {n: Int -> m + n}} dollar strToInt appliedOver strTo3)("anything") == 11
 */
infix fun <A, B, C> Reader<A, (B) -> C>.appliedOver(v: Reader<A, B>): Reader<A, C> = v.ap(this)
