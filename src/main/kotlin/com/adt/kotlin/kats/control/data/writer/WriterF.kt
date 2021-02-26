package com.adt.kotlin.kats.control.data.writer



object WriterF {

    /**
     * Constructor factory functions.
     */
    fun <W, A> writer(pair: Pair<W, A>): Writer<W, A> = Writer(pair)
    fun <W, A> writer(w: W, a: A): Writer<W, A> = Writer(Pair(w, a))

    /**
     * An action that produces the output w.
     *
     * Examples:
     *   let wr = WriterF.tell("Ken")
     *   wr.written() = "Ken"
     */
    fun <W> tell(w: W): Writer<W, Unit> = writer(Pair(w, Unit))



    // Functor extension functions:

    /**
     * Lift a function into the Writer context.
     */
    fun <W, A, B> lift(f: (A) -> B): (Writer<W, A>) -> Writer<W, B> =
            {wwa: Writer<W, A> -> wwa.map(f)}

}   // WriterF
