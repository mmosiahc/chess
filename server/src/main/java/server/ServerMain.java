package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

public class ServerMain {
    public static void main(String[] args) throws DataAccessException {

        Server server = new Server();
        server.run(8080);

        // Create chess database
        DatabaseManager.createDatabase();

        System.out.println("♕ 240 Chess Server");
    }
}