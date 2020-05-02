package fr.diverse.team.FleepGameEngine.creator;

import java.util.List;

import fr.diverse.team.FleepGameEngine.FleepGames;
import fr.gjouneau.FleepBotAPI.requests.HttpResp;
import fr.gjouneau.FleepBotAPI.requests.RequestUtils;

/**
 * GameRequests : manage the requests made by games
 * @author gwandalf
 *
 */
public class GameRequests {
	
	/**
	 * sendMessage : send a message to a conversation
	 * @param convId
	 * @param message
	 */
	public static void sendMessage(String convId, String message) {
		RequestUtils.sendMessage(convId, message);
	}
	
	/**
	 * createConv : create a conversation with multiple users
	 * @param userIds
	 * @param convName
	 * @return convID
	 */
	public static String createConv(List<String> userIds, String convName) {
		HttpResp resp = RequestUtils.createConv(userIds, convName);
		return (String) resp.getJSONObject("header").get("conversation_id");
	}
	
	/**
	 * createPrivateConv : create a conversation with one user
	 * @param userId
	 * @param convName
	 * @return convID
	 */
	public static String createPrivateConv(String userId, String convName) {
		HttpResp resp = RequestUtils.createConv(userId, convName);
		return (String) resp.getJSONObject("header").get("conversation_id");
	}
	
	/**
	 * leaveConv : make the bot leave the conv
	 * @param convName
	 */
	public static void leaveConv(String convId) {
		RequestUtils.leaveConv(convId);
	}
}
