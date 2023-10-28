package dev.isnow.qchecker;

public class QChecker {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("USAGE: java -jar " + System.getProperty("java.class.path").substring(System.getProperty("java.class.path").lastIndexOf(System.getProperty("path.separator")) + 1) + " check.txt");
            return;
        }
        if(args.length >= 2) {
            new QCheckerImpl(args[0], Integer.parseInt(args[1]));
        } else {
            new QCheckerImpl(args[0], -1);
        }
    }
}
