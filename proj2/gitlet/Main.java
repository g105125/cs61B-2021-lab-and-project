package gitlet;

import java.io.IOException;


/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args){
        // TODO: what if args is empty?
        if(args.length==0){
            System.out.println("Please enter a command.");
            return;
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                if(args.length!=1){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Init();
                // TODO: handle the `init` command
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Add(args[1]);
                break;
            case "rm":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Rm(args[1]);
                break;
            case "commit":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Commit(args[1]);
                break;
            case "log":
                if(args.length!=1){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Log();
                break;
            case "checkout":
               if(args.length==3){
                   gitlet.Repository.Checkout1(args[2]);
                   break;
               }
               if(args.length==2){
                   gitlet.Repository.Checkout3(args[1]);
                   break;
               }
               if(args.length==4){
                   gitlet.Repository.Checkout2(args[1],args[3]);
                   break;
               }
                System.out.println("Incorrect operands.");
                break;
            case "global-log":
                if(args.length!=1){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Global_log();
                break;
            case "find":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Find(args[1]);
                break;
            case "status":
                if(args.length!=1){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Status();
                break;
            case "branch":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Branch(args[1]);
                break;
            case "rm-branch":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Rm_branch(args[1]);
                break;
            case "reset":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Reset(args[1]);
                break;
            case "merge":
                if(args.length!=2){
                    System.out.println("Incorrect operands.");
                    break;
                }
                gitlet.Repository.Merge(args[1]);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
    }
}
