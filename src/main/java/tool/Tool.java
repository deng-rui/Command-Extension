package extension.tool;

import java.io.*;
import java.net.*;
import java.util.*;

import arc.*;
import arc.Core;
import arc.files.*;
import arc.util.*;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.type.BaseUnit;
import mindustry.entities.type.Player;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.net.ValidateException;
import mindustry.world.blocks.power.NuclearReactor;

public class Tool {
	public static boolean isBlank(String string) {
		if (string == null || "".equals(string.trim())) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}

	public static class RuntimeData {
	private static final Map<String, Team> teams = new LinkedHashMap<>();
	public static Map<String, Team> getTeams() {
	  return teams;
	}
   
}
}