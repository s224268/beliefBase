import CNF_Converter.stringTo
import CNF_Converter.toClass
import java.lang.reflect.Type
import kotlin.io.*
fun main() {
    val cnfList: List<CNFimported> = toClass(stringTo("IMP(IFF('k','b'),'d')"))
    val beliefBase: BeliefBase = BeliefBase()
    println(cnfList[0].convert().simplify().toSAT())
    val belief: Belief = Belief(cnfList[0].convert().simplify().toSAT())
    //beliefBase.giveBelief(belief)

}