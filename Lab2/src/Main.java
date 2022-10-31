public class Main {
    public static void main(String[] args) {
//        SymbolTable symTbl = new SymbolTable(5);
//        symTbl.addToTable("ana");
//        symTbl.addToTable("2");
//        symTbl.addToTable("7");
//        System.out.println(symTbl);
//        System.out.println(symTbl.canFind("2"));
//        symTbl.deleteFromTable("2");
//        System.out.println(symTbl.canFind("2"));
//
//        System.out.println(symTbl);

        System.out.println();
        System.out.println("Program 1");
        MyScanner scannerP1 = new MyScanner("src/files/p1.txt",
                "src/files/out1.txt");
        scannerP1.scan();

        System.out.println("------------------------");
        System.out.println("Program 2");
        MyScanner scannerP2 = new MyScanner("src/files/p2.txt",
                "src/files/out2.txt");
        scannerP2.scan();


        System.out.println("------------------------");
        System.out.println("Program 3");
        MyScanner scannerP3 = new MyScanner("src/files/p3.txt",
                "src/files/out3.txt");
        scannerP3.scan();

        System.out.println("------------------------");
        System.out.println("Program with lexical errors");
        MyScanner scannerP1err = new MyScanner("src/files/p1err.txt",
                "src/files/out1err.txt");
        scannerP1err.scan();
    }
}
