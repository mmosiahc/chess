package client;

import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

public class Repl implements ServerMessageObserver {

    private String username = null;
    private final ServerFacade facade;
    private ChessClient client;

    public Repl(String serverUrl) {
        this.facade = new ServerFacade(serverUrl, this);
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
                System.out.print(result);
                System.out.println();
                setUsername(result);
                askResign(scanner, result);
                if(!result.isEmpty() && !result.equals("quit")) {
                    System.out.print(printPrompt(client));
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public void notifyClientNotification(NotificationMessage message) {
        System.out.println(message.getMessage());

    }

    public void notifyClientError(ErrorMessage message) {
        System.out.println(message.getErrorMessage());
    }


    public void notifyClientLoadMessage(LoadGameMessage message) {
        GameData g = message.getGame();
        if(client instanceof GameplayClient gameplayClient) {
            gameplayClient.updateGameState(g.game());
            System.out.println();
            gameplayClient.redraw();
            System.out.println(printPrompt(client) + "\n");
        }
    }

    String printPrompt(ChessClient client) {
        if(client instanceof PostLoginClient || client instanceof GameplayClient) {
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
            username = result.substring(start, end);
        }
    }

    private void askResign(Scanner scanner, String result) {
        String prefix = "Are you sure";
        if(result.startsWith(prefix)) {
            String input;
            boolean response = true;
            while(response) {
                input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("y") || input.equals("yes")) {
                    break;
                } else if (input.equals("n") || input.equals("no")) {
                     response = false;
                } else {
                    System.out.println("Invalid input. Please enter 'y' or 'n'.\n");
                }
            }
            if(response) {
                client.eval("resign confirmed");
            }
        }
    }

    public void setState(ChessClient newState) {
        this.client = newState;
    }
}
