import java.io.File;
import java.util.*;

public class FiniteAutomaton {
    public Set<String> alphabet, states, finalStates;
    public String initialState;
    public Map<Pair<String, String>, Set<String>> transitions;

    //we initialise all our fields
    public FiniteAutomaton(String filename) {
        this.states = new HashSet<>();
        this.alphabet = new HashSet<>();
        this.finalStates = new HashSet<>();
        this.transitions = new HashMap<>();

        readFiniteAutomaton(filename);
    }

    //in order to read the file, we know the format of it, so on the first line we will have the states
    //on the second line we will have the alphabet
    //then the initial state
    //and then the final states
    private void readFiniteAutomaton(String filename) {
        try {
            File file = new File(filename);
            Scanner reader = new Scanner(file);

            String statesLine = reader.nextLine();
            states = new HashSet<>(Arrays.asList(statesLine.split(" ")));

            String alphabetLine = reader.nextLine();
            alphabet = new HashSet<>(Arrays.asList(alphabetLine.split(" ")));

            initialState = reader.nextLine();

            String finalStatesLine = reader.nextLine();
            finalStates = new HashSet<>(Arrays.asList(finalStatesLine.split(" ")));
            //then we will go line by line again and break each line in 3, 2 states and the alphabet character between them
            while (reader.hasNextLine()) {
                String transitionLine = reader.nextLine();
                String[] transitionElements = transitionLine.split(" ");
                //if our states and the character are valid (already defined)
                if (states.contains(transitionElements[0]) && states.contains(transitionElements[2]) && alphabet.contains(transitionElements[1])) {
                    //we create a new transition
                    Pair<String, String> transitionStates = new Pair<>(transitionElements[0], transitionElements[1]);
                    //check if we already defined it and act accordingly
                    if (!transitions.containsKey(transitionStates)) {
                        Set<String> transitionStatesSet = new HashSet<>();
                        transitionStatesSet.add(transitionElements[2]);
                        transitions.put(transitionStates, transitionStatesSet);
                    } else {
                        transitions.get(transitionStates).add(transitionElements[2]);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //string function for the alphabet
    public String writeAlphabet() {
        StringBuilder builder = new StringBuilder();
        builder.append("Alphabet: ");
        for (String a : alphabet) {
            builder.append(a).append(" ");
        }

        return builder.toString();
    }
    //string function for the states
    public String writeStates() {
        StringBuilder builder = new StringBuilder();
        builder.append("States: ");
        for (String s : states) {
            builder.append(s).append(" ");
        }

        return builder.toString();
    }
    //string function for the final states
    public String writeFinalStates() {
        StringBuilder builder = new StringBuilder();
        builder.append("Final states: ");
        for (String fs : finalStates) {
            builder.append(fs).append(" ");
        }

        return builder.toString();
    }
    // string function for the transitions
    public String writeTransitions() {
        StringBuilder builder = new StringBuilder();
        builder.append("Transitions: \n");
        transitions.forEach((K, V) -> {
            builder.append("<").append(K.key).append(",").append(K.value).append("> -> ").append(V).append("\n");
        });

        return builder.toString();
    }
    //in order to be a DFA each transition must have a singular value
    public boolean checkIfDFA() {
        return this.transitions.values().stream().noneMatch(list -> list.size() > 1);
    }
    //we check it a sequence is valid by trying to get see if we can verify it through our given states
    public boolean checkSequence(String sequence) {
        if (sequence.length() == 0)
            return finalStates.contains(initialState);

        String state = initialState;
        for (int i = 0; i < sequence.length(); ++i) {
            Pair<String, String> key = new Pair<>(state, String.valueOf(sequence.charAt(i)));
            if (transitions.containsKey(key))
                state = transitions.get(key).iterator().next();
            else
                return false;
        }

        return finalStates.contains(state);
    }

    @Override
    public String toString() {
        return "FiniteAutomaton{" +
                "alphabet=" + alphabet +
                ", states=" + states +
                ", finalStates=" + finalStates +
                ", initialState='" + initialState + '\'' +
                ", transitions=" + transitions +
                '}';
    }
}
