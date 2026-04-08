package service;

import Exceptions.DataAccessException;
import dataaccess.*;


public class ClearService {
    private final UserDatabase users;
    private final AuthDatabase authentications;
    private final GameDatabase games;

    public ClearService(UserDatabase users, AuthDatabase authentications, GameDatabase games) {
        this.users = users;
        this.authentications = authentications;
        this.games = games;
    }

    public void clear() throws DataAccessException {
        users.clear();
        authentications.clear();
        games.clear();
    }
}
