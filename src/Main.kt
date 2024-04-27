import CNF_Converter.stringTo
import CNF_Converter.toClass
import kotlin.io.*
fun main() {
    val cnfList: List<CNF> = toClass(stringTo("IMP('c','d')"))
    System.out.println(cnfList[0].convert().simplify().toSAT())
}