import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

class NoneTypeE extends Exception {}

abstract class CNFimported {
    // methods here later
    abstract CNFimported convert ();
    abstract String toSAT ();
    abstract CNFimported simplify ();
}

class Atomicimported extends CNFimported {
    private char val = ' ';
    private boolean isTrue;

    Atomicimported(boolean b, String flag) {
        this.isTrue = b;
    }

    Atomicimported(char val) {
        this.val = val;
    }

    @Override
    public String toString () {
        if (val == ' ') {
            return isTrue ? "TRUE" : "FALSE";
        } else {
            return "\'" + val + "\'";
        }
    }

    @Override
    CNFimported convert () {
        return this;
    }

    @Override
    String toSAT() {
        if (val == ' ') {
            return isTrue ? "TRUE" : "FALSE";
        } else {
            return String.valueOf(val);
        }
    }

    @Override
    CNFimported simplify() {
        // cannot simplify atomic
        return this;
    }

    public char getVal() {
        return val;
    }

    public void setVal(char val) {
        this.val = val;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setBool(boolean aTrue) {
        this.isTrue = aTrue;
    }
}

class Not extends CNFimported {
    private CNFimported value;

    Not (CNFimported value) {
        this.value = value;
    }

    @Override
    public String toString () {
        return "NOT(" + value + ")";
    }

    @Override
    CNFimported convert() {
        return new Not(value.convert());
    }

    @Override
    String toSAT() {
        return "¬" + "(" + value.toSAT() + ")";
    }

    @Override
    CNFimported simplify() {
        CNFimported nval;
        if (value instanceof Not) {
            nval = ((Not) value).getValue().simplify();
        } else if (value instanceof And) {
            ArrayList<CNFimported> ors = new ArrayList<>();
            for (CNFimported o : ((And) value).getPhis()) {
                CNFimported negapp = new Not(o).simplify();
                ors.add(negapp);
            }
            nval = new Or(ors);
        } else if (value instanceof Or) {
            ArrayList<CNFimported> and = new ArrayList<>();
            for (CNFimported o : ((Or) value).getPhis()) {
                CNFimported negapp = new Not(o).simplify();
                and.add(negapp);
            }
            nval = new And(and);
        } else if (value instanceof Atomicimported) {
            if (((Atomicimported) value).getVal() == ' ') {
                if (((Atomicimported) value).isTrue()) {
                    nval = new Atomicimported(false, "");
                } else {
                    nval = new Atomicimported(true, "");
                }
            } else {
                nval = this;
            }
        } else {
            nval = this;
        }
        return nval;
    }

    public CNFimported getValue() {
        return value;
    }

    public void setValue(CNFimported value) {
        this.value = value;
    }
}


class Imp extends CNFimported {
    private CNFimported left;
    private CNFimported right;

    Imp (CNFimported left, CNFimported right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString () {
        return "IMP(" + left + "," + right + ")";
    }

    @Override
    CNFimported convert() {
        ArrayList<CNFimported> ors = new ArrayList<>(Arrays.asList(new Not(left.convert()), right.convert()));
        return new Or(ors);
    }

    @Override
    String toSAT() {
        return "(" + left.toSAT() + ")" + "→" + "(" + right.toSAT() + ")";
    }

    @Override
    CNFimported simplify() {
        // cannot simply if
        return this;
    }

    public CNFimported getLeft() {
        return left;
    }

    public void setLeft(CNFimported left) {
        this.left = left;
    }

    public CNFimported getRight() {
        return right;
    }

    public void setRight(CNFimported right) {
        this.right = right;
    }
}


class Iff extends CNFimported {
    private CNFimported left;
    private CNFimported right;
    // same as its recent parent

