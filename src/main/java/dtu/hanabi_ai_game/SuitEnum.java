package dtu.hanabi_ai_game;

public enum SuitEnum {
	WHITE("\u001B[37mW", 0),
	RED("\u001B[1;31mR", 1),
	BLUE("\u001b[36;1mB", 2),
	YELLOW("\u001B[33;1mY", 3),
	GREEN("\u001B[32;1mG", 4);
	
	private final String colorCode;
	private final int id;
	
	private SuitEnum(final String colorCode, final int id) { this.colorCode = colorCode;  this.id = id;}
	public String getColorCode() { return colorCode; }
	public int getID() { return id;};
	
}
