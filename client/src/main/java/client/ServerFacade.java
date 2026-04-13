package client;

import com.google.gson.Gson;
import data_transfer.*;
import com.google.gson.reflect.TypeToken;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.GameData;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.UserGameCommand;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;
    private String token;
    private final WebsocketCommunicator ws;

    public ServerFacade(String url, ServerMessageObserver observer) {
        this.url = url;
        this.ws = new WebsocketCommunicator(url, observer);
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        RegisterResult result = handleResponse(response, RegisterResult.class);
        assert result != null;
        token = result.authToken();
        return result;
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        LoginResult result = handleResponse(response, LoginResult.class);
        assert result != null;
        token = result.authToken();
        return result;
    }

    public void logout() throws Exception {
        var request = buildRequest("DELETE", "/session", null, token);
        var response = sendRequest(request);
        handleResponse(response, null);
        token = null;
    }

    public CreateGameResult createGame(String gameName) throws Exception {
        CreateGameBody body = new CreateGameBody(gameName);
        var request = buildRequest("POST", "/game", body, token);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public Map<String, Collection<GameData>> listGames() throws Exception {
        var request = buildRequest("GET", "/game", null, token);
        var response = sendRequest(request);
        return handleResponse(response, Map.class);
    }

    public void joinGame(JoinGameBody body, String username) throws Exception {
        var request = buildRequest("PUT", "/game", body, token);
        var response = sendRequest(request);
        handleResponse(response, null);
        ConnectCommand connect = new ConnectCommand(UserGameCommand.CommandType.CONNECT, token, body.gameID(), username, body.playerColor());
        ws.playerJoins(connect);
    }

    public void clear() throws Exception {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void sendLeaveCommand(String username, Integer gameID) {
        LeaveCommand leave = new LeaveCommand(UserGameCommand.CommandType.LEAVE, token, gameID, username);
        ws.playerLeaves(leave);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String token) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-type", "application/json");
        }
        if (token != null) {
            request.setHeader("authorization", token);
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if(request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }


    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception("Failed to send request");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                var bodyFromJson = new Gson().fromJson(body, Map.class);
                String exceptionMessage = bodyFromJson.get("message").toString();
                switch (exceptionMessage) {
                    case "Error: bad request" -> throw new BadRequestException();
                    case "Error: already taken" -> throw new AlreadyTakenException();
                    case "Error: unauthorized" -> throw new UnauthorizedException();
                    default -> throw new DataAccessException(exceptionMessage);
                }
            }

            throw new DataAccessException("other failure: " + response.statusCode());
        }

        if (responseClass != null) {
            if (Map.class.isAssignableFrom(responseClass)) {
                Type type = new TypeToken<Map<String, Collection<GameData>>>() {
                }.getType();
                return new Gson().fromJson(response.body(), type);
            }
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    public void setToken(String testToken) {
        token = testToken;
    }

}
