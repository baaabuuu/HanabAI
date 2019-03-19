package dtu.AI;

public class AI {
	private Strategy s;
	
	public AI(Strategy s){
		this.s = s;
	} 

	public String play(int depth) {
		return s.search(depth).parse();
	}
	public String taunt() {
		return "kill all humans";
	}
}
