import java.util.ArrayList;
import java.util.List;

public class ProgramInternalForm {
    private List<Pair<Integer, Pair<Integer, Integer>>> PIF = new ArrayList<>();

    public void add(Integer code, Pair<Integer, Integer> value) {
        Pair<Integer, Pair<Integer, Integer>> pair = new Pair<>(code, value);
        PIF.add(pair);
    }

    @Override
    public String toString() {
        //our PIF will have the code from the language specification hash map, and then the corresponding pair from the sym tbl
        StringBuilder result = new StringBuilder();
        result.append("----------------------------------START OF PIF----------------------------------\n");
        for (Pair<Integer, Pair<Integer, Integer>> pair : PIF) {
            result.append(pair.getKey()).append(" -> (").append(pair.getValue().getKey()).append(", ").append(pair.getValue().getValue()).append(")\n");
        }
        result.append("----------------------------------END OF PIF----------------------------------\n");
        return result.toString();
    }
}