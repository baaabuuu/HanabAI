package dtu.AI;

import dtu.hanabi_ai_game.*;

public class Action {
	public GameAction action =null;
	private Card card=null;
	public int target;
	public int index;
	public int value;
	public SuitEnum suit=null;
	
	public Action(GameAction a, Card c, int player) {
		this.action = a;
		this.card = c;
		this.target = player;
	}
	public Action(GameAction a, int index) {
		this.action = a;
		this.index = index;
	}
	
	public Action(GameAction a, SuitEnum s,int player) {
		this.action = a;
		this.suit = s;
		this.target = player;
	}
	public Action(GameAction a, int value,int player) {
		this.action = a;
		this.value = value;
		this.target = player;
	}

	public String parse() {
		switch(action) {
		case PLAY:
		case DISCARD:
			return action.export()+index;		
		case HINTSUIT:
			String suit = card.getCardSuit().getSuitChar();
			return action.export()+target+suit;
		case HINTVALUE:
			int value = card.getCardValue();
			return action.export()+target+value;
		default:
			return "Unknown Action";
		} 
	}
}
