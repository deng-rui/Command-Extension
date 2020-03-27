package extension.data.db;

public class PlayerData {
	// 奇怪的public
	public String UUID;
	public String User;
	public String NAME;
	public String Mail;
	//
	public long IP;
	public long GMT;
	public String Country;
	public int Time_format;
	public String Language;
	public long LastLogin;
	public int Kickcount;
	//int Sensitive;
	public boolean Translate;
	public int Level;
	public long Exp;
	public long Reqexp;
	public long Reqtotalexp;
	public long Playtime;
	public int Pvpwincount;
	public int Pvplosecount;
	public int Authority;
	public long Lastchat;
	public int Deadcount;
	public int Killcount;
	public int Joincount;
	public int Breakcount;
	//
	public int Buildcount;
	public int Dismantledcount;
	public int Cumulative_build;
	/* */
	public boolean Online;
	public String PasswordHash;
	public String CSPRNG;
	/* */
	public long Jointime;

	public PlayerData(String UUID) {
		this.UUID = UUID;
	}
}
	