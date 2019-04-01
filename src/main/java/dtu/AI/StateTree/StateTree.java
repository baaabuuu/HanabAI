package dtu.AI.StateTree;

import java.util.ArrayList;

import ai_actions.Action;
import ai_actions.ActionDiscard;
import ai_actions.ActionPlay;
import dtu.hanabi_ai_game.Board;
import log.Log;
/**
 * File Author: Emil Bejder (s164161)
 * 
 * */
public class StateTree {
	private boolean isRoot = true;
	protected Score scoreObject = null;
	protected StateTree root;
	protected ArrayList<StateTree> childern = new ArrayList<StateTree>();
	protected int depth = 0;
	protected int childCount = 0;
	protected Action action;
	protected Board gameState;
	protected  int rootPlayer;
	
	protected double stateScore =0;
	protected double stateScoreWeight =1.0;
	public StateTree(StateTree root, Action a, Board b, int rootPlayer) {
		this.root = root;
		this.action = a;
		this.gameState = b;
		this.rootPlayer=rootPlayer;
	}
	public boolean isRoot() {
		return isRoot;
		
	}
	
	public void addChild(StateTree c) {
		this.childern.add(c);
	}
	public void addChild(Action a) {
		//always work on copies of states
		ArrayList<Board> resultStates = TreeHelper.getBoardStatesFromAction(a, this.gameState.copyState(), rootPlayer, depth);
	
		// If we have more than one possible move we make a belief state
		if(resultStates.size()> 1) {
			
			//BeliefStates have the same boardState as root.
			BeliefState bState = new BeliefState(this, a, this.gameState.copyState(), this.rootPlayer);
			for(Board b: resultStates) {
				bState.addChild(a,b);
			}
			
			this.childern.add(bState);
			//remember that constructor automatically increments depth
			
		} else {
			//Be aware that we assume the resulting States to always have 1 or more.
			//Add normal state
			this.childern.add(new State(this, a, resultStates.get(0), this.rootPlayer));
			
		}
			
	}
	
	public int getDepth() {
		return this.depth;
	}

	public Board getGameState() {
		return this.gameState;
		
	}
	public StateTree getRoot() {
		return this.root;
		
	}
	public Action getAction() {
		return action;
		
	}
	public ArrayList<StateTree> getChildern(){
		return this.childern;
	}
	public double getScore() {
		// TODO Auto-generated method stub
		return this.stateScore ;
	}
	
	public void setScoreWeight(double w) {
		this.stateScoreWeight = w;
	}
	
	protected void setScore(double score) {
		this.stateScore = score;
	}
	
	public void calculateScore() {
		setScoreObject(ScoreHelper.calculateStateScore(this.rootPlayer, this.gameState, this.depth));
		setScore(this.scoreObject.getSum());
		
		
	}
	public double getBeliefWeight() {
		return 1.0;
	}
	public double getFlatScoreWeight(){
		return this.stateScoreWeight;
	}
	
	//recursivly get the score weight
	public double getScoreWeight() {
		// TODO Auto-generated method stub
		if(this.root == null) {
			return 1.0;
		}
		//Log.log("Score weight: "+this.stateScoreWeight);
		return this.stateScoreWeight*getRoot().getScoreWeight();
	}
	public int getRootPlayer() {
		return this.rootPlayer;
	}
	public Score getScoreObject() {
		return scoreObject;
	}
	public void setScoreObject(Score scoreObject) {
		this.scoreObject = scoreObject;
	}
}
