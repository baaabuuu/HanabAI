package dtu.AI;

import ai_actions.Action;
import dtu.hanabi_ai_game.Board;
/**
 * Generic interface for scoring BOARDS
 * @author s160902
 *
 */
public interface BoardScorer {
	public int getBoardScore(Board board, int origTurn, Action action, int currDepth, int maxDepth);
}
