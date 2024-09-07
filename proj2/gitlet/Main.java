package gitlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Suiren
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            throw new GitletException("Please enter a command.");
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                validateArgs(args, 1);
                Repository.init();
                break;
            case "add":
                validateArgs(args, 2);
                Repository.add(args[1]);
                break;
            case "commit":
                validateArgs(args, 2);
                Repository.commit(args[1]);
                break;
            case "rm":
                validateArgs(args, 2);
                Repository.rm(args[1]);
                break;
            case "log":
                validateArgs(args, 1);
                Repository.log();
                break;
            case "global-log":
                validateArgs(args, 1);
                Repository.globalLog();
                break;
            case "find":
                validateArgs(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validateArgs(args, 1);
                Repository.status();
                break;
            case "checkout":
                if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                } else if (args.length == 3 && args[1].equals("--")) {
                    Repository.checkoutFile(args[2]);
                } else if (args.length == 4 && args[2].equals("--")) {
                    Repository.checkoutFileFromCommit(args[1], args[3]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "branch":
                validateArgs(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                validateArgs(args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                validateArgs(args, 2);
                Repository.reset(args[1]);
                break;
            case "merge":
                break;
            default:
                throw new GitletException("No command with that name exists.");
        }
    }

    /**
     * check the num of args except checkout
     * @param args the command to be validated
     * @param argNum the expected num of args
     */
    private static void validateArgs(String args[], int argNum) {
        if (args.length != argNum) {
            throw new GitletException("Incorrect operands.");
        }
    }
}
