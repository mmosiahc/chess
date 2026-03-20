package service;

import chess.ChessGame;

public record JoinGameBody(ChessGame.TeamColor playerRequestColor, int gameID) {}
