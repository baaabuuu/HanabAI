package dtu.AI;

import java.util.ArrayList;

import dtu.hanabi_ai_game.Board;
import dtu.hanabi_ai_game.Card;
import dtu.hanabi_ai_game.SuitEnum;
import log.Log;

public class PredictorAdvanced implements Predictor {

	private SuitEnum[] suits = {SuitEnum.WHITE, SuitEnum.RED, SuitEnum.BLUE, SuitEnum.YELLOW, SuitEnum.GREEN};

	
	/**
	 * Finds out which cards havent been played/ no information is given about.
	 * @author s164166
	 * @param playerhands
	 * @param playerTurn - the one whose turn is.
	 * @param origPlayer - the one that initiated the chain
	 * @return
	 */
	public ArrayList<Card> generatePotentialCards(ArrayList<ArrayList<Card>> playerhands, int playerTurn, int origPlayer, Board board)
	{
		ArrayList<Card> potentialCards = board.generateDeck();
		for (Card card : board.getPlayedCards())
		{
			if (card.isSuitRevealed() && card.isValueRevealed())
			{
				potentialCards.remove(card);
			}
		}
		int playerCount = playerhands.size();
		for (int i = 0; i < playerCount; i++)
		{
			if (i != origPlayer && i != playerTurn)
			{
				potentialCards.removeAll(playerhands.get(i));
			}
			else
			{					
				for (Card card : playerhands.get(i))
				{
					if (card.isSuitRevealed() && card.isValueRevealed())
					{
						potentialCards.remove(card);
					}
				}
			}
		}
		return potentialCards;
	}
	

	@Override
	public ArrayList<Card> predict(ArrayList<ArrayList<Card>> hands, int turn, int origTurn, Board board) {
		ArrayList<Card> attempt = new ArrayList<Card>();
		for (Card card : hands.get(turn))
		{
				attempt.add(card.copyCard());
		}
		ArrayList<Card> potentialCards = generatePotentialCards(hands, turn, origTurn, board);
		while (true)
		{
			boolean guess = false;
			for (int i = 0; i < attempt.size(); i++) {
				Card card = attempt.get(i);
				guess = guessSuitOrValue(card, potentialCards);
				if (guess)
				{
					break;
				}
			}
			//if we have less than or equal to 10, we can make a guess about the suits left.
			if (suitsLeftInHand(attempt) && potentialCards.size() <= 10) {
				guess = predictSuite(potentialCards, attempt);
				if (guess)
				{
					break;
				}
			}
			//if there are less than 3 cards, we can make a guess about the values left.
			if (valueLeftInHand(attempt) && potentialCards.size() <= 3) {
				guess = predictValue(potentialCards, attempt);
				if (guess)
				{
					break;
				}
			}

			if (!guess) {
				break;
			}
		}
		
		int[] suitCopies = {0,0,0,0,0};
		int[] numberCopies = {0,0,0,0,0};
		boolean isEverythingPlayable = true;
		int count = 0;
		for (Card card : potentialCards)
		{
			suitCopies[card.getCardSuit().getID()]++;
			numberCopies[card.getCardValue()-1]++;
			if (isEverythingPlayable && card.getCardValue() != board.getFireworkStacks()[card.getCardSuit().getID()] + 1)
			{
				if (board.getLife() > count)
				{
					isEverythingPlayable = false;
				}
				count++;
			}
		}
		
		
		for (Card card : attempt)
		{
			if (isEverythingPlayable)
			{
				card.setPlayable();
			}
			if (!card.isValueRevealed())
			{
				for (int i = 0; i < 5; i++)
				{
					if (numberCopies[i] == 0)
					{
						card.isNot(i);
					}
				}
			}
			if (!card.isSuitRevealed())
			{
				for (int i = 0; i < 5; i++)
				{
					if (suitCopies[i] == 0)
					{
						card.isNot(suits[i]);
					}
				}
			}
		}
		return attempt;
	}

