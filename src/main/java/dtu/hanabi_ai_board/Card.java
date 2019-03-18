package dtu.hanabi_ai_board;

public class Card
{
	private SuitEnum suit;
	private int cardValue;
	
	private boolean suitRevealed = false;
	private boolean valueRevealed = false;
	
	public Card copyCard()
	{
		Card card = new Card(getCardSuit(), getCardValue());
		card.suitRevealed = suitRevealed;
		card.valueRevealed = valueRevealed;
		return card;
	}
	
	public Card(SuitEnum suit, int value)
	{
		this.suit = suit;
		cardValue = value;
	}
	
	public boolean isSuitRevealed()
	{
		return suitRevealed;
	}
	
	public boolean valueRevealed()
	{
		return valueRevealed;
	}
	
	
	public SuitEnum getCardSuit()
	{
		return suit;
	}
	public int getCardValue()
	{
		return cardValue;
	}
	
	public String getStringRepresentation()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(suit.getColorCode());
		sb.append(cardValue);
		sb.append( "\u001B[0m");
		return sb.toString();
	}
}
