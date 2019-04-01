package dtu.AI.StateTree;

import java.util.ArrayList;

import ai_actions.Action;
import ai_actions.ActionDiscard;
import ai_actions.ActionHint;
import ai_actions.ActionPlay;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;
import log.Log;

public class PlayHelper {
	/**
	 * File Author: Emil Bejder (s164161)
	 * 
	 * */

	
	public static ArrayList<Action> getPossibleActions(Board b, int currentPlayer) {
		
		ArrayList<Action> actions = new ArrayList<Action>();
		//We can always choose to play any card in our hand
		for (int j = 0; j < 4; j++) {
			
			actions.add(new ActionPlay(j));
		
		}
		if(b.getClueTokens() < 8) {
			for (int j = 0; j < 4; j++) {
				
				actions.add(new ActionDiscard(j));
				
			}
			
			
		}
		
		if (b.getClueTokens() > 0) {
			for (int i = 0; i < b.getPlayerHands().size(); i++) {
				// We can only hint players that are not ourselves

				// For a given player we only consider valid hints i.e. don't consider hinting
				// about green if a player has no green
				//Also don't ive duplicate hints
				Boolean[] suits = { false, false, false, false, false };
				Boolean[] values = { false, false, false, false, false };
				// Handle offset
				if (i != currentPlayer) {
					b.getPlayerHands().get(i).forEach((x) -> {
						if(!x.isSuitRevealed()) suits[x.getCardSuit().getID()] = true;
						
						if(!x.isValueRevealed()) values[x.getCardValue() - 1] = true;
				});

					// Itterate the possiblites
					for (int k = 0; k < 5; k++) {
						if (suits[k]) {
							actions.add(new ActionHint(i, getEnumFromId(k).getSuitChar()));
						}
						if (values[k]) {
							actions.add(new ActionHint(i, String.valueOf(k+1)));
						}
					}

				}
			}
		}
		return actions;
		
	}
	
	

	
	

	
	

	
	
	private static SuitEnum getEnumFromId(int id) {

		switch (id) {
		case 0:
			return SuitEnum.WHITE;
		case 1:
			return SuitEnum.RED;
		case 2:
			return SuitEnum.BLUE;
		case 3:
			return SuitEnum.YELLOW;
		case 4:
			return SuitEnum.GREEN;
		default:
			return null;
		}

	}
}
