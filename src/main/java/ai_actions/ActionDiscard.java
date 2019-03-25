package ai_actions;

public class ActionDiscard implements Action {
	private int index;
	public int getTarget()
	{
		return index;
	}
	public ActionDiscard(int index)
	{
		this.index = index;
	}
	
	public String play()
	{
		return "D"+index;
	}

	@Override
	public String getActionType() {
		return "D";
	}
}
