package service;

import dataaccess.*;


public class ClearService {
    private static UserDatabase users;
    private static AuthDatabase authentications;
    private static GameDatabase games;

    public ClearService(UserDatabase users, AuthDatabase authentications, GameDatabase games) {
        ClearService.users = users;
        ClearService.authentications = authentications;
        ClearService.games = games;
    }

    public void clear() throws DataAccessException {
        try {
            users.clear();
            authentications.clear();
            games.clear();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }

    }
}
