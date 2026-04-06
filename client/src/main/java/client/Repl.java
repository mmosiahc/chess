package client;

public class Repl {

    private String username = null;
    private final ServerFacade facade;
    private State state = State.SIGNEDOUT;
    private final ChessClient client;

    public Repl(String serverUrl) {
        this.facade = new ServerFacade(serverUrl);
        this.client = new PreloginClient(facade);
    }

    public void run() {
        System.out.println("♕ 240 Chess Client");
        System.out.print(client.help());
    }
}
