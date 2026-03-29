package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;

public class ServerMain {
    public static void main(String[] args) throws DataAccessException {
        // Create chess database
        DatabaseManager.createDatabase();
        // Create users table
//        String createUserTableStatement = """
//                CREATE TABLE users (
//                id INT NOT NULL AUTO_INCREMENT,
//                username VARCHAR(255) NOT NULL,
//                password VARCHAR(255) NOT NULL,
//                email VARCHAR(255) NOT NULL,
//                PRIMARY KEY (id)
//                )
//                """;
//        DatabaseManager.createTable(createUserTableStatement);
//        // Create authentications table
//        String createAuthTableStatement = """
//                CREATE TABLE authentications (
//                id INT NOT NULL AUTO_INCREMENT,
//                authtoken VARCHAR(255) NOT NULL,
//                username VARCHAR(255) NOT NULL,
//                PRIMARY KEY (id)
//                )
//                """;
//        DatabaseManager.createTable(createUserTableStatement);
//        // Create games table
//        String createGameTableStatement = """
//                CREATE TABLE IF NOT EXIST games (
//                id INT NOT NULL AUTO_INCREMENT,
//                white_username VARCHAR(255) DEFAULT NULL,
//                black_usernameVARCHAR(255) DEFAULT NULL,
//                game_name VARCHAR(255) NOT NULL,
//                game longtext NOT NULL,
//                PRIMARY KEY (id)
//                )
//                """;
//        DatabaseManager.createTable(createUserTableStatement);
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}