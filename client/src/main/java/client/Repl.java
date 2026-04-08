package client;

import ui.DrawChessBoard;

import java.util.Scanner;

public class Repl {

    private String username = null;
    private final ServerFacade facade;
    private ChessClient client;

    public Repl(String serverUrl) {
        this.facade = new ServerFacade(serverUrl);
        this.client = new PreLoginClient(facade, this);
    }

    public void run() {
        System.out.println("♕ 240 Chess Client. Type \"help\" to get started\n");
        System.out.print(printPrompt(client));

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while(!result.equals("quit")) {
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                setUsername(result);
                System.out.print(result);
                System.out.println();
                if(result.startsWith("You joined")) {
                    boolean isWhite = result.endsWith("WHITE\n");
                    boolean isObserver = result.endsWith("observer\n");
                    if(isWhite) {
                        DrawChessBoard.main(true);
                    } else if (isObserver) {
                        DrawChessBoard.main(true);
                    }
                }
                if(!result.equals("quit")) {
                    System.out.print(printPrompt(client));
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    String printPrompt(ChessClient client) {
        if(client instanceof PostLoginClient) {
            return "[" + username + "] >>> ";
        }
        return "[LOGGED_OUT] >>> ";
    }

    private void setUsername(String result) {
        String prefix = "You signed in as ";
        if(result.startsWith(prefix)) {
            String suffix = "\n";
            int start = result.indexOf(prefix) + prefix.length();
            int end = result.indexOf(suffix);
            this.username = result.substring(start, end);
        }
    }

    public void setState(ChessClient newState) {
        this.client = newState;
    }
}
