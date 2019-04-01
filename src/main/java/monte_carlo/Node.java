package monte_carlo;

import java.util.ArrayList;
import java.util.List;

public class Node {
	List<State> states;
	State avgState;
	Node parent;
	List<Node> childArray;
	
	
	public Node(Node node) {
		this.parent = node.getParent();
		childArray = new ArrayList<>();
		childArray.addAll(node.childArray);
		states = new ArrayList<>();
		for(State tempState: node.getAllStates()){
			states.add(new State(tempState));	
		}
		this.avgState = new State(node.getState());
	}

	private List<State> getAllStates() {
		return states;
	}

	public Node(List<State> state2, int playNo) {
		states = new ArrayList<>();
		for(int i = 0; i < state2.size(); i++){
			state2.get(i).setNodeIndex(i);
			state2.get(i).setPlayerNo(playNo);
			states.add(state2.get(i));
		}
		childArray = new ArrayList<>();
		this.avgState = new State(state2.get(0));
	}

	public List<Node> getChildArray() {
		return childArray;
	}

	public State getState() {
		int randomState =(int) ((states.size()-1)*Math.random());
		return states.get(randomState);
	}
	
	public State getState(int ind) {
		return states.get(ind);
	}
	
	public Node getParent() {
		return parent;
	}

	public void setParent(Node node) {
		parent = node;
	}

	public Node getRandomChildNode() {
		int noOfChildren = childArray.size()-1;
		int randomChild = (int) (noOfChildren*Math.random()); 
		List<Node> tempArray = getChildArray();
		return tempArray.get(randomChild);
	}

	public Node getChildWithMaxScore() {
		int maxChild = 0;
		List<Node> tempArray = getChildArray();
		for(int i = 1; i < childArray.size(); i++){
			if(tempArray.get(i).getAvgState().getVisitCount() > tempArray.get(maxChild).getAvgState().getVisitCount()){
				maxChild = i;
			}
		}
		return tempArray.get(maxChild);
	}

	public State getAvgState() {
		return avgState;
	}

	public List<Node> getChildArray(int index) {
		return childArray;
	}
	
}
