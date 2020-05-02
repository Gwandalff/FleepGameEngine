package fr.diverse.team.FleepGameEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.diverse.team.FleepGameEngine.creator.Game;
import fr.gjouneau.FleepBotAPI.FleepBot;
import fr.gjouneau.FleepBotAPI.requests.RequestUtils;

public class FleepGames extends FleepBot {
	
	private Commands cmd;
	private String gamesFolder;
	private Map<String, Game> gamesByName = new HashMap<String, Game>();
	private Map<String, Game> gamesByToken = new HashMap<String, Game>();
	private Map<String, List<String>> rooms = new HashMap<String, List<String>>();
	
	public FleepGames(String email, String password, String gamesFolder) {
		super(email, password);
		this.gamesFolder = gamesFolder;
		this.cmd = new Commands(this);
	}
	
	@Override
	protected void handleMessage(JSONObject message) {
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
				String messageText = RequestUtils.parseXmlToText((String) message.get("message"));
				
				System.out.println("message after parsing: " + messageText);
				if (messageText != null && messageText.length() > 0) {
					cmd.execute(messageText, conversationId, messageAccountId);
				}
			} else {
				System.out.println("it's my own message, stop here");
			}
		}
	}
	
	public String findName(Game g) {
		Set<String> names = gamesByName.keySet();
		for (String name : names) {
			if(gamesByName.get(name) == g) {
				return name;
			}
		}
		return null;
	}

	public Map<String, Game> gamesByName(){return gamesByName;}
	public Map<String, Game> gamesByToken(){return gamesByToken;}
	public Map<String, List<String>> rooms(){return rooms;}
	public String gamesFolder(){return gamesFolder;}
}
