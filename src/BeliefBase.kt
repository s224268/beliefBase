private class Disjunction(val disjunctionString: String){
    val variables: MutableList<Literal> = mutableListOf()
    init{
        for (literalString in disjunctionString.split('∨')){
            variables.add(Literal(literalString))
        }
    }
    fun evaluate(map: Map<String, Boolean?>): Boolean?{
        var falseVariableCounter = 0
        for (variable in variables){
            if (variable.evaluate(map)){
                return true
            } else if (variable.evaluate(map) == false){
                falseVariableCounter++
            }
        }
        if (falseVariableCounter == variables.size){
            return false
        }
        return null
    }
}

/**
 * This is a conjunction
 */
private class CNF(val CNFString: String) {
    val disjunctions: MutableList<Disjunction> = mutableListOf()
    init{
        try{
            val stringList = CNFString.split('∧')
            for(disjunctionString in stringList){
                disjunctions.add(Disjunction(disjunctionString))
            }
        } catch (e: KotlinNullPointerException){
            println("Invalid input")
        }
    }


    fun evaluate(map: Map<String, Boolean?>): Boolean? {
        for (disjunction in disjunctions){
            val result = disjunction.evaluate(map)
            if (result == null) return null
            if (!disjunction.evaluate(map)!!){
                return false
            }
        }
        return true
    }
}

private class Literal(val literalString: String){
    var varName: String = ""
    var isNot: Boolean = false

    init{
        if(literalString.contains('¬')){
            isNot = true
        }
        val regex = "[a-zA-Z]+".toRegex()
        varName = regex.find(literalString,0)!!.value

    }

    fun evaluate(map: Map<String, Boolean?>): Boolean?{
        if (map[varName] == null){
            return null
        }
        if (isNot){
            return !map[varName]!!
        }
        return map[varName]!!
    }
}

/**
 * This function is basically confirmation basis, algorithmically
 */
private fun getWorth(belief:Belief): Int{
    //TODO: We should ideally determine this based on number of entailments, but this works for now
    return belief.addedNumber
}
/**
 * Decides on which belief to remove.
 */
private fun selectBeliefToRemove(contradictingBeliefs: Set<Belief>): Belief{

    //TODO The following is based on number of entailments/children.
    // We could, alternatively, just order them based on addedNumber if this is impractical
    val beliefsToNumbers: MutableMap<Belief, Int> = mutableMapOf() //Rename this?
    for (belief in contradictingBeliefs) {
        beliefsToNumbers.put(key = belief, value = getWorth(belief))
    }
    return beliefsToNumbers.minBy{ it.value }.key //Lowest val
}

public class Belief(originalExpression: String) {
    val CNFString = originalExpression
    var CNF: CNF = CNF(originalExpression)

    //addedNumber Is used to order the beliefs. Maybe we should just use a sorted list instead.
    //This is mainly for if we just remove the oldest belief first, which is dubious, but at the same time
    //we automatically assume that newer beliefs are true so it follows that older beliefs are less true
    var addedNumber: Int = 0

    //All beliefs that directly follow from this belief. This is the "Children"
    val entailments: MutableList<Belief> = mutableListOf()

    val parents: MutableList<Belief> = mutableListOf() //The corresponding parents. Not sure if we want this
}
/**
 * The big boy
 */
class BeliefBase {
    private var numberOfBeliefs: Int = 0 //Keeps track of total number of beliefs that have been added. Works as a "timestamp"

    //I think every base belief should be added to this, but not entailments. E.G if we know that (A||B) and !B,
    //then (A||B), !B are added, but A is added as a child of (A||B) AND !B. Then, if we later get told that B,
    //it will easy to remove A from all its parents (which is why we store the parents).
    //My current idea is to essentially delete all children and then redo entailment calculations. Slower, but simpler.
    //
    // TODO: Discuss all this or make a decision
    //There is no reason to ever remove a belief unless we find its direct contradiction, since redundant information
    //may be un-redundated when presented with new info
    private val beliefs: MutableSet<Belief> = mutableSetOf() //Only holds base beliefs. None of these have parents

    /**
     * Checks whether two beliefs contradict eachother
     */
    private fun contradicts(belief1: Belief): Boolean{
        //https://sat.inesc-id.pt/~ines/cp07.pdf This for some advanced shit.
        // Maybe we should just iterate over every combination first
        return TODO()
    }

    fun printBeliefs(){
        println("Current base beliefs are:")
        for (belief in beliefs){
            println(belief.CNFString)
        }
        println("Entailments of these are:")
        println("TODO") //TODO: Fix this
    }

    private fun addBelief(beliefToAdd: Belief) {
        beliefToAdd.addedNumber = numberOfBeliefs
        numberOfBeliefs++
        beliefs.add(beliefToAdd)
        //redoEntailments()
    }

    private fun clearAllEntailments(){
        //Hugely inefficient, but I don't see the issue. Simpler than going through every child and determining if it's still true
        for (belief in beliefs){
            belief.entailments.clear()
            if (belief.parents.isNotEmpty()){
                throw Exception("Base belief had parent")
            }
        }
    }

