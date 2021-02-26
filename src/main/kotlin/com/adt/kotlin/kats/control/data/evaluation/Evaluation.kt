package com.adt.kotlin.kats.control.data.evaluation

/**********
import com.adt.kotlin.kats.control.data.evaluation.EvaluationF.evaluate
import com.adt.kotlin.kats.data.immutable.option.Option
import com.adt.kotlin.kats.data.immutable.option.Option.None
import com.adt.kotlin.kats.data.immutable.option.Option.Some
import com.adt.kotlin.kats.data.immutable.option.OptionF.none
import com.adt.kotlin.kats.data.immutable.option.OptionF.some

sealed class Evaluation<out A> {

    abstract class Bind<A, START>(val start: () -> Evaluation<START>, run: (START) -> Evaluation<A>) : Evaluation<A>() {

        override val memoize: Evaluation<A> = Memoize(this)
        override fun value(): A = evaluate(this)
    }   // bind

    class Memoize<A>(val evaluation: Evaluation<A>) : Evaluation<A>() {
        var result: Option<A> = none()
        override val memoize: Evaluation<A> = this
        override fun value(): A =
                when (result) {
                    is None -> {
                        val a: A = evaluate(this)
                        result = some(a)
                        a
                    }
                    is Some -> result.value
                }
    }

    abstract fun value(): A

    abstract val memoize: Evaluation<A>

}   // Evaluation



sealed class Leaf<A> : Evaluation<A>() {

    class Now<A>(val value: A): Leaf<A>() {
        override val memoize: Evaluation<A> = this
    }

    class Later<A>(val f: () -> A) : Leaf<A>()

    class Always<A>(val f: () -> A) : Leaf<A>()

}   // Leaf
**********/
