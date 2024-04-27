import CNF_Converter.stringTo
import CNF_Converter.toClass
import java.lang.reflect.Type
import kotlin.io.*
fun main() {
    val cnfList: List<CNFimported> = toClass(stringTo("NOT('d')"))
    val beliefBase: BeliefBase = BeliefBase()
    println(cnfList[0].convert().simplify().toSAT())
    beliefBase.giveBeliefString(cnfList[0].convert().simplify().toSAT())

    println("\n\n\n")

    val cnfList2: List<CNFimported> = toClass(stringTo("'d'"))
    beliefBase.giveBeliefString(cnfList2[0].convert().simplify().toSAT())
    beliefBase.printBeliefs()

}