package com.stephengware.java.games.chess.bot;

import java.util.*;

import com.stephengware.java.games.chess.state.*;



/**
 * A chess bot which selects its next move at random.
 * 
 * @author Stephen G. Ware
 */
public class MyBot extends Bot {

	/** A priority queue for the future nodes */
	private PriorityQueue<Result> results;
	
	/**
	 * Constructs a new chess bot named "My Chess Bot" and whose random  number
	 * generator (see {@link java.util.Random}) begins with a seed of 0.
	 */
	public MyBot() {
		super("eacastr1");
	}
	
	@Override
	protected State chooseMove(State root) {
		results = createPriorityQueue(root.player);
		return findMinMaxIterativeDeepening(root, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 2).getState();
	}
	
	private Result findMinMaxIterativeDeepening(State r, double alpha, double beta, int maxDepth) {
		State root = r;
		Iterator<State> itr = root.next().iterator();
		Result best = null;
		int currentDepth = 1;
		while(!root.searchLimitReached() && itr.hasNext()) {
			State next = itr.next();
			
			Result current;
			if(r.player == Player.WHITE) {
				current = findMinItrDeep(next, alpha, beta, currentDepth + 1, maxDepth);
			} else {
				current = findMaxItrDeep(next, alpha, beta, currentDepth + 1, maxDepth);
			}
			
			results.add(current);
			
			if(!itr.hasNext()) {
				best = results.poll();
				itr = root.next().iterator();
				results = createPriorityQueue(r.player);
				maxDepth = maxDepth + 2;
			}
		}
		
		if(best != null) {
			return best;
		} 
		best = results.poll();
		return best;
	}
	
	private Result findMaxItrDeep(State s, double alpha, double beta, int currentDepth, int maxDepth) {
		if(s.over && s.check || currentDepth > maxDepth) {
			return new Result(s, Utility.evaluateBoard(s));
		}
		Iterator<State> itr = s.next().iterator();
		// We are returning/rating the best possible score of THIS state.
		Result best = new Result(s);
		double max = Double.NEGATIVE_INFINITY;
		while(!s.searchLimitReached() && itr.hasNext()) {
			Result result = findMinItrDeep(itr.next(), alpha, beta, currentDepth + 1, maxDepth);
			
			max = Math.max(max, result.getUtility());
			
			if(max >= beta)
				break;
			alpha = Math.max(alpha, max);
		}
		// Prevents the state from being evaluated as an infinite value.
		if(max != Double.NEGATIVE_INFINITY)
			best.setUtility(max);
		return best;
	} 
	
	private Result findMinItrDeep(State s, double alpha, double beta, int currentDepth, int maxDepth) {
		if(s.over && s.check || currentDepth > maxDepth) {
			return new Result(s, Utility.evaluateBoard(s));
		}
		Iterator<State> itr = s.next().iterator();
		Result best = new Result(s);
		double min = Double.POSITIVE_INFINITY;
		while(!s.searchLimitReached() && itr.hasNext()) {
			Result result = findMaxItrDeep(itr.next(), alpha, beta, currentDepth + 1, maxDepth);
			
			min = Math.min(min, result.getUtility());
			
			if(min <= alpha)
				break;
			beta = Math.min(beta, min);
		}
		// Prevents the state from being evaluated as an infinite value.
		if(min != Double.POSITIVE_INFINITY) 
			best.setUtility(min);
		return best;
	}
	
	private PriorityQueue<Result> createPriorityQueue(Player player) {
		if(player == Player.WHITE) {
			return new PriorityQueue<>(Collections.reverseOrder());
			// return findMax(root, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, root.turn + 1).getState();
		} else {
			return new PriorityQueue<>();
			// return findMin(root, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, root.turn + 1).getState();
		}
	}
}
