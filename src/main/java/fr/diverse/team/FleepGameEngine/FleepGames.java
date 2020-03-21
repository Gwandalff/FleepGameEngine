package fr.diverse.team.FleepGameEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.diverse.team.FleepGameEngine.creator.Game;
import fr.diverse.team.FleepGameEngine.requests.LoginRequest;
import fr.diverse.team.FleepGameEngine.requests.PollRequest;
import fr.diverse.team.FleepGameEngine.requests.SyncRequest;

/**
 * Fleep.io echo bot example
 * 
 * Replies every message in every conversation where it is added.
 * Separate account might be desired for logging into this bot.
 * 
 * See rest of the API documentation: https://fleep.io/api/describe
 * 
 */

public class FleepGames {
	
	private static HttpHelper httpHelper;
	private static String ticket;
	private static String myAccountId;
	private static String gamesFolder;
	private static RequestUtils requestUtils;
	private static Map<String, Game> gamesByName = new HashMap<String, Game>();
	private static Map<String, Game> gamesByToken = new HashMap<String, Game>();
	private static Map<String, List<String>> rooms = new HashMap<String, List<String>>();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		gamesFolder = args[0];
		System.out.println("Fleep Games Bot");
		System.out.println("Please enter your credentials");
		System.out.print("username: ");

		Scanner userInput = new Scanner(System.in);
		String username = userInput.next();
		System.out.print("password: ");
		String password = userInput.next();
		userInput.close();
		System.out.println("Logging in with " + username + "...");
		
		httpHelper = new HttpHelper();
		
		// login
		logIn(username, password);
		// request last state of events
		long eventHorizon = requestInitialEventHorizon();
		// start echo
		if (eventHorizon >= 0) {
			startBot(eventHorizon);
		}
	}
	
	/**
	 * Log in the account
	 * @param email
	 * @param password
	 * @return web-ticket that is required for other requests
	 */
	private static void logIn(String email, String password) {
		LoginRequest req = new LoginRequest(email, password);
		HttpResp resp = httpHelper.doRequest(req);
		if (resp != null && resp.getResponseCode() == 200) {
			System.out.println("Login successful.");
			ticket = resp.getString("ticket");
			myAccountId = resp.getString("account_id");
			requestUtils = new RequestUtils(httpHelper, ticket, myAccountId);
		} else {
			System.out.println("Login failed.");
			System.exit(-1);
		}
	}
	
	/**
	 * Request for current event horizon.
	 * We don't want this bot to receive any history from the server, but only new messages
	 * Therefore we need to request for up to date event horizon to start the long-poll with
	 * @return current event horizon
	 */
	private static long requestInitialEventHorizon() {
		SyncRequest req = new SyncRequest(ticket);
		HttpResp resp = httpHelper.doRequest(req);
		if (resp != null && resp.getResponseCode() == 200) {
			long eventHorizon = resp.getLong("event_horizon");
			System.out.println("Initial event horizon: " + eventHorizon);
			return eventHorizon;
		}
		return -1;
	}
	
	/**
	 * Start actual echo bot
	 * It will connect to server using long poll pattern.
	 * Connection will stay alive for 90 seconds. If new events appear, server returns instantly.
	 * If no events appear, server returns empty array and client will initiate another request.
	 * @param eventHorizon
	 */
	private static void startBot(long initialEventHorizon) {
		long eventHorizon = initialEventHorizon;
		while (true) {
			PollRequest req = new PollRequest(eventHorizon, ticket);
			HttpResp resp = httpHelper.doRequest(req);
			if (resp != null && resp.getResponseCode() == 200) {
				eventHorizon = resp.getLong("event_horizon");
				JSONArray list = resp.getList("stream");
				if (list != null) {
					handlePollResults(list);
				}
			} else if (resp != null && resp.getResponseCode() == 401) {
				// current credentials are not valid any more
				System.out.println("Session expired, stopping program");
				System.exit(-1);
				return;
			} else {
				System.out.println("Connection lost.. retrying in 1 minute");
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	
	/**
	 * Go through the array of events returned by long poll
	 * @param stream
	 */
	private static void handlePollResults(JSONArray stream) {
		System.out.println("handlePollResults: " + stream.size());
		for (int i = 0; i < stream.size(); i++) {
			JSONObject item = (JSONObject) stream.get(i);
			String type = (String) item.get("mk_rec_type");
			// only interested in messages
			if (type.equals("message")) {
				// new message received
				replyMessage(item);
			}
		}
	}
	
	/**
	 * Reply same message to the same conversation
	 * @param message
	 */
	private static void replyMessage(JSONObject message) {
		String mkMessageType = (String) message.get("mk_message_type");
		if (mkMessageType.equals("text")) {
			System.out.println("message received...");
			// first, some checks to make sure it's just plain message
			
			// check if it's pin or unpin event
			if (message.get("tags") != null) {
				JSONArray tags = (JSONArray) message.get("tags");
				if (tags != null && tags.size() > 0) {
					if (tags.toString().contains("pin")) {
						// either pin or unpin event
						System.out.println("it's a pin, stop here");
						return;
					}
				}
			}
			// check if it is edit to some existing message
			if (message.get("flow_message_nr") != null) {
				// flow message nr appears on edit events only
				System.out.println("it's an edit, stop here");
				return;
			}
			
			String messageAccountId = (String) message.get("account_id");
			if (!messageAccountId.equals(myAccountId)) {
				// only reply to messages that are not my own
				String conversationId = (String) message.get("conversation_id");
				String messageText = requestUtils.parseXmlToText((String) message.get("message"));
				
				System.out.println("message after parsing: " + messageText);
				if (messageText != null && messageText.length() > 0) {
					Commands.execute(messageText, conversationId, messageAccountId);
				}
			} else {
				System.out.println("it's my own message, stop here");
			}
		}
	}
	
	public static String findName(Game g) {
		Set<String> names = gamesByName.keySet();
		for (String name : names) {
			if(gamesByName.get(name) == g) {
				return name;
			}
		}
		return null;
	}

	public static Map<String, Game> gamesByName(){return gamesByName;}
	public static Map<String, Game> gamesByToken(){return gamesByToken;}
	public static RequestUtils requestUtils(){return requestUtils;}
	public static Map<String, List<String>> rooms(){return rooms;}
	public static String gamesFolder(){return gamesFolder;}
}
