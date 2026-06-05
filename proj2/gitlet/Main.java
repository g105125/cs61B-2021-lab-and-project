package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.init();
                break;
            case "add":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.add(args[1]);
                break;
            case "rm":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.rm(args[1]);
                break;
            case "commit":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.commit(args[1]);
                break;
            case "log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.log();
                break;
            case "checkout":
                if (args.length == 3) {
                    Repository.checkout1(args[2]);
                    break;
                }
                if (args.length == 2) {
                    Repository.checkout3(args[1]);
                    break;
                }
                if (args.length == 4) {
                    Repository.checkout2(args[1], args[3]);
                    break;
                }
                System.out.println("Incorrect operands.");
                break;
            case "global-log":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.globalLog();
                break;
            case "find":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.find(args[1]);
                break;
            case "status":
                if (args.length != 1) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.status();
                break;
            case "branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.reset(args[1]);
                break;
            case "merge":
                if (args.length != 2) {
                    System.out.println("Incorrect operands.");
                    break;
                }
                Repository.merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}