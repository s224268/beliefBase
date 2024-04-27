import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CNF_Converter {
    static String noParen (String input) {
        int begin = input.indexOf("(");
        return input.substring(begin+1, input.length()-1);
    }

    // splits a string at , - ignores those in a paren
    static List<String> splitAt (String input) {
        int parens = 0;
        int index = 0;
        List<String> ret = new ArrayList<>();
        char[] charr = input.toCharArray();
        for (char c : charr) {
            if (c == '(') {
                parens += 1;
            } else if (c == ')') {
                parens -= 1;
            }
            if (parens == 0) {
                if (c == ',') {
                    ret.add(input.substring(0, index));
                    var recur = splitAt(input.substring(index+1));
                    ret.addAll(recur);
                    break;
                }
            }
            index += 1;
        }
        return ret.size() == 0 ? Collections.singletonList(input) : ret;
    }

    // takes an input string and returns all the terms and subterms
    static List<String> stringTo (String input) {
        List<String> terms = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        terms.add(input);

        if (input.contains("(")) {
            List<String> split = splitAt(noParen(input));
            //String subterm = noParen(input);
            temp.addAll(split);
            for (String s : temp) {
                if (s.length() == 0) {
                } else if (s.length() == 3) {
                    terms.add(s);
                } else {
                    terms.addAll(stringTo(s));
                }
            }
        }
        Set<String> noDupes = new HashSet<>(terms);
        return noDupes.stream().sorted((s1, s2) -> s2.length() - s1.length()).collect(Collectors.toList());
    }

    static <E>BiFunction<CNFimported, CNFimported, CNFimported> arrows (String choice) {
        try {
            if (choice.equals("IFF")) {
                return Iff::new;
            } else if (choice.equals("IMP")) {
                return Imp::new;
            } else {
                throw new NoneTypeE();
            }
        } catch (NoneTypeE e) {
            e.printStackTrace();
            System.out.println("Invalid rule inputted");
            return null;
        }
    }

    static <E>Function<ArrayList<CNFimported>, CNFimported> andor (String choice) {
        try {
            if (choice.equals("AND")) {
                return And::new;
            } else if (choice.equals("OR(")) {
                return Or::new;
            } else {
                throw new NoneTypeE();
            }
        } catch (NoneTypeE e) {
            e.printStackTrace();
            System.out.println("Invalid rule inputted");
            return null;
        }
    }

    static Map<String, CNFimported> hash (List<CNFimported> terms) {
        Map<String, CNFimported> map = new HashMap<>();
        for (CNFimported t : terms) {
            map.put(t.toString(), t);
        }
        map.put("TRUE", new Atomicimported(true, ""));
        map.put("FALSE", new Atomicimported(false, ""));
        return map;
    }

    //this is where the actual conversion happens
    static List<CNFimported> toClass (List<String> terms) {
        List<String> possible = Arrays.asList("IFF", "OR", "NOT", "IMP", "AND");
        if (terms.size() > 0) {
            String goal = terms.remove(0);
            List<CNFimported> converted = toClass(terms);
            var map = hash(converted);
            if (goal.length() == 3) {
                converted.add(0, new Atomicimported(goal.charAt(1)));
            } else {
                if (goal.equals("TRUE")) {
                    converted.add(0, new Atomicimported(true, ""));
                } else if (goal.equals("FALSE")) {
                    converted.add(0, new Atomicimported(false, ""));
                } else {
                    String outer = goal.substring(0, 3);
                    String inner = noParen(goal);
                    if (outer.equals(possible.get(0)) || outer.equals(possible.get(3))) {
                        var constructor = arrows(outer);
                        List<String> matchon = splitAt(inner);
                        String left = matchon.get(0);
                        String right = matchon.get(1);
                        converted.add(0, constructor.apply(map.get(left), map.get(right)));
                    } else if (outer.equals(possible.get(2))) {
                        converted.add(0, new Not(map.get(inner)));
                    } else if (outer.contains(possible.get(1)) || outer.equals(possible.get(4))) {
                        var constructor = andor(outer);
                        List<String> matchon = splitAt(inner);
                        ArrayList<CNFimported> cnfed = new ArrayList<>();
                        for (String s : matchon) {
                            if (!s.equals("")) {
                                cnfed.add(map.get(s));
                            }
                        }
                        converted.add(0, constructor.apply(cnfed));
                    } else {
                        System.out.println("hate");
                    }
                }
            }
            return converted;
        } else {
            return new ArrayList<>();
        }
    }
}