	private boolean predictValue(ArrayList<Card> potentialCards, ArrayList<Card> attempt) {
		int guessNumber = 0;
		for (Card cardGuess : potentialCards) {
			if (guessNumber == 0) {
				guessNumber = cardGuess.getCardValue();
			} else if (guessNumber != cardGuess.getCardValue()) {
				guessNumber = 0;
				return false;
			}
		}
			for (Card iteratingCard : attempt) {
				iteratingCard.revealValue();
				if (iteratingCard.isSuitRevealed()) {
					potentialCards.remove(iteratingCard);
				}
			}
			for (Card iteratingCard : attempt) {
				iteratingCard.revealValue();
			}
		return true;
	}

	private boolean predictSuite(ArrayList<Card> potentialCards, ArrayList<Card> attempt) {
		int guessSuit = -1;
		for (Card cardGuess : potentialCards) {
			if (guessSuit == -1) {
				guessSuit = cardGuess.getCardSuit().getID();
			} else if (guessSuit != cardGuess.getCardSuit().getID()) {
				return false;
			}
		}

		for (Card iteratingCard : potentialCards) {
			iteratingCard.revealSuit();
			if (iteratingCard.isValueRevealed()) {
				potentialCards.remove(iteratingCard);
			}
		}
		for (Card iteratingCard : attempt) {
			iteratingCard.revealSuit();
		}
		return true;
		
	}

	/**
	 * Checks whether any card in a hand has not had its suit revealed
	 * @author s164166
	 * @param hand
	 * @return
	 */
	private boolean suitsLeftInHand(ArrayList<Card> hand)
	{
		return hand.parallelStream().anyMatch(card -> !card.isSuitRevealed());
	}
	
	/**
	 * Checks whether any card in a hand has not had its value revealed
	 * @author s164166
	 * @param hand
	 * @return
	 */
	private boolean valueLeftInHand(ArrayList<Card> hand)
	{
		return hand.parallelStream().anyMatch(card -> !card.isValueRevealed());
	}
	
	
	/**
	 * Guess about the cards suit or value if the suit or the value has no information, but there is information about the other.
	 * @author s164166
	 * @param card
	 * @param potentialCards
	 * @return
	 */
	private boolean guessSuitOrValue(Card card, ArrayList<Card> potentialCards) {
		if (card.isSuitRevealed() && !card.isValueRevealed()) {
			return makeGuessNumber(card, potentialCards);
		} else if (card.isValueRevealed() && !card.isSuitRevealed()) {
			return makeGuessSuit(card, potentialCards);
		}
		return false;
	}

	/**
	 * Guess about the cards suit
	 * @author s164166
	 * @param card
	 * @param potentialCards
	 * @return
	 */
	private boolean makeGuessSuit(Card card, ArrayList<Card> potentialCards) {
		int guessSuit = -1;
		for (Card cardGuess : potentialCards) {
			if (cardGuess.getCardValue() == card.getCardValue() && guessSuit == -1) {
				guessSuit = cardGuess.getCardSuit().getID();

			} else if (cardGuess.getCardValue() == card.getCardValue()
					&& guessSuit != cardGuess.getCardSuit().getID()) {
				return false;
			}
		}
		potentialCards.remove(card);
		card.revealValue();
		Log.important("Predicted the card " + card.getStringRepresentation());
		return true;
	}

	/**
	 * Guess about the cards number
	 * @author s164166
	 * @param card
	 * @param potentialCards
	 * @return
	 */
	private boolean makeGuessNumber(Card card, ArrayList<Card> potentialCards) {
		int guessNumber = 0;
		for (Card cardGuess : potentialCards) {
			if (cardGuess.getCardSuit().equals(card.getCardSuit()) && guessNumber == 0) {
				guessNumber = cardGuess.getCardValue();

			} else if (cardGuess.getCardSuit().equals(card.getCardSuit())
					&& guessNumber != cardGuess.getCardValue()) {
				return false;
			}
		}
		potentialCards.remove(card);
		card.revealValue();
		Log.important("Predicted the card " + card.getStringRepresentation());
		return true;
	}

}
