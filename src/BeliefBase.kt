import CNF_Converter.stringTo
import CNF_Converter.toClass

/**
 * This one needs to call the Java code
 */
private fun toCNF(expression: String): String{
    //TODO: Call external library. Supposedly hard, so dont bother doing it yourself
    val cnfList: List<CNF> = toClass(stringTo(expression))
    return cnfList.toString()
}
/**
 * Checks whether two beliefs contradict eachother
 */
private fun contradicts(belief1: Belief, belief2: Belief): Boolean{
    //https://sat.inesc-id.pt/~ines/cp07.pdf This for some advanced shit.
    // Maybe we should just iterate over every combination first
    return TODO()
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

class Belief(originalExpression: String) {
    val CNF: String = toCNF(originalExpression)
    val originalExpression: String = originalExpression

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

    private fun addBelief(beliefToAdd: Belief) {
        beliefToAdd.addedNumber = numberOfBeliefs
        numberOfBeliefs++
        beliefs.add(beliefToAdd)
        redoEntailments()
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
    public fun giveBelief(newBelief: Belief) {
        do {
            val contradictingBeliefs = mutableSetOf<Belief>()
            for (belief in beliefs) {
                if (contradicts(belief, newBelief)) {
                    contradictingBeliefs.add(belief)
                }
            }
            //An issue exists
            val beliefToRemove = selectBeliefToRemove(contradictingBeliefs)
            //Shouldn't ever be an issue, but a lil error checking never hurts
            if (!beliefs.remove(beliefToRemove)) {throw Exception("Tried to remove belief that wasn't in belief base")}
        } while (contradictingBeliefs.size!=0)
        //The absolutely last things we do.
        addBelief(newBelief)

    }




    /**
     * Returns whether a belief is contradictory to the knowledge base. Not necessary for the assignment afaik
     */
    public fun checkIfBeliefContradicts(beliefToCheck: Belief): Boolean{
        for (belief in beliefs) {
            if (contradicts(belief, beliefToCheck)) return true
        }
        return false
    }
}

