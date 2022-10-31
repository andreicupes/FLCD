import java.util.ArrayList;

public class SymbolTable {
    private final int length;
    private final ArrayList<ArrayList<String>> hashTable;

    public SymbolTable(int length) {
        this.length = length;
        this.hashTable = new ArrayList<>();
        for(int i=0; i<length; i++) this.hashTable.add(new ArrayList<>());
    }

    private int hash(String key) {
        int sum = 0;
        for(int i=0; i< key.length(); i++) sum += key.charAt(i);
        return sum % length;
    }

    public boolean addToTable(String key){
        int hashValue = hash(key);
        if(!hashTable.get(hashValue).contains(key)){
            hashTable.get(hashValue).add(key);
            return true;
        }
        return false;
    }

    public boolean canFind(String key){
        int hashValue = hash(key);
        return hashTable.get(hashValue).contains(key);
    }

    public int getLength() {
        return length;
    }

    public Pair<Integer, Integer> getPosition(String key){
        if (this.canFind(key)){
            int listPos = this.hash(key);
            int listIndex = 0;
            for(String el:this.hashTable.get(listPos)) {
                if (!el.equals(key))
                    listIndex++;
                else
                    break;
            }
            return new Pair<>(listPos, listIndex);
        }
        return new Pair<>(-1, -1);
    }

    public boolean deleteFromTable(String key){
        int hashValue = hash(key);
        if(hashTable.get(hashValue).contains(key))  {
            hashTable.get(hashValue).remove(key);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("----------------------------------START OF SYMBOL TABLE----------------------------------\n");
        for (int i=0; i<length; i++) {
            result.append(i).append(": {");
            String separator = "";
            for(String item: hashTable.get(i)){
                result.append(separator);
                separator = ", ";
                result.append(item);
            }
            result.append("}\n");
        }
        result.append("----------------------------------END OF SYMBOL TABLE----------------------------------\n");
        return result.toString();
    }
}
