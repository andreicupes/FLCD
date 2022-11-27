import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Grammar {
    private Set<String> N = new HashSet<>(); //nonterminals
    private Set<String> E = new HashSet<>(); //terminals
    private final HashMap<Set<String>, Set<List<String>>> P = new HashMap<>();
    private String S = "";

    public Grammar(String filename) {
        readFromFile(filename);
    }

    private void readFromFile(String filename) {
        try{
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            //we parse the nonTerminals line
            String input = reader.readLine();
            String[] nonTerminalsLineSplit = input.split("=",input.indexOf("="));
            StringBuilder nonTerminalsLine = new StringBuilder();
            for(int i=1;i<nonTerminalsLineSplit.length;++i)
                nonTerminalsLine.append(nonTerminalsLineSplit[i]);
            StringBuilder newStringBuilder = new StringBuilder(nonTerminalsLine.toString());
            // we eliminate the curly bracketss
            newStringBuilder.deleteCharAt(1).deleteCharAt(nonTerminalsLine.length()-2);
            nonTerminalsLine = new StringBuilder(newStringBuilder.toString());
            this.N = new HashSet<>(Arrays.asList(nonTerminalsLine.toString().strip().split(" ")));

            //we parse the Terminals line
            input = reader.readLine();
            String[] terminalsLineSplit = input.split("=",input.indexOf("="));
            StringBuilder terminalsLine = new StringBuilder();
            for(int i=1;i<terminalsLineSplit.length;++i)
                terminalsLine.append(terminalsLineSplit[i]);
            newStringBuilder = new StringBuilder(terminalsLine.toString());
            // we eliminate the curly brackets
            newStringBuilder.deleteCharAt(1).deleteCharAt(terminalsLine.length()-2);
            terminalsLine = new StringBuilder(newStringBuilder.toString());
            this.E = new HashSet<>(Arrays.asList(terminalsLine.toString().strip().split(" ")));

            this.S = reader.readLine().split("=")[1].strip();

            // first and last lines for productions will not contain any relevant information, we only need to check starting from the second until the second-last
            reader.readLine();
            String line = reader.readLine();
            while(line != null){
                // while we are still not at the end
                if(!line.equals("}")) {
                    String[] tokens = line.split("->");
                    String[] lhsTokens = tokens[0].split(",");
                    String[] rhsTokens = tokens[1].split("\\|");
                    //we create the left-hand side of each production
                    Set<String> lhs = new HashSet<>();
                    for(String l : lhsTokens)
                        lhs.add(l.strip());
                    if(!P.containsKey(lhs))
                        P.put(lhs,new HashSet<>());
                    //we get the right-hand side of each production
                    for(String rhsT : rhsTokens) {
                        ArrayList<String> productionElements = new ArrayList<>();
                        String[] rhsTokenElement = rhsT.strip().split(" ");
                        for(String r : rhsTokenElement)
                            productionElements.add(r.strip());
                        P.get(lhs).add(productionElements);
                    }
                }
                line = reader.readLine();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String printNonTerminals() {
        StringBuilder sb = new StringBuilder("N = { ");
        for(String n : N)
            sb.append(n).append(" ");
        sb.append("}");
        return sb.toString();
    }

    public String printTerminals() {
        StringBuilder sb = new StringBuilder("E = { ");
        for(String e : E)
            sb.append(e).append(" ");
        sb.append("}");
        return sb.toString();
    }

    public String printProductions() {
        // we will print each production by taking the pairs
        // formed from the left-hand  side and the right-hand side
        StringBuilder sb = new StringBuilder("P = { \n");
        P.forEach((lhs, rhs) -> {
            sb.append("\t");
            int count = 0;
            for(String lh : lhs) {
                sb.append(lh);
                count++;
                if(count<lhs.size())
                    sb.append(", ");
            }
            sb.append(" -> ");
            count = 0;
            for(List<String> rh : rhs){
                for(String r : rh) {
                    sb.append(r).append(" ");
                }
                count++;
                if (count < rhs.size())
                    sb.append("| ");

            }
            sb.append("\n");
        });
        sb.append("}");
        return sb.toString();
    }

    public String printProductionsForNonTerminal(String nonTerminal){
        StringBuilder sb = new StringBuilder();
        // for the given non-terminal we print the productions we have
        // based on it being found on the left-hand side
        for(Set<String> lhs : P.keySet()) {
            if(lhs.contains(nonTerminal)) {
                sb.append(nonTerminal).append(" -> ");
                Set<List<String>> rhs = P.get(lhs);
                int count = 0;
                for (List<String> rh : rhs) {
                    for(String r : rh) {
                        sb.append(r).append(" ");
                    }
                    count++;
                    if (count < rhs.size())
                        sb.append("| ");
                }
            }
        }

        return sb.toString();
    }

    //all left members have at most 1 terminal
    public boolean checkIfCFG(){
        var checkStartingSymbol = false;
        //we check if we can find at least one left-hand side containing the starting symbol
        for(Set<String> lhs : P.keySet())
            if (lhs.contains(S)) {
                checkStartingSymbol = true;
                break;
            }
        //otherwise we do not have a context free grammar
        if(!checkStartingSymbol)
            return false;

        for(Set<String> lhs : P.keySet()){
            //if our left-hand side have more than 1 nonTerminal then we do not have CFG
            if(lhs.size()>1)
                return false;
            //or if the left-hand side cannot be found between out nonTerminals
            else if(!N.contains(lhs.iterator().next()))
                return false;

            Set<List<String>> rhs = P.get(lhs);
            // for our right-hand side we just need to check if we can find it between
            // our terminals or nonTerminals
            for(List<String> rh : rhs) {
                for (String r : rh) {
                    if(!(N.contains(r) || E.contains(r) || r.equals("epsilon")))
                        return false;
                }
            }
        }
        //if all the conditions have passed then we can confirm that de do have a context free grammar
        return true;
    }

    public Set<String> getN() {
        return N;
    }

    public Set<String> getE() {
        return E;
    }

    public HashMap<Set<String>, Set<List<String>>> getP() {
        return P;
    }

    public String getS() {
        return S;
    }

    public Set<List<String>> getProductionForNonterminal(String nonTerminal) {
        for (Set<String> lhs : P.keySet()) {
            if (lhs.contains(nonTerminal)) {
                return P.get(lhs);
            }
        }
        return new HashSet<>();
    }
}