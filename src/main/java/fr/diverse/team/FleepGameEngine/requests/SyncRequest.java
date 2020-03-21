package fr.diverse.team.FleepGameEngine.requests;

public class SyncRequest extends BaseRequest {

	public SyncRequest(String ticket) {
		super("account/sync", ticket);
	}
}
