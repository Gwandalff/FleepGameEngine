package fr.diverse.team.FleepGameEngine.creator;

public class GameEvent{
	
	private final String[] commandArgs;
	private final String userId;
	private final String convId;
	
	public GameEvent(String[] commandArgs, String userId, String convId) {
		this.commandArgs = commandArgs;
		this.convId = convId;
		this.userId = userId;
	}

	public String[] getCommandArgs() {
		return commandArgs;
	}

	public String getUserId() {
		return userId;
	}

	public String getConvId() {
		return convId;
	}

}
