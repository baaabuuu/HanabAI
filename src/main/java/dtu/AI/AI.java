package dtu.AI;


public class AI
{	
	private Strategy s;
	
	public AI(Strategy s){
		this.s = s;
	}
	
	public String play(int maxDepth) 
	{
		return s.search(maxDepth);
	}
}
