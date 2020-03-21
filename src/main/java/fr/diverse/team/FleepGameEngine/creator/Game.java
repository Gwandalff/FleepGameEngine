package fr.diverse.team.FleepGameEngine.creator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Game {
	
	private final String token;
	private final Consumer<List<String>> constructor;
	private final BooleanSupplier destructor;
	private final BooleanSupplier checker;
	private final Map<String, Consumer<GameEvent>> commands;
	private final String help;
	
	Game(String token,
			Consumer<List<String>> constructor,
			BooleanSupplier destructor,
			BooleanSupplier checker,
			Map<String, Consumer<GameEvent>> commands) {
		this.checker = checker;
		this.commands = commands;
		this.constructor = constructor;
		this.destructor = destructor;
		this.token = token;
		this.help = "";
	}
	
	Game(String token,
			Consumer<List<String>> constructor,
			BooleanSupplier destructor,
			BooleanSupplier checker,
			Map<String, Consumer<GameEvent>> commands,
			String help) {
		this.checker = checker;
		this.commands = commands;
		this.constructor = constructor;
		this.destructor = destructor;
		this.token = token;
		this.help = help;
	}
	
	public String token() {return token;}
	
	public void start(List<String> userIds) {
		constructor.accept(userIds);
	}
	
	public boolean triggerEvent(String cmd, String[] args, String userId, String convId) {
		GameEvent e = new GameEvent(args, userId, convId);
		Consumer<GameEvent> commandEvent = commands.get(cmd);
		if(commandEvent != null) {
			commandEvent.accept(e);
		}
		boolean isFinished = checker.getAsBoolean();
		if(isFinished) {
			return destructor.getAsBoolean();
		}
		return false;
	}
	
	public String genericHelp() {
		if(!help.equals("")) return help;
		String out = "";
		Set<String> cmds = commands.keySet();
		for (String cmd : cmds) {
			out += token + " " + cmd + "\n";
		}
		return out;
	}

}
