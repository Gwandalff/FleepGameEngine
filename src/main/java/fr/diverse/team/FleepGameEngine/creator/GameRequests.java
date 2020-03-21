package fr.diverse.team.FleepGameEngine.creator;

import java.util.List;

import fr.diverse.team.FleepGameEngine.FleepGames;
import fr.diverse.team.FleepGameEngine.HttpResp;

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
		FleepGames.requestUtils().sendMessage(convId, message);
	}
	
	/**
	 * createConv : create a conversation with multiple users
	 * @param userIds
	 * @param convName
	 * @return
	 */
	public static String createConv(List<String> userIds, String convName) {
		HttpResp resp = FleepGames.requestUtils().createConv(userIds, convName);
		return (String) resp.getJSONObject("header").get("conversation_id");
	}
	
	public static String createPrivateConv(String userId, String convName) {
		HttpResp resp = FleepGames.requestUtils().createConv(userId, convName);
		return (String) resp.getJSONObject("header").get("conversation_id");
	}
	
	public static void deleteConv(String convId) {
		FleepGames.requestUtils().deleteConv(convId);
	}
}
