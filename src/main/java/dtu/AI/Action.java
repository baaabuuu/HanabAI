package dtu.AI;

import dtu.hanabi_ai_game.*;

public class Action {
	private GameAction action;
	private Card card;
	private int target;
	private int index;
	
	public Action(GameAction a, Card c, int player) {
		this.action = a;
		this.card = c;
		this.target = player;
	}
	public Action(GameAction a, int index) {
		this.action = a;
		this.index = index;
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
