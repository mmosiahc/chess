package client;

import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
//                if(result.startsWith("You joined")) {
//                    boolean isWhite = result.endsWith("WHITE\n");
//                    boolean isObserver = result.endsWith("observer\n");
//                    if(isWhite) {
//                        DrawChessBoard.main(true);
//                    } else if (isObserver) {
//                        DrawChessBoard.main(true);
//                    } else {
//                        DrawChessBoard.main(false);
//                    }
//                }
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

    public void notifyClient(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case ERROR -> printServerErrorMessage((ErrorMessage) message);
            case NOTIFICATION -> printNotification((NotificationMessage) message);
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
        }
    }

    public void printServerErrorMessage(ErrorMessage message) {
        System.out.print(message.getErrorMessage() + "\n");
    }

    public void printNotification(NotificationMessage message) {
        System.out.print(message.getMessage() + "\n");
    }

    public void loadGame(LoadGameMessage message) {
        GameData data = message.getGame();
        GameplayClient.gameData = message.getGame();
        boolean isWhite = username.equals(data.whiteUsername());
//        DrawChessBoard.drawBoardFromGame(data.game(), isWhite);
        System.out.print(data.game().toString());
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
