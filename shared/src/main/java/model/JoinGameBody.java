package model;

import chess.ChessGame;

public record JoinGameBody(ChessGame.TeamColor playerColor, int gameID) {}
