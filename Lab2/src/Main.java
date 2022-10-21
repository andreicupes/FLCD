public class Main {
    public static void main(String[] args) {
        SymbolTable symTbl = new SymbolTable(5);
        symTbl.addToTable("ana");
        symTbl.addToTable("2");
        symTbl.addToTable("7");
        System.out.println(symTbl);
        System.out.println(symTbl.canFind("2"));
        symTbl.deleteFromTable("2");
        System.out.println(symTbl.canFind("2"));

        System.out.println(symTbl);


    }
}
