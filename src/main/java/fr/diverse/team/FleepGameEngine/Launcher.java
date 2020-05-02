package fr.diverse.team.FleepGameEngine;

import fr.gjouneau.FleepBotAPI.FleepBot;

public class Launcher {

	public static void main(String[] args) {
		FleepBot gameEngine = new FleepGames(args[0], args[1], args[2]);
		gameEngine.start();
	}

}
