package com.stephengware.java.games.chess.bot;

import com.stephengware.java.games.chess.state.*;

public class Result implements Comparable<Result> {
	
	private final State state;
	private double utility;
	
	public Result(State state) {
		this.state = state;
		utility = 0;
	}
	
	public Result(State state, double utility) {
		this.state = state;
		this.utility = utility;
	}
	
	public double getUtility() {
		return utility;
	}
	
	public void setUtility(double utility) {
		this.utility = utility;
	}
	
	public State getState() {
		return state;
	}

	@Override
	public int compareTo(Result o) {
		// TODO Auto-generated method stub
		return Double.compare(utility, o.getUtility());
	}

}
