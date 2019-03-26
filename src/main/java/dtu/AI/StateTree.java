package dtu.AI;

import java.util.ArrayList;

import dtu.hanabi_ai_game.Board;

public class StateTree {
	private Boolean isRoot = false;
	private Board parent= null;
	private Board state;
	private double score;
	private ArrayList<StateTree> childern = new ArrayList<StateTree>();
	public StateTree(Board parentState,Board state) {
		
		if(parentState == null) {
			isRoot = true;
		}else {
			parent = parentState;
		}
		this.state = state;
	}
	public Board getParent() {
		return parent; //returns null if root
		
	}
	public Board getState() {
		return this.state;
	}
	public StateTree getChild(int index){
		return childern.get(index);
		
	}
	public void addChild(Board state) {
		childern.add(new StateTree(this.state,state));
		
	}
	public Boolean isRoot() {
		return isRoot;
	}
}
