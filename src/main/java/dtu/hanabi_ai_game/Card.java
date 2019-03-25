package dtu.hanabi_ai_game;


/**
 * A card class that each have a suit and a value.
 * @author s164166
 *
 */
public class Card
{
	private SuitEnum suit;
	private int cardValue;
	
	private boolean suitRevealed = false;
	private boolean valueRevealed = false;
	/**
	 * Copies a card
	 * @author s164166
	 * @return
	 */
	public Card copyCard()
	{
		Card card = new Card(getCardSuit(), getCardValue());
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
