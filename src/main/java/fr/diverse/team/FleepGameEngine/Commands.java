package fr.diverse.team.FleepGameEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import fr.diverse.team.FleepGameEngine.creator.DynamicGameLoading;
import fr.diverse.team.FleepGameEngine.creator.Game;
import fr.diverse.team.FleepGameEngine.creator.GameRequests;

public class Commands {

	private static final String MAIN_TOKEN = "games";
	
	private static String[] args;
	private static String convId;
	private static String userId;

	public static void execute(String message, String convId, String userId) {
		Commands.convId = convId;
		Commands.userId = userId;
		String[] parts = message.split(" ");
		commands cmd = parse(parts);
		System.out.println("Executing the command : "+cmd.name());
		switch (cmd) {
		case ERROR:
			GameRequests.sendMessage(convId, "Unknown command. To get the list of commands use : games commands");
		case COMMANDS:
			printCommands();
			break;
		case UPDATE:
			updategamesByName();
			break;
		case LIST:
			listgamesByName();
			break;
		case PREPARE:
			prepareGame();
			break;
		case JOIN:
			joinGame();
			break;
		case START:
			startGame();
			break;
		case GAME_SPECIFIC:
			transfertToGame(parts[0], parts[1]);
			break;
		}
	}

	public enum commands {
		COMMANDS, UPDATE, LIST, PREPARE, JOIN, START, GAME_SPECIFIC, ERROR
	}

	private static commands parse(String[] parts) {
		if (parts[0].equals(MAIN_TOKEN)) {
			String cmd = parts[1];
			args = Arrays.copyOfRange(parts, 2, parts.length);
			switch (cmd) {
			case "update":
				return commands.UPDATE;
			case "prepare":
				return commands.PREPARE;
			case "join":
				return commands.JOIN;
			case "start":
				return commands.START;
			case "list":
				return commands.LIST;
			case "commands":
				return commands.COMMANDS;
			default:
				return commands.ERROR;
			}
		} else {
			args = Arrays.copyOfRange(parts, 2, parts.length);
			return commands.GAME_SPECIFIC;
		}
	}
	
	private static void printCommands() {
		if(args.length == 0) {
			String out = "The commands of the game manager are :\n";
			out += "games commands [game]  : Print commands of the manager or the given game\n";
			out += "games list             : List all the game available\n";
			out += "games update           : Update the game list(dynamic load of new games)\n";
			out += "games prepare game     : Create a room for the game\n";
			out += "games join game        : Join the room of the game\n";
			out += "games start game       : Start the game with users that joined\n";
			GameRequests.sendMessage(convId, out);
			return;
		}
		Game g = FleepGames.gamesByName().get(args[0]);
		if(g != null) {
			String out = "The commands of "+args[0]+" are :\n";
			out += g.genericHelp();
			GameRequests.sendMessage(convId, out);
			return;
		}
	}

	private static void updategamesByName() {
		FleepGames.gamesByName().clear();
		FleepGames.gamesByToken().clear();
		FleepGames.gamesByName().putAll(new DynamicGameLoading().discoverGames(FleepGames.gamesFolder()));
		Set<String> names = FleepGames.gamesByName().keySet();
		for (String name : names) {
			Game g = FleepGames.gamesByName().get(name);
			FleepGames.gamesByToken().put(g.token(), g);
		}
	}

	private static void prepareGame() {
		Set<String> names = FleepGames.gamesByName().keySet();
		if (!names.contains(args[0])) {
			FleepGames.requestUtils().sendMessage(convId, "This game doesn't exist (yet)");
			return;
		}
		if (FleepGames.rooms().containsKey(args[0])) {
			FleepGames.requestUtils().sendMessage(convId,
					"A room for this game is active and this bot can handle only one room per game (for now)");
			return;
		}
		FleepGames.rooms().put(args[0], new ArrayList<String>());
	}

	private static void joinGame() {
		if (FleepGames.rooms().containsKey(args[0])) {
			List<String> players = FleepGames.rooms().get(args[0]);
			if (players == null) {
				FleepGames.requestUtils().sendMessage(convId, "Sorry, but the game already started");
				return;
			}
			players.add(userId);
			return;
		}
		FleepGames.requestUtils().sendMessage(convId, "Sorry, but you need to create a room before joining a game");
	}

	private static void startGame() {
		if (FleepGames.rooms().containsKey(args[0])) {
			List<String> players = FleepGames.rooms().get(args[0]);
			if (players == null) {
				FleepGames.requestUtils().sendMessage(convId, "Sorry, but the game already started");
				return;
			}
			FleepGames.rooms().put(args[0], null);
			Game g = FleepGames.gamesByName().get(args[0]);
			g.start(players);
			return;
		}
		FleepGames.requestUtils().sendMessage(convId, "Sorry, but you need to create a room before starting a game");
	}

	private static void listgamesByName() {
		Set<String> names = FleepGames.gamesByName().keySet();
		String out = "List of deployed games :";
		for (String name : names) {
			out += "\n" + name;
		}
		FleepGames.requestUtils().sendMessage(convId, out);
	}

	private static void transfertToGame(String token, String cmd) {
		System.out.println("Searching for games with token : "+token);
		Game game = FleepGames.gamesByToken().get(token);
		if(game == null) {
			System.out.println("Game not found -> this may be a usual message");
			return;
		}
		String gameName = FleepGames.findName(game);
		System.out.println("We found the game : "+gameName);
		if(!FleepGames.rooms().containsKey(gameName) || FleepGames.rooms().get(gameName) != null ) {
			System.out.println("The game is not started -> transfert abort");
			return;
		}
		System.out.println("Transfert command "+cmd+" to the game");
		boolean isFinished = true;
		try {
			isFinished = game.triggerEvent(cmd, args, userId, convId);
		} catch (Exception e) {
			GameRequests.sendMessage(convId, "An error occured in the game : "+gameName);
		}
		if(isFinished) FleepGames.rooms().remove(gameName);
	}
}
