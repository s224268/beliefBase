import CNF_Converter.stringTo
import CNF_Converter.toClass
import java.lang.reflect.Type
import kotlin.io.*
fun main() {

    val beliefBase: BeliefBase = BeliefBase()
    giveBelief("OR('a','b','c')", beliefBase)
    giveBelief("NOT('a')", beliefBase)
    giveBelief("NOT('b')", beliefBase)
}

fun giveBelief(input: String, beliefBase: BeliefBase){
    val cnfList: List<CNFimported> = toClass(stringTo(input))
    beliefBase.giveBeliefString(cnfList[0].convert().simplify().toSAT())
}