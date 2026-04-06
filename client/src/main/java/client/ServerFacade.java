package client;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import service.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String url;

    public ServerFacade(String url) {
        this.url = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws Exception {
        var request = buildRequest("POST", "/user", registerRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest loginRequest) throws Exception {
        var request = buildRequest("POST", "/session", loginRequest, null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(String token) throws Exception {
        var request = buildRequest("DELETE", "/session", null, token);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws Exception {
        var request = buildRequest("POST", "/game", createGameRequest, createGameRequest.authToken());
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    public HashMap listGames(String token) throws Exception {
        var request = buildRequest("GET", "/game", null, token);
        var response = sendRequest(request);
        return handleResponse(response, HashMap.class);
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
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}
