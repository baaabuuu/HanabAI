package dtu.AI.StateTree;

import ai_actions.Action;
import dtu.hanabi_ai_game.Board;
/**
 * File Author: Emil Bejder (s164161)
 * 
 * */
public class State extends StateTree {
	public State(StateTree root, Action a, Board b,int rootPlayer) {
		super(root, a, b, rootPlayer);
		super.depth = root.getDepth()+1;
	
		// TODO Auto-generated constructor stub
	}

	
	
public void calculateScore() {
	setScoreObject(ScoreHelper.calculateStateScore(this.rootPlayer, this.gameState, this.depth));

		setScore((this.scoreObject.getSum()/getDepth()));
		
		
	}
}
