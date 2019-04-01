package dtu.AI;

import ai_actions.MoveWrapper;
/**
 * Move generator generates a series of possible moves for the AI to take and expand its state space based upon
 * @author s164166
 *
 */
public interface MoveGenerator
{
	public void generateMoves(MoveWrapper wrapper, int maxDepth, int currPlayer, int origPlayer, Predictor predictor);

}
