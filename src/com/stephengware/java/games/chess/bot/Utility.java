package com.stephengware.java.games.chess.bot;

import com.stephengware.java.games.chess.state.*;

public class Utility {
	
	// THIS IS THE IMPORTANT PART OF AI, CREATING INCENTIVES
	
	private static int[][] pawnMap = {
		    {0,  0,  0,  0,  0,  0,  0,  0},
		    {5, 10, 10,-20,-20, 10, 10,  5},
		    {5,-5,-10,  0,  0,-10,-5,  5},
		    {0,  0,  0, 20, 20,  0,  0,  0},
		    {5,  5, 10, 25, 25, 10,  5,  5},
		    {10, 10, 20, 30, 30, 20, 10, 10},
		    {50, 50, 50, 50, 50, 50, 50, 50},
		    {0,  0,  0,  0,  0,  0,  0,  0}
	};
	private static int[][] knightMap = {
			{-50,-40,-30,-30,-30,-30,-40,-50},
			{-40,-20,  0,  0,  0,  0,-20,-40},
			{-30,  0, 10, 15, 15, 10,  0,-30},
			{-30,  5, 15, 20, 20, 15,  5,-30},
			{-30,  0, 15, 20, 20, 15,  0,-30},
			{-30,  5, 10, 15, 15, 10,  5,-30},
			{-40,-20,  0,  5,  5,  0,-20,-40},
			{-50,-40,-30,-30,-30,-30,-40,-50}
	};
	private static int[][] bishopMap = {
		    {-20,-10,-10,-10,-10,-10,-10,-20},
		    {-10,  5,  0,  0,  0,  0,  5,-10},
		    {-10, 10, 10, 10, 10, 10, 10,-10},
		    {-10,  0, 10, 10, 10, 10,  0,-10},
		    {-10,  5,  5, 10, 10,  5,  5,-10},
		    {-10,  0,  5, 10, 10,  5,  0,-10},
		    {-10,  0,  0,  0,  0,  0,  0,-10},
		    {-20,-10,-10,-10,-10,-10,-10,-20}
	};
	private static int[][] rookMap = {
		    { 0,  0,  0,  5,  5,  0,  0,  0},
		    {-5,  0,  0,  0,  0,  0,  0, -5},
		    {-5,  0,  0,  0,  0,  0,  0, -5},
		    {-5,  0,  0,  0,  0,  0,  0, -5},
		    {-5,  0,  0,  0,  0,  0,  0, -5},
		    {-5,  0,  0,  0,  0,  0,  0, -5},
		    { 5, 10, 10, 10, 10, 10, 10,  5},
		    { 0,  0,  0,  0,  0,  0,  0,  0}
	};
	
	private static int[][] queenMap = {
		    {-20,-10,-10, -5, -5,-10,-10,-20},
		    {-10,  0,  5,  0,  0,  0,  0,-10},
		    {-10,  5,  5,  5,  5,  5,  0,-10},
		    { -5,  0,  5,  5,  5,  5,  0, -5},
		    {  0,  0,  5,  5,  5,  5,  0, -5},
		    {-10,  0,  5,  5,  5,  5,  0,-10},
		    {-10,  0,  0,  0,  0,  0,  0,-10},
		    {-20,-10,-10, -5, -5,-10,-10,-20}
	};
	
	private static int[][] midKingMap = {
		    {20, 30, 10,  0,  0, 10, 30, 20},
		    {20, 20,  0,  0,  0,  0, 20, 20},
		    {-10,-20,-20,-20,-20,-20,-20,-10},
		    {-20,-30,-30,-40,-40,-30,-30,-20},
		    {-30,-40,-40,-50,-50,-40,-40,-30},
		    {-30,-40,-40,-50,-50,-40,-40,-30},
		    {-30,-40,-40,-50,-50,-40,-40,-30},
		    {-30,-40,-40,-50,-50,-40,-40,-30}
	};
	
	private static int[][] endKingMap = {
		    {-50,-30,-30,-30,-30,-30,-30,-50},
		    {-30,-30,  0,  0,  0,  0,-30,-30},
		    {-30,-10, 20, 30, 30, 20,-10,-30},
		    {-30,-10, 30, 40, 40, 30,-10,-30},
		    {-30,-10, 30, 40, 40, 30,-10,-30},
		    {-30,-10, 20, 30, 30, 20,-10,-30},
		    {-30,-20,-10,  0,  0,-10,-20,-30},
		    {-50,-40,-30,-20,-20,-30,-40,-50}
	};
	
	/*
	 *
	 */
	public static double evaluateBoard(State state) {
		double currentScore = 0;
		
		// Reward check mate.
		if(state.over && state.check) {
			if(state.player == Player.WHITE) {
				return -1_000_000;
			} else {
				return 1_000_000;
			}
		}
		
		// Avoid risking stale mate if this state is one move away from a draw or avoid stale mate entirely.
		if((state.previous.movesUntilDraw == 2 && state.movesUntilDraw == 1) || (state.over && !state.check)) {
			return 0;
		}
		
		// Reward check but only slightly
		if(state.check && (state.player == Player.WHITE)) {
			return -50;
		} else if(state.check && (state.player == Player.BLACK)) {
			return 50;
		}
		
		// Evaluate the pieces on the board
		for(Piece piece : state.board) {
			if(piece.player == Player.WHITE) {
				currentScore += evaluatePiece(piece, state);
			}
			else {
				currentScore -= evaluatePiece(piece, state);
			}
		}
		
		return currentScore;
	}
	
	/**
	 * Evaluates piece by their score and positioning.
	 * @param piece The piece to be evaluated.
	 * @param state The current state of the board.
	 * @return The piece evaluation score.
	 */
	private static double evaluatePiece(Piece piece, State state) {
		int rank = piece.rank;
		// Reverse for black pieces.
		if(piece.player == Player.BLACK) {
			rank = (pawnMap[0].length-1) - rank;
		}
		
		if(piece.getClass() == Pawn.class) {
			return 100 + pawnMap[rank][piece.file];
		}
		else if(piece.getClass() == Knight.class) {
			return 300 + knightMap[rank][piece.file];
		}
		else if(piece.getClass() == Bishop.class) {
			return 300 + bishopMap[rank][piece.file];
		}
		else if(piece.getClass() == Rook.class) {
			return 500 + rookMap[rank][piece.file];
		}
		else if(piece.getClass() == Queen.class) {
			return 900 + queenMap[rank][piece.file];
		}
		else if(piece.getClass() == King.class && state.turn <= 30) {
			return midKingMap[rank][piece.file];
		} 
		else if(piece.getClass() == King.class && state.turn > 30) {
			return endKingMap[rank][piece.file];
		}
		return 0;
	}
}
