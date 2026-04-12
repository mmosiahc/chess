package client;

public class WebsocketCommunicator {
    private final ServerMessageObserver messageObserver;

    public WebsocketCommunicator(ServerMessageObserver observer) {
        this.messageObserver = observer;
    }
}
