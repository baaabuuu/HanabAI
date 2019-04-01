package dtu.AI.StateTree;

import java.util.ArrayList;

import ai_actions.Action;
import dtu.hanabi_ai_game.Board;
import log.Log;
/**
 * File Author: Emil Bejder (s164161)
 * 
 * */
public class BeliefState extends StateTree {
	private double beliefWeight =0.0;
	public BeliefState(StateTree root,Action a, Board b, int rootPlayer) {
		super(root, a, b, rootPlayer);
		super.depth = root.getDepth();
		// hiTODO Auto-generated constructor stub
	}

	public void addChild(Action a) {
		//Don't accidentally call this on a belief state
		return;
	}
	public void addChild(Action a, Board b) {
		this.childern.add(new State(this, a, b, this.rootPlayer));
		
	}
	public void calculateScore() {

		
			double scoreSum =0.0;
			double childern = 0;
			double betterChildren=0;
			
			for(StateTree s : this.childern) {
				
				s.calculateScore();
				double score = s.getScore();
				

				scoreSum+= score;
				childern++;
				if(score> this.root.getScore()) betterChildren++;
			}
			//Log.important(this.action.play());
			double average = scoreSum/childern;
			setScore(((scoreSum/childern))/(getDepth()+1));
			setScoreObject(new Score(getScore(),getDepth()));
			if(betterChildren/childern == 0.0) {
				int betterThanAverage = 0;
				for(StateTree s : this.childern) {
					//Avoid off by 0.000000001 by flooring the  average
					double flooredAvg = Math.floor(average);
					if(s.getScore()>= flooredAvg) betterThanAverage++;
				}
				
				this.beliefWeight = betterThanAverage/childern;
				if(beliefWeight == 0.0);
			}else {
				this.beliefWeight = betterChildren/childern;
			}
			
		}
		
	public double getBeliefWeight() {
		return this.beliefWeight;
	}
		
		public double getScoreWeight() {
			
			
	
			
			return this.stateScoreWeight*this.getRoot().getScoreWeight();
			
			
		}
		
	

}
