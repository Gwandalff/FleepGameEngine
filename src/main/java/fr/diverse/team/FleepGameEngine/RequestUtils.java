package fr.diverse.team.FleepGameEngine;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import fr.diverse.team.FleepGameEngine.requests.DeleteConvRequest;
import fr.diverse.team.FleepGameEngine.requests.NewConvRequest;
import fr.diverse.team.FleepGameEngine.requests.NewPrivateConvRequest;
import fr.diverse.team.FleepGameEngine.requests.SendMessageRequest;

public class RequestUtils {
	
	private HttpHelper httpHelper;
	private String ticket;
	
	public RequestUtils(HttpHelper httpHelper, String ticket, String myAccountId) {
		this.httpHelper = httpHelper;
		this.ticket = ticket;
	}
	
	/**
	 * messages are received in XML format.
	 * Plain text should be sent back.
	 * @return
	 */
	public String parseXmlToText(String message) {
		System.out.println("parseXmlToText(): " + message);
		try {
			return XmlParser.parse(message);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Send message to specific conversation
	 * @param conversationId
	 * @param message
	 */
	public void sendMessage(String conversationId, String message) {
		System.out.println("sendMessage(): " + message);
		SendMessageRequest req = new SendMessageRequest(message, conversationId, ticket);
		httpHelper.doRequest(req);
	}
	
	/**
	 * create a new conversation with a group of users
	 * @param userIds
	 * @param convName
	 * @return the response of the request
	 */
	public HttpResp createConv(List<String> userIds, String convName) {
		System.out.println("createConv(): " + convName);
		NewConvRequest req = new NewConvRequest(ticket, userIds, convName);
		return httpHelper.doRequest(req);
	}
	
	/**
	 * create a new conversation with one user
	 * @param userId
	 * @param convName
	 * @return the response of the request
	 */
	public HttpResp createConv(String userId, String convName) {
		System.out.println("createConv(): " + convName);
		NewPrivateConvRequest req = new NewPrivateConvRequest(ticket, userId, convName);
		return httpHelper.doRequest(req);
	}
	
	/**
	 * create a new conversation with one user
	 * @param userId
	 * @param convName
	 * @return the response of the request
	 */
	public void deleteConv(String convid) {
		System.out.println("deleteConv(): " + convid);
		DeleteConvRequest req = new DeleteConvRequest(ticket, convid);
		httpHelper.doRequest(req);
	}
}