    /**
     * Since we have added a new belief, we need to determine whether there are any new entailments.
     * If we know that (A||B) and !A, then B is a child of both (A||B) and !A
     * My intuition is to clear every entailment and start over, but we can discuss this.
     */
    private fun redoEntailments(){
        clearAllEntailments()
        determineEntailments()
    }

    private fun determineEntailments(){


        TODO() //This is where all the actual hard code goes
    }

    /**
     * The "main" method for adding a belief
     */
    public fun giveBeliefString(newBeliefString: String){
        giveBelief(Belief(newBeliefString))
    }
    private fun giveBelief(newBelief: Belief) {
        do {
            val contradictingBeliefs = mutableSetOf<Belief>()

            if (!DPLL_satisfiable(newBelief)){
                println("Model is not satisfiable:")
            }

        } while (false)
        //The absolutely last things we do.
        addBelief(newBelief)

    }




    /**
     * Returns whether a belief is contradictory to the knowledge base. Not necessary for the assignment afaik
     */
    /*
    public fun checkIfBeliefContradicts(beliefToCheck: Belief): Boolean{
        for (belief in beliefs) {
            if (contradicts(belief, beliefToCheck)) return true
        }
        return false
    }

     */

    private fun DPLL_satisfiable(newBelief: Belief): Boolean{
        val clauses: MutableSet<Disjunction> = mutableSetOf<Disjunction>()
        val literals: MutableSet<Literal> = mutableSetOf<Literal>()
        val model: MutableMap<String, Boolean?> = mutableMapOf()

        for (belief in beliefs){
            clauses.addAll(belief.CNF.disjunctions)
        }
        clauses.addAll(newBelief.CNF.disjunctions)

        for(belief in beliefs){
            for (disjunc in belief.CNF.disjunctions){
                for(literal in disjunc.variables){
                    literals.add(literal)
                }
            }
        }
        for (disjunc in newBelief.CNF.disjunctions){
            literals.addAll(disjunc.variables)
        }
        return DPLL(clauses, literals, model)!!
    }

    private fun allClausesTrue(clauses: Set<Disjunction>, model: Map<String, Boolean?>): Boolean {
        var truthCounter = 0
        for (clause in clauses) {
            if (clause.evaluate(model) == true) {
                truthCounter++
            }
        }
        return truthCounter == clauses.size
    }

    private fun someClauseFalse(clauses: Set<Disjunction>, model: Map<String, Boolean?>): Boolean {
        for (clause in clauses) {
            if (clause.evaluate(model) == false) {
                return true
            }
        }
        return false
    }

    private fun DPLL(clauses: Set<Disjunction>, symbols: MutableSet<Literal>, model: MutableMap<String, Boolean?>): Boolean { //TODO: Remove kotlin.any
        //If every clause in clauses is true in model then return true

        if(allClausesTrue(clauses, model)){
            return true
        }

        //If some clause in clauses is false in model then return false
        if (someClauseFalse(clauses, model)){
            return false
        }

        // iterate over strings in model. If any string is only presented one time, safely set it to true and check the model
        //P, value = FINDPURESYMBOL(symbol, clauses, model)
        var P: Literal? = null

        for(literal in symbols) {
            var pure: Boolean = true
            var symbolsOfLiteral: List<Literal> =
                symbols.filter { sym -> sym.varName == literal.varName}
            for (innerLiteral in symbolsOfLiteral) {
                if (innerLiteral.isNot != literal.isNot) {
                    pure = false
                    break
                }
            }
            if(pure) {
                P = literal
            }
        }

        if (P != null){ //If P != null return DPLL(clauses, symbols - P, model where P = value)
            model[P.varName] = !P.isNot
            symbols.remove(P)
            return DPLL(clauses, symbols, model)
        }

        /*  Iterates over the clauses. If a clause contains exactly one undecided literal
            and all other literals are false (as by the model),
            then it should set that literal to true in the model
            and remove it from the symbols set.*/
        var secondP: Literal? = null
        for (clause in clauses) {
            var undecidedCount: Int = 0
            var allLiteralsFalse = true
            for (variable in clause.variables) {
                val status: Boolean? = model[variable.varName]
                if (status == null) {
                    undecidedCount += 1
                    secondP = variable
                }
                else if (status == true) {
                    allLiteralsFalse = false
                    break
                }
            }
            if (undecidedCount == 1 && allLiteralsFalse) {
                if (secondP != null) {
                    model[secondP.varName] = true
                }
                symbols.remove(secondP)
            }
        }


        //Exact problem here is that
        //P = FIRST(Symbols) [pick any?]
        //Rest = REST(symbols)

        //ANTON MODIFIED THIS
        val thirdP = symbols.first()
        symbols.remove(thirdP)
        val modelWherePTrue = model.toMutableMap()
        val modelWherePFalse = model.toMutableMap()
        modelWherePTrue.set(thirdP.varName, true)
        modelWherePFalse.set(thirdP.varName, false)
        return DPLL(clauses, symbols, modelWherePTrue) || DPLL(clauses, symbols, modelWherePFalse)

    }
}