package dtu.AI;
import dtu.hanabi_ai_game.SuitEnum;
public enum GameAction {
	PLAY("P"),
	HINTSUIT("H"),
	HINTVALUE("H"),
	DISCARD("D");
	private String shorthand;
	private GameAction(String s) {
		this.shorthand = s;
	}
 	public String export() {return this.shorthand;}


}
