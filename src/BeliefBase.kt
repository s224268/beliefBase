abstract class Belief {
    abstract val CNF: String
    abstract val originalExpression: String
    abstract fun evaluate()

}

class Conjunction: Belief(){
    override val CNF: String = TODO()
    override val originalExpression: String = TODO()

    override fun evaluate() {
        TODO("Not yet implemented")
    }
}

class disjunction: Belief(){
    override val CNF: String = TODO()
    override val originalExpression: String = TODO()

    override fun evaluate() {
        TODO("Not yet implemented")
    }
}

class BeliefBase {
    val beliefs: Set<Belief> = TODO()

    fun addBelief(belief: Belief){

        //Not sure this is the correct way to do it
        if (testBelief(belief)){

        }

    }

    private fun testBelief(belief: Belief): Boolean{
        //TODO: Test belief against existing beliefbase
    }



}