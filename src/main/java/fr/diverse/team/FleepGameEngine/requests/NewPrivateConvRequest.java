package fr.diverse.team.FleepGameEngine.requests;

import java.util.Arrays;

public class NewPrivateConvRequest extends NewConvRequest {

	private static final long serialVersionUID = -3642125086511844165L;

	public NewPrivateConvRequest(String ticket, String accountId, String convName) {
		super(ticket, Arrays.asList(new String[] {accountId}), convName );
	}

}
