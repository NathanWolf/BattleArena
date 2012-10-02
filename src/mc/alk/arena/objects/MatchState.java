package mc.alk.arena.objects;


/**
 * @author alkarin
 *
 * Enum of MatchTransitions, and MatchStates
 */
public enum MatchState {
	DEFAULTS("defaults"),
	ONOPEN("onOpen"), PREREQS ("preReqs"), ONBEGIN("onBegin"),
	ONJOIN ("onJoin"), ONPRESTART ("onPrestart"), ONSTART ("onStart"), ONVICTORY ("onVictory"),
	ONCOMPLETE ("onComplete"), ONCANCEL ("onCancel"),
	ONFINISH("onFinish"),
	ONDEATH ("onDeath"), ONSPAWN ("onSpawn"), WINNER ("winner"),
	ONENTER("onEnter"), ONLEAVE("onLeave"), ONENTERWAITROOM("onEnterWaitRoom"),
	LOSERS ("losers"), FIRSTPLACE ("firstPlace"), PARTICIPANTS("participants"),
	ONMATCHINTERVAL("onMatchInterval"), ONMATCHTIMEEXPIRED("onMatchTimeExpired"),
	NONE("None"), ONCOUNTDOWNTOEVENT("onCountdownToEvent");
	
	String name;
	MatchState(String name){
		this.name = name;
	}
	public String toString(){
		return name;
	}
	public static MatchState fromName(String str){
		str = str.toUpperCase();
		return MatchState.valueOf(str);
	}
	public boolean isRunning() {
		return this == MatchState.ONSTART;
	}
}
