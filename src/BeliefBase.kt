
/**
 * This one needs to call the Java code
 */
fun toCNF(expression: String): String{

    return TODO()
}
/**
 * Checks whether two beliefs contradict eachother
 */
fun contradicts(belief1: Belief, belief2: Belief): Boolean{
    //https://sat.inesc-id.pt/~ines/cp07.pdf This for some advanced shit. Maybe we should just iterate over every combination first
    return TODO()
}

/**
 * This function is basically confirmation basis, algorithmically
 */
fun getWorth(belief:Belief): Int{
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

    val entailments: MutableList<Belief> = mutableListOf() //All beliefs that directly follow from this belief
    val parents: MutableList<Belief> = mutableListOf() //The opposite. Not sure if we want this
}
/**
 * The big boy
 */
class BeliefBase {
    private var numberOfBeliefs: Int = 0
    private val beliefs: MutableSet<Belief> = TODO()

    private fun addBelief(belief: Belief) {
        belief.addedNumber = numberOfBeliefs
        numberOfBeliefs++
        beliefs.add(belief)
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
}

