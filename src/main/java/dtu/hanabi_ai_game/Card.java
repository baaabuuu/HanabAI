package dtu.hanabi_ai_game;

import java.util.ArrayList;
import java.util.List;

/**
 * A card class that each have a suit and a value.
 * @author s164166
 *
 */
public class Card
{
	private SuitEnum suit;
	private int cardValue;
	private ArrayList<SuitEnum> possibleSuites = new ArrayList<SuitEnum>();
	private ArrayList<Integer> possibleValues = new ArrayList<Integer>();
	
	private boolean suitRevealed = false;
	private boolean valueRevealed = false;
	private boolean playable = false;
	
	/**
	 * Playable is used when eliminating possilbeSuites and Values to remove things
	 * @author s160902
	 * @return
	 */
	public boolean isPlayable()
	{
		return playable;
	}
	
	/**
	 * Marks a card as playable -  used by predictor
	 * @author s160902
	 */
	public void setPlayable()
	{
		playable = true;
	}
	
	/**
	 * Copies a card
	 * @author s164166
	 * @return
	 */
	public Card copyCard()
	{
		Card card = new Card(getCardSuit(), getCardValue());
		card.possibleSuites = new ArrayList<SuitEnum>();
		card.possibleValues = new ArrayList<Integer>();
		for (SuitEnum possibleSuit : possibleSuites)
		{
			card.possibleSuites.add(possibleSuit);
		}
		for (Integer suitValue : possibleValues)
		{
			card.possibleValues.add(new Integer(suitValue));
		}
		card.suitRevealed = suitRevealed;
		card.valueRevealed = valueRevealed;
		return card;
	}
	
	/**
	 * Reveals the suit of the card.
	 * @author s164166
	 */
	public void revealSuit()
	{
		suitRevealed = true;
	}
	
	/**
	 * Reveals the value of a card
	 * @author s164166
	 */
	public void revealValue()
	{
		valueRevealed = true;
	}
	
	/**
	 * Creates a card with a suit and a value.
	 * @author s164166
	 * 
	 * @param suit
	 * @param value
	 */
	public Card(SuitEnum suit, int value)
	{
		this.suit = suit;
		cardValue = value;
		possibleSuites.add(SuitEnum.WHITE);
		possibleSuites.add(SuitEnum.BLUE);
		possibleSuites.add(SuitEnum.GREEN);
		possibleSuites.add(SuitEnum.RED);
		possibleSuites.add(SuitEnum.WHITE);
		possibleValues.add(1);
		possibleValues.add(2);
		possibleValues.add(3);
		possibleValues.add(4);
		possibleValues.add(5);
	}
	
	/**
	 * Its not this value
	 * @author s160902
	 * @param value
	 */
	public void isNot(int value)
	{
		possibleValues.remove(value);
	}
	/**
	 * Its not this suit
	 * @author s160902
	 * @param value
	 */
	public void isNot(SuitEnum suit)
	{
		possibleSuites.remove(suit);
	}
	
	/**
	 * Check if the card is possible this value
	 * @author s160902
	 * @param value
	 * @return
	 */
	public boolean isCard(int value)
	{
		return possibleValues.contains(value);
	}
	
	/**
	 * Check if a card is possible this suit
	 * @author s160902
	 * @param suit
	 * @return
	 */
	public boolean isCard(SuitEnum suit)
	{
		return possibleSuites.contains(suit);
	}
	
	/**
	 * Get the possible values arrayList
	 * @author s160902
	 * @return
	 */
	public ArrayList<Integer> getPossibleValues()
	{
		return possibleValues;
	}
	
	/**
	 * Check if a suit is revealed
	 * @author s164166
	 * @return
	 */
	public boolean isSuitRevealed()
	{
		return suitRevealed;
	}
	
	/**
	 * Check if a value is revealed.
	 * @author s164166
	 * @return
	 */
	public boolean isValueRevealed()
	{
		return valueRevealed;
	}
	
	/**
	 * Returns the suit of the card.
	 * @author s164166
	 * @return
	 */
	public SuitEnum getCardSuit()
	{
		return suit;
	}
	
	/**
	 * Returns the value of the card.
	 * @author s164166
	 * @return
	 */
	public int getCardValue()
	{
		return cardValue;
	}
	
	/**
	 * Creates a string representation of the card.
	 * Informs people whether they are revealed or not.
	 * @author s164166
	 * @return
	 */
	public String getStringRepresentation()
	{
		StringBuilder sb = new StringBuilder();
		if (isSuitRevealed())
		{
			sb.append(suit.getColorCode() + "\u001B[0m");
			//sb.append(suit.getSuitChar());
		}
		else
		{
			sb.append("(" + suit.getColorCode() + "\u001B[0m)");
			//sb.append("(" + suit.getSuitChar()+")");
		}
		
		if (isValueRevealed())
		{
			sb.append(cardValue);
		}
		else
		{
			sb.append("(" + cardValue + ")");
		}
		return sb.toString();
	}
	
	/**
	 * Creates a string representation during your turn, used for the players own hand.
	 * @author s164166
	 * @return
	 */
	public String getStringDuringTurn()
	{
		StringBuilder sb = new StringBuilder();
		if (isSuitRevealed())
		{
			sb.append(suit.getColorCode() + "\u001B[0m");
		}
		if (isValueRevealed())
		{
			sb.append(cardValue);
		}
		if (sb.length() == 0)
		{
			sb.append("?");
		}
		return sb.toString();
	}
	

}
