package fr.diverse.team.FleepGameEngine.creator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class GameBuilder {
	
	private String token;
	private Consumer<List<String>> constructor;
	private BooleanSupplier destructor;
	private BooleanSupplier checker;
	private Map<String, Consumer<GameEvent>> commands;
	private String help;
	
	public GameBuilder() {
		commands = new HashMap<String, Consumer<GameEvent>>();
	}
	
	public GameBuilder startingToken(String token) {
		this.token = token;
		return this;
	}
	
	public GameBuilder constructor(Consumer<List<String>> constructor) {
		this.constructor = constructor;
		return this;
	}
	
	public GameBuilder destructor(BooleanSupplier destructor) {
		this.destructor = destructor;
		return this;
	}
	
	public GameBuilder winChecker(BooleanSupplier checker) {
		this.checker = checker;
		return this;
	}
	
	public GameBuilder addGameEvent(String token, Consumer<GameEvent> event) {
		this.commands.put(token, event);
		return this;
	}
	
	public GameBuilder addHelp(String helpMessage) {
		help = helpMessage;
		return this;
	}
	
	public Game build() {
		if(help == null) {
			return new Game(token, constructor, destructor, checker, commands);
		} else {
			return new Game(token, constructor, destructor, checker, commands, help);
		}
	}
}
