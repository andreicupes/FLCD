import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyScanner {
    private final LanguageSpecification languageSpecification = new LanguageSpecification();
    private final ProgramInternalForm programInternalForm = new ProgramInternalForm();
    private final int lenSymbolTable = 25;
    private final SymbolTable symbolTable = new SymbolTable(lenSymbolTable);

    private final String programFile;
    private final String output;

    public MyScanner(String programFile, String output) {
        this.programFile = programFile;
        this.output = output;
    }

    public void scan() {
        List<Pair<String, Integer>> tokenPairs = new ArrayList<>();
        try {
            File file = new File(programFile);
            Scanner reader = new Scanner(file);
            //we parse the file line by line, tokenizing and populating our tables
            int lineNr = 1;
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                List<String> tokens = tokenize(line);
                for (String token : tokens) {
                    tokenPairs.add(new Pair<>(token, lineNr));
                }
                lineNr++;
            }
            reader.close();
            createProgramInternalForm(tokenPairs);
            printToFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> tokenize(String line) {
        ArrayList<String> tokens = new ArrayList<>();

        for (int i = 0; i < line.length(); i++) {
            //if our character is a separator then we add it as it is
            if (languageSpecification.isSeparator(String.valueOf(line.charAt(i))) && !(String.valueOf(line.charAt(i))).equals(" ")) {
                tokens.add(String.valueOf(line.charAt(i)));
            }
            //we need to add a string to the tokens
            else if (line.charAt(i) == '\"') {
                String constant = identStringConst(line, i);
                tokens.add(constant);
                i += constant.length() - 1;
            }
            //we need to check if + is an operator or a constant
            //then add it to the tokens
            else if (line.charAt(i) == '+') {
                String token = identPlus(line, i, tokens);
                tokens.add(token);
                i += token.length() - 1;
            }
            //we need to check if - is an operator or a constant and then add it to the tokens
            else if (line.charAt(i) == '-') {
                String token = identMinus(line, i, tokens);
                tokens.add(token);
                i += token.length() - 1;
            }
            //we need to add a char to the tokens
            else if (line.charAt(i) == '\'') {
                String constant = identStringConst(line, i);
                tokens.add(constant);
                i += constant.length() - 1;
            }
            // if we hit this case it means that our character can be part of an operator so we check the lang. specs.
            else if (languageSpecification.isPartOfOperator(line.charAt(i))) {
                String operator = identOper(line, i);
                tokens.add(operator);
                i += operator.length() - 1;
            }
            //we need to identify the token and then add it to our list
            else if (line.charAt(i) != ' ') {
                String token = identTok(line, i);
                tokens.add(token);
                i += token.length() - 1;
            }
        }
        return tokens;
    }

    public String identStringConst(String line, int position) {
        StringBuilder constant = new StringBuilder();
        //for it to be a string/char constant we build it until we hit a separator/operator/end of line
        for (int i = position; i < line.length(); i++) {
            if ((languageSpecification.isSeparator(String.valueOf(line.charAt(i))) ||
                    languageSpecification.isOperator(String.valueOf(line.charAt(i)))) &&
                    ((i == line.length() - 2 && line.charAt(i + 1) != '\"') || (i == line.length() - 1)))
                break;
            constant.append(line.charAt(i));
            if (line.charAt(i) == '\"' && i != position)
                break;
        }

        return constant.toString();
    }


    public String identMinus(String line, int position, ArrayList<String> tokens) {
        //if minus is preceded by an identifier or constant then it is an operator
        if (languageSpecification.isIdentifier(tokens.get(tokens.size() - 1)) ||
                languageSpecification.isConstant(tokens.get(tokens.size() - 1))) {
            return "-";
        }

        //if minus is preceded by operator or separator, then we have a numeric constant to which - belongs to
        StringBuilder token = new StringBuilder();
        token.append('-');

        for (int i = position + 1; i < line.length() && (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.'); i++) {
            token.append(line.charAt(i));
        }
        return token.toString();
    }

    public String identPlus(String line, int position, ArrayList<String> tokens) {
        //plus is preceded by an identifier, constant -> plus is an operator
        if (languageSpecification.isIdentifier(tokens.get(tokens.size() - 1)) ||
                languageSpecification.isConstant(tokens.get(tokens.size() - 1))) {
            return "+";
        }

        //plus is preceded by operator or separator -> assign a positive number or condition with positive number -> plus belongs to a numeric constant
        StringBuilder token = new StringBuilder();
        token.append('+');

        for (int i = position + 1; i < line.length() && (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.'); i++) {
            token.append(line.charAt(i));
        }

        return token.toString();
    }

    public String identOper(String line, int position) {
        StringBuilder operator = new StringBuilder();
        operator.append(line.charAt(position));
        operator.append(line.charAt(position + 1));
        // we can check the operator directly into the hash map containing the language specification
        if (languageSpecification.isOperator(operator.toString()))
            return operator.toString();

        return String.valueOf(line.charAt(position));
    }

    public String identTok(String line, int position) {
        StringBuilder token = new StringBuilder();
        //a token is valid as long as it s not a separator or a part of an operator
        for (int i = position; i < line.length()
                && !languageSpecification.isSeparator(String.valueOf(line.charAt(i)))
                && !languageSpecification.isPartOfOperator(line.charAt(i))
                && line.charAt(i) != ' '; i++) {
            token.append(line.charAt(i));
        }

        return token.toString();
    }

    public void createProgramInternalForm(List<Pair<String, Integer>> tokens) {
        List<String> invalidTokens = new ArrayList<>();
        boolean isLexicallyCorrect = true;
        for (Pair<String, Integer> tokenPair : tokens) {
            String token = tokenPair.getKey();
            //if the token is part of the token table(language specification) then it is not in the sym tbl
            if (languageSpecification.isOperator(token) ||
                    languageSpecification.isReservedWord(token) ||
                    languageSpecification.isSeparator(token)) {
                int code = languageSpecification.getCode(token);
                programInternalForm.add(code, new Pair<>(-1, -1));
            } else if (languageSpecification.isIdentifier(token)) {
                //if we have an identifier we add it to the symbol table and then the pair from there to the pif
                symbolTable.addToTable(token);
                Pair<Integer, Integer> position = symbolTable.getPosition(token);
                programInternalForm.add(0, position);
            } else if (languageSpecification.isConstant(token)) {
                //if we have a constant we add it to the symbol table and then the pair from there to the pif
                symbolTable.addToTable(token);
                Pair<Integer, Integer> position = symbolTable.getPosition(token);
                programInternalForm.add(1, position);
            } else if (!invalidTokens.contains(token)) {
                //we have an invalid token
                invalidTokens.add(token);
                isLexicallyCorrect = false;
                System.out.println("LEXICAL ERROR! at line " + tokenPair.getValue() + ": invalid token " + token);
            }
        }

        if (isLexicallyCorrect) {
            System.out.println("Program is lexically correct");
        }
    }

    public void printToFiles() {
        try {
            File outFile = new File(output);
            FileWriter outFileWriter = new FileWriter(outFile, false);
            BufferedWriter outWriter = new BufferedWriter(outFileWriter);
            outWriter.write(programInternalForm.toString());
            outWriter.write(symbolTable.toString());
            outWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}