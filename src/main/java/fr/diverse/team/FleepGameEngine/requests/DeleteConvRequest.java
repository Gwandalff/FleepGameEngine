package fr.diverse.team.FleepGameEngine.requests;

public class DeleteConvRequest extends BaseRequest {

	private static final long serialVersionUID = 897249001755844790L;

	public DeleteConvRequest(String ticket, String convId) {
		super("conversation/delete/"+convId, ticket);
	}

}
