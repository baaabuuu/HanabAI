package dtu.hanabi_ai_game;

/**
 * Standard suit enum, generic way for the different types, easily accessible etc.
 * @author s164166
 *
 */
public enum SuitEnum {
	WHITE("\u001B[37mW", 0),
	RED("\u001B[1;31mR", 1),
	BLUE("\u001b[36;1mB", 2),
	YELLOW("\u001B[33;1mY", 3),
	GREEN("\u001B[32;1mG", 4);
	
	private final String colorCode;
	private final int id;
	/**
	 * Each one has a colorCode and an ID
	 * @author s164166
	 * @param colorCode
	 * @param id
	 */
	private SuitEnum(final String colorCode, final int id) { this.colorCode = colorCode;  this.id = id;}
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
	
}
