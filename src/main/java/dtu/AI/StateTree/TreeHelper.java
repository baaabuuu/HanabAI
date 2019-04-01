package dtu.AI.StateTree;

import java.util.ArrayList;
import java.util.HashMap;

import ai_actions.Action;
import ai_actions.ActionDiscard;
import ai_actions.ActionPlay;
import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import log.Log;
/**
 * File Author: Emil Bejder (s164161)
 * 
 * */
public class TreeHelper {

	public static ArrayList<Board> getBoardStatesFromAction(Action a, Board board, int rootPlayer, int depth) {
		Board b = board.copyState();
		int currentPlayer = ((rootPlayer + depth) % b.getPlayerHands().size());
		if ((a instanceof ActionPlay) || (a instanceof ActionDiscard)) {
			ArrayList<Card> potentialCards = getPossibleCardsFromAction(a, b, rootPlayer, currentPlayer);

			return buildStatesFromOutcomes(a, b, potentialCards, currentPlayer);
		} else {

			ArrayList<Board> singleton = new ArrayList<Board>();
			singleton.add(createNewBoardStateWithHint(a, b, rootPlayer));
			return singleton;
		}

	}

	private static Board createNewBoardStateWithHint(Action a, Board b, int currentPlayer) {
		Board board = b.copyState();
		return getBoardFromAction(a, board, currentPlayer);

	}

	private static int getHandIndexOfCard(Action a) {
		// Helper
		return Character.getNumericValue(a.play().charAt(1));
	}

	private static ArrayList<Card> getPossibleCardsFromAction(Action a, Board b, int rootPlayer, int currentPlayer) {
		// driver for filterPossiblities
		ArrayList<Card> hand = b.getPlayerHand(currentPlayer);
		Card c = hand.get(getHandIndexOfCard(a));
		return filterPossibilities(getUnknowns(b, rootPlayer, currentPlayer), c);
	}

	public static ArrayList<Card> getUnknowns(Board b, int rootPlayer, int currentPlayer) {
		// Helper
		ArrayList<Card> unknowns = new ArrayList<Card>();
		unknowns.addAll(b.getDeck());
		unknowns.addAll(b.getPlayerHand(rootPlayer));
		if (rootPlayer != currentPlayer) {
			unknowns.addAll(b.getPlayerHand(currentPlayer));
		}
		return unknowns;
	}

	public static ArrayList<Card> filterPossibilities(ArrayList<Card> unknowns, Card c) {
		ArrayList<Card> temp = new ArrayList<Card>();
		temp.addAll(unknowns);
		// Predicate function: If suit or value is revealed and the current card does
		// not match, remove it.
		temp.removeIf(s -> (c.isSuitRevealed() && !s.getCardSuit().equals(c.getCardSuit()))
				|| (c.isValueRevealed() && !(s.getCardValue() == c.getCardValue())));

		return temp;

	}

	private static ArrayList<Board> buildStatesFromOutcomes(Action a, Board b, ArrayList<Card> possibleCards,
			int currentPlayer) {
		ArrayList<Board> boards = new ArrayList<Board>();

		for (Card c : possibleCards) {
			boards.add(createNewBoardStateWithSwappedCard(a, b, c, currentPlayer));
		}

		return boards;
	}

	private static Board createNewBoardStateWithSwappedCard(Action a, Board b, Card c, int currentPlayer) {
		//Note that getBoardFromAction also creates a new copy of the boardState
		Board board = b.copyState();

		swapCardInHandToDeck(getHandIndexOfCard(a), board, c, currentPlayer);
		

		return getBoardFromAction(a, board, currentPlayer);

	}

	private static void swapCardInHandToDeck(int handIndex, Board b, Card newCard, int currentPlayer) {
		ArrayList<Card> hand = b.getPlayerHand(currentPlayer);
		Card oldCard = b.getPlayerHand(currentPlayer).get(handIndex);
		ArrayList<Card> newDeck = new ArrayList<Card>();

		newDeck.addAll(b.getDeck());
		newDeck.add(oldCard);
		newDeck.remove(newCard);

		if (oldCard.isSuitRevealed())
			newCard.revealSuit();
		if (oldCard.isValueRevealed())
			newCard.revealValue();

		hand.set(handIndex, newCard);

		b.setDeck(newDeck);
		b.setPlayerHand(currentPlayer, hand);
	}


	// We only deal in valid actions
	private static Board getBoardFromAction(Action a, Board board, int player) {
		Board b = board.copyState();
		int index = getHandIndexOfCard(a); //Only to be used in ActionPlay and ActionDiscard
		switch (a.getActionType()) {
		case "D": // Discard Action

			Card card = b.getPlayerHand(player).remove(index);

			card.revealSuit();
			card.revealValue();
			b.discardCard(card);

			if (b.getClueTokens() < 8) b.addClueToken();
			if (b.getDeckSize() > 0) {
				b.drawCard(player);
				if (b.getDeckSize() == 0) {
					// finalRound = player;
					//Log.log("We reached the final round but there is no way to do anything about it from the board");
				}
			}
			return b;
		case "H":
			int hintRecipient = a.getTarget();
			b.removeClueToken();
			HashMap<Character,Integer> suitToId = buildSuitToIdHashMap();

			char identifier = a.play().charAt(2);
			int parsedIdentifier = Character.getNumericValue(identifier);
			if (parsedIdentifier > 0) // We were able to parse the identifier, it must be a number
			{

				int charValue = parsedIdentifier;
				for (Card c : b.getPlayerHand(hintRecipient))
				{
					if (c.getCardValue() == charValue)
					{
						c.revealValue();
					}
				}
				return b;
			}
			else 
			{
				int id = suitToId.get(identifier); //Use hashmap instead ofwhatever the heck the other thing was

				for (Card c : b.getPlayerHand(hintRecipient))
				{
					if (c.getCardSuit().getID() == id)
					{
						c.revealSuit();
					}
				}
				return b;
			}
		case "P":
				Card c = b.getPlayerHand(player).remove(index);
				c.revealSuit();
				c.revealValue();
				int cardSuite = c.getCardSuit().getID();
				if (b.getTopCard(cardSuite)+1 == c.getCardValue())
				{
					b.playCard(c, cardSuite);
					b.addPoint();
					if (c.getCardValue() == 5 && b.getClueTokens() < 8)
					{
						//Log.important("A state somewhere added token");
						b.addClueToken();
					
					}
				}
				else
				{
					b.discardCard(c);
					b.removeLife();

				}

				if (b.getDeckSize() > 0)
				{
					b.drawCard(player);
					if (b.getDeckSize() == 0)
					{
						
					//Final round?	finalRound = player;
					}
				}
				return b;
		default:
			Log.important("Error: Action not known");
			return b;
		}

}

private static HashMap<Character, Integer> buildSuitToIdHashMap() {
	// TODO Auto-generated method stub
	HashMap<Character,Integer> suitToId = new HashMap<Character,Integer>();
	suitToId.put('W', 0);
	suitToId.put('R', 1);
	suitToId.put('B', 2);
	suitToId.put('Y', 3);
	suitToId.put('G', 4);
	return suitToId;
}

}
