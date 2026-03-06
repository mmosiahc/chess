package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;


public class ClearService {
    private static MemoryUserDAO userMemory;
    private static MemoryAuthDAO authMemory;
    private static MemoryGameDAO gameMemory;

    public ClearService(MemoryUserDAO userMemory, MemoryAuthDAO authMemory, MemoryGameDAO gameMemory) {
        ClearService.userMemory = userMemory;
        ClearService.authMemory = authMemory;
        ClearService.gameMemory = gameMemory;
    }

    public static void clear() throws DataAccessException {
        try {
            userMemory.clear();
            authMemory.clear();
            gameMemory.clear();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }

    }
}
