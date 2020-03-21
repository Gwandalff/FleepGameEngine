package fr.diverse.team.FleepGameEngine.creator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class DynamicGameLoading {
	
	private Map<String, Game> games = new HashMap<String, Game>();
	
	public Map<String, Game> discoverGames(String pathToGameFolder) {
		File dir = new File(pathToGameFolder);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
		    if (file.isFile() && file.toString().endsWith(".jar")) {
		        load(file);
		    }
		}
		return games;
	}
	
	public void load(File myJar){
		URLClassLoader child = null;
		try {
			child = new URLClassLoader(
			    new URL[] {myJar.toURI().toURL()},
			    GameBuilder.class.getClassLoader()
			);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Class<AbstractGameSpecification> GameClass = null;
		try {
			String name = myJar.getName();
			name = name.substring(0, name.length()-4);
			GameClass = (Class<AbstractGameSpecification>) Class.forName(name, true, child);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		AbstractGameSpecification instance = null;
		try {
			instance = GameClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		Method getGame = null;
		Method getName = null;
		try {
			getName = GameClass.getDeclaredMethod("getName");
			getGame = GameClass.getDeclaredMethod("getGame");
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		String name = null; 
		Game game = null;
		try {
			name = (String) getName.invoke(instance);
			game = (Game) getGame.invoke(instance);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		games.put(name, game);
	}

}
