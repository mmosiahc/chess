package client;

import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;

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
                    default -> help(cmd);
                };
            }else {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help(cmd);
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
            repl.setState(new PostLoginClient(facade, repl));
            return String.format("You signed in as %s\n", result.username());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String login(String... params) {
        if(params.length != 2) {
            return "Expected <username> <password>\n";
        }
        try {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = facade.login(request);
            repl.setState(new PostLoginClient(facade, repl));
            return String.format("You signed in as %s\n", result.username());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String help(String failedCommand) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-45s | %s%n", "Command", "Description"));
        sb.append("-".repeat(67)).append("\n");

        sb.append(String.format("%-45s | %s%n", "register <USERNAME> <PASSWORD> <EMAIL> (-r)", "Create a new account"));
        sb.append(String.format("%-45s | %s%n", "login <USERNAME> <PASSWORD> (-l)", "Login to your account"));
        sb.append(String.format("%-45s | %s%n", "quit (-q)", "Exit the application"));
        sb.append(String.format("%-45s | %s%n", "help (-h)", "Show these options again"));

        if(failedCommand.equalsIgnoreCase("help") || failedCommand.equalsIgnoreCase("-h")) {
            return sb.toString();
        }
        sb.append("\nExpected <Command> got \"" + failedCommand + "\"\n");

        return sb.toString();
    }

}
