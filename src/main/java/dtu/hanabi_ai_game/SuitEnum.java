package dtu.hanabi_ai_game;

/**
 * Standard suit enum, generic way for the different types, easily accessible etc.
 * @author s164166
 *
 */
public enum SuitEnum {
	WHITE("\u001B[37mW", 0,"W"),
	RED("\u001B[1;31mR", 1,"R"),
	BLUE("\u001b[36;1mB", 2,"B"),
	YELLOW("\u001B[33;1mY", 3,"Y"),
	GREEN("\u001B[32;1mG", 4,"G");
	
	private final String colorCode;
	private final int id;
	private final String suitChar;
	/**
	 * Each one has a colorCode and an ID
	 * @author s164166
	 * @param colorCode
	 * @param id
	 */
	private SuitEnum(final String colorCode, final int id,final String suitChar) { this.colorCode = colorCode;  this.id = id;this.suitChar = suitChar;}
	/**
	 * Returns the colorcode of the card + the letter indicating it
	 * @author s164166
	 * @return
	 */
	public String getColorCode() { return colorCode; }
	/**
	 * Returns the ID of the enum.
	 * @author s164166
	 * @return
	 */
	public int getID() { return id;};
	public String getSuitChar() {return this.suitChar;}
	
	public static SuitEnum fromInteger(int x) 
	{
        switch(x) {
        case 0:
            return WHITE;
        case 1:
            return RED;
        case 2:
            return BLUE;
        case 3:
            return YELLOW;
        case 4:
            return GREEN;
        }
        return null;
    }
}
