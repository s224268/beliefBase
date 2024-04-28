import CNF_Converter.stringTo
import CNF_Converter.toClass
import java.lang.reflect.Type
import kotlin.io.*
fun main() {
    takeInput()
}

fun takeInput(){
    val beliefBase: BeliefBase = BeliefBase()
    //TODO: Print grammar rules
    while(true){
        println("State your belief:")

        try{
            val input = readLine()!!
            giveBelief(input,beliefBase)
        } catch (e: Exception ){
            when(e){
                is java.lang.StringIndexOutOfBoundsException -> println("An error was found in your input. Please check it and try again")
                is java.lang.NullPointerException -> println("An error was found in your input. Please check it and try again")
                else -> println("A fatal error occured: " + e)
            }

        }

    }
}

fun testMain(){
    val beliefBase: BeliefBase = BeliefBase()
    giveBelief("AND(IFF('k','b'),NOT('k'))", beliefBase)
    /*
    giveBelief("NOT('c')", beliefBase)
    giveBelief("OR('a','b','c')", beliefBase)
    giveBelief("NOT('a')", beliefBase)

     */
}

fun giveBelief(input: String, beliefBase: BeliefBase){
    val cnfList: List<CNFimported> = toClass(stringTo(input))
    println(cnfList[0].convert().simplify().toSAT())
    beliefBase.giveBeliefString(cnfList[0].convert().simplify().toSAT())
}