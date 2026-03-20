package service;

import chess.ChessGame;

public record JoinGameRequest(String authToken, ChessGame.TeamColor playerRequestColor, int gameID) {}
