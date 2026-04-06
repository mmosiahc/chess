package client;

public class PreloginClient implements ChessClient{
    private final ServerFacade facade;

    public PreloginClient(ServerFacade facade) {
        this.facade = facade;
    }

    public String eval(String input) {
        return null;
    }

    public String help() {
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create account");
        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("quit - to exit this chess application");
        System.out.println("help - to show options");
    }
}