    Iff (CNFimported left, CNFimported right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString () {
        return "IFF(" + left + "," + right + ")";
    }

    @Override
    CNFimported convert() {
        Imp nleft = new Imp(left, right);
        Imp nright = new Imp(right, left);
        ArrayList<CNFimported> ands = new ArrayList<>(Arrays.asList(nleft.convert(), nright.convert()));
        return new And(ands);
    }

    @Override
    String toSAT() {
        return "(" + left.toSAT() + ")" + "↔" + "(" + right.toSAT() + ")";
    }

    @Override
    CNFimported simplify() {
        // cannot simply if
        return null;
    }

    public CNFimported getLeft() {
        return left;
    }

    public void setLeft(CNFimported left) {
        this.left = left;
    }

    public CNFimported getRight() {
        return right;
    }

    public void setRight(CNFimported right) {
        this.right = right;
    }
}

class Or extends CNFimported {
    private ArrayList<CNFimported> phis;

    Or (ArrayList<CNFimported> phis) {
        this.phis = phis;
    }

    @Override
    public String toString () {
        String returned = "OR(";
        String sep = ",";
        for (int i=0; i<phis.size(); i++) {
            returned += (i == phis.size()-1) ? phis.get(i) + ")": phis.get(i) + sep;
        }
        return phis.size() == 0 ? returned + ")" : returned;
    }

    @Override
    CNFimported convert() {
        ArrayList<CNFimported> converted = new ArrayList<>();
        for (CNFimported phi : phis) {
            converted.add(phi.convert());
        }
        return phis.size() == 0 ? new Atomicimported(false, "") : new Or(converted);
    }

    @Override
    String toSAT() {
        return phis.size() == 0 ? "FALSE" : phis.stream().map((x) -> "(" + x.toSAT() + ")").collect(Collectors.joining("∨"));
    }

    @Override
    CNFimported simplify() {
        ArrayList<CNFimported> simps = new ArrayList<>();
        for (CNFimported phi : phis) {
            CNFimported simp = phi.simplify();
            if (simp instanceof Atomicimported) {
                if (((Atomicimported) simp).getVal() == ' ') {
                    if (((Atomicimported) simp).isTrue()) {
                        return new Atomicimported(true, "");
                    } else if (!((Atomicimported) simp).isTrue()) {
                        continue;
                    }
                }
            }
            simps.add(simp);
        }
        return simps.size() == 0 ? new Atomicimported(false, "") : new Or(simps);
    }

    public ArrayList<CNFimported> getPhis() {
        return phis;
    }

    public void setPhis(ArrayList<CNFimported> phis) {
        this.phis = phis;
    }
}


class And extends CNFimported {
    private ArrayList<CNFimported> phis;

    And (ArrayList<CNFimported> phis) {
        this.phis = phis;
    }

    @Override
    public String toString () {
        String returned = "AND(";
        String sep = ",";
        for (int i=0; i<phis.size(); i++) {
            returned += (i == phis.size()-1) ? phis.get(i) + ")": phis.get(i) + sep;
        }
        return phis.size() == 0 ? returned + ")" : returned;
    }

    @Override
    CNFimported convert() {
        ArrayList<CNFimported> converted = new ArrayList<>();
        for (CNFimported phi : phis) {
            converted.add(phi.convert());
        }
        return phis.size() == 0 ? new Atomicimported(true, "") : new And(converted);
    }

    @Override
    String toSAT() {
        return phis.size() == 0 ? "TRUE" : phis.stream().map((x) -> "(" + x.toSAT() + ")").collect(Collectors.joining("∧"));
    }

    @Override
    CNFimported simplify() {
        ArrayList<CNFimported> simps = new ArrayList<>();
        for (CNFimported phi : phis) {
            CNFimported simp = phi.simplify();
            if (simp instanceof Atomicimported) {
                if (((Atomicimported) simp).getVal() == ' ') {
                    if (((Atomicimported) simp).isTrue()) {
                        continue;
                    } else if (!((Atomicimported) simp).isTrue()) {
                        return new Atomicimported(false, "");
                    }
                }
            }
            simps.add(simp);
        }
        return simps.size() == 0 ? new Atomicimported(true, "") : new And(simps);
    }

    public ArrayList<CNFimported> getPhis() {
        return phis;
    }

    public void setPhis(ArrayList<CNFimported> phis) {
        this.phis = phis;
    }
}