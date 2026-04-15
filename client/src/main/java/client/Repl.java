package client;

import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Scanner;

public class Repl implements ServerMessageObserver {

    static String username = null;
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
                setUsername(result);
                System.out.print(result);
                System.out.println();
                if(!result.isEmpty() && !result.equals("quit")) {
                    System.out.print(printPrompt(client));
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        boolean inGameplay = client instanceof GameplayClient;
        if(!inGameplay){
            System.out.println();
        }
    }

    public void notifyClientNotification(NotificationMessage message) {
        System.out.println(message.getMessage());

    }

    public void notifyClientError(ErrorMessage message) {
        System.out.println(message.getErrorMessage());
    }


    public void notifyClientLoadMessage(LoadGameMessage message) {
        GameData data = message.getGame();
        if(client instanceof GameplayClient) {
            ((GameplayClient) client).updateGameState(message.getGame().game());
        }
        System.out.print("\n" + data.game().toString());
        if(client instanceof GameplayClient) {
            System.out.print(printPrompt(client));
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

    public void setState(ChessClient newState) {
        this.client = newState;
    }
}
