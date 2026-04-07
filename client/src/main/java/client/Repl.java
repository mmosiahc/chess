package client;

import java.util.Scanner;

public class Repl {

    private String username = null;
    private final ServerFacade facade;
    private State state = State.LOGGED_OUT;
    private ChessClient client;

    public Repl(String serverUrl) {
        this.facade = new ServerFacade(serverUrl);
        this.client = new PreLoginClient(facade);
    }

    public void run() {
        System.out.println("♕ 240 Chess Client. Type \"help\" to get started\n");
        System.out.print(printPrompt(state));

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                if(result.startsWith("You signed in")) {
                    state = State.LOGGED_IN;
                    setUsername(result);
                    client = new PostLoginClient(facade);
                }
                System.out.print(result);
                System.out.println();
                System.out.print(printPrompt(state));
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    String printPrompt(State state) {
        if(state == State.LOGGED_IN || state == State.IN_GAME) {
            return "[" + username + "] >>> ";
        }
        return "[" + state.toString() + "] >>> ";
    }

    void setUsername(String result) {
        this.username = result.substring("You signed in as ".length());
    }
}
