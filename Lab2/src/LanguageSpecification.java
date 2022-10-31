import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LanguageSpecification {
    private final List<String> separators = Arrays.asList("(",")","[", "]","{", "}",";"," ","\n","\t");
    private final List<String> operators = Arrays.asList("+","-","*","/","%","=","==","!=","<",">","<=",">=");
    private final List<String> reservedWords = Arrays.asList("int","char","string","float","for","while","if","else","read","print");
    private final HashMap<String, Integer> langSpec = new HashMap<>();

    public LanguageSpecification() {
        buildSpecsTable();
    }

    private void buildSpecsTable() {
        //in our hash map we will add every valid token the program needs to know in order to run properly
        langSpec.put("identifier", 0);
        langSpec.put("constant", 1);

        int code = 2;
        for (String sep : separators) {
            langSpec.put(sep, code);
            code++;
        }
        for (String op : operators) {
            langSpec.put(op, code);
            code++;
        }
        for (String word : reservedWords) {
            langSpec.put(word, code);
            code++;
        }
    }
    //we build some methods to check where does a token fit
    public boolean isOperator(String token) {return operators.contains(token);}
    public boolean isPartOfOperator(char op) {
        return op == '!' || isOperator(String.valueOf(op));
    }
    public boolean isSeparator(String token) {
        return separators.contains(token);
    }
    public boolean isReservedWord(String token) {
        return reservedWords.contains(token);
    }
    public boolean isIdentifier(String token) {
        //an identifier starts with a letter, never a digit/another character
        String pattern = "^[a-zA-Z]([a-z|A-Z|0-9|_])*$";
        return token.matches(pattern);
    }

    public boolean isConstant(String token) {
        // we will use REGEX in order to verify if a token is valid

        //a number can either be 0, can start with + or - and be followed by a natural value,
        //can start directly with its natural value, can start with + or minus and be followed by a decimal value
        //or can be a decimal value with no sign in front
        String numericPattern = "^0|[1-9]([0-9])*|[+|-][1-9]([0-9])*|[1-9]([0-9])*\\.([0-9])*|[+|-][1-9]([0-9])*\\.([0-9])*$";
        // if we find a char it can either be a digit, letter or a special character
        String charPattern = "^\'[a-zA-Z0-9_?!#*./%+=<>;()\\[\\]{} ]\'";
        // a string has the same configuration as a char but it has 1 or multiple chars in it
        String stringPattern = "^\"[a-zA-Z0-9_?!#*./%+=<>;()\\[\\]{} ]+\"";
        return token.matches(numericPattern) || token.matches(charPattern) || token.matches(stringPattern);
    }

    public Integer getCode(String token) {
        return langSpec.get(token);
    }
}