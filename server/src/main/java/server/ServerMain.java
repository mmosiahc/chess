package server;

import dataaccess.DataAccessException;

import static dataaccess.DatabaseManager.createDatabase;

public class ServerMain {
    public static void main(String[] args) throws DataAccessException {
        createDatabase();
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}