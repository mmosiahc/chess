package client;

import datatransfer.LoginRequest;
import datatransfer.LoginResult;
import datatransfer.RegisterRequest;
import datatransfer.RegisterResult;

import java.util.Arrays;

public class PreLoginClient implements ChessClient{
    private final ServerFacade facade;
    private final Repl repl;

    public PreLoginClient(ServerFacade facade, Repl repl) {
        this.facade = facade;
        this.repl = repl;
    }

    public String eval(String input) {

        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(cmd.startsWith("-")) {
                return switch (cmd) {
                    case "-r" -> register(params);
                    case "-l" -> login(params);
                    case "-q" -> "quit";
                    case "-h" -> help();
                    default -> failed(cmd);
                };
            }else {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    case "help" -> help();
                    default -> failed(cmd);
                };
            }

        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) {
        if(params.length != 3) {
            return "Expected <username> <password> <email>\n";
        }
        try {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = facade.register(request);
            repl.setState(new PostLoginClient(facade, repl, result.username()));
            return String.format("You signed in as %s\n", result.username());
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String login(String... params) {
        if(params.length != 2) {
            return "Expected <username> <password>\n";
        }
        try {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = facade.login(request);
            repl.setState(new PostLoginClient(facade, repl, result.username()));
            return String.format("You signed in as %s\n", result.username());
        } catch (Exception e) {
            return e.getMessage() + "\n";
        }
    }

    public String help() {
        return String.format("%-45s | %s%n", "Command", "Description") +
                "-".repeat(67) + "\n" +
                String.format("%-45s | %s%n", "register <USERNAME> <PASSWORD> <EMAIL> (-r)", "Create a new account") +
                String.format("%-45s | %s%n", "login <USERNAME> <PASSWORD> (-l)", "Login to your account") +
                String.format("%-45s | %s%n", "quit (-q)", "Exit the application") +
                String.format("%-45s | %s%n", "help (-h)", "Show these options again");
    }

    public String failed(String failedCommand) {
        return String.format("Expected <Command> got \"%s\" | (-h) for help\n", failedCommand);
    }
}
