package ai_actions;

public class ActionPlay implements Action {
	
	private int index;
	public ActionPlay(int index)
	{
		this.index = index;
	}
	
	public int getTarget()
	{
		return index;
	}
	
	public String play()
	{
		return "P"+index;
	}

	@Override
	public String getActionType() {
		return "P";
	}
}
