package ai_actions;

public class ActionPlay implements Action {
	private int index;
	public ActionPlay(int index)
	{
		this.index = index;
	}
	
	public String play()
	{
		return "P"+index;
	}
}
