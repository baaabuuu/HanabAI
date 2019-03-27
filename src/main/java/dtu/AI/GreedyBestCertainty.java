package dtu.AI;

import java.util.ArrayList;

import ai_actions.Action;
import ai_actions.MoveWrapper;
import dtu.hanabi_ai_game.Board;


public class GreedyBestCertainty implements Strategy {
	private Board b;
	private int aiplayer;
	@Override
	public String search(int depth) {
		// TODO Auto-generated method stub
		return null;
	}

	public GreedyBestCertainty(int aiplayer,Board b) {
		this.aiplayer = aiplayer;
		this.b = b;
	}
	
	private MoveWrapper generateMoves(int depth) {
		
		MoveGeneratorGreedyCertainty generator = new MoveGeneratorGreedyCertainty();
		
		MoveWrapper w = new MoveWrapper(null,this.b);
		generator.generateMoves(w, depth, aiplayer, aiplayer, null);
		return w;
	}
	
	
}
