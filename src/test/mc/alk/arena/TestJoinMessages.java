package mc.alk.arena;

import junit.framework.TestCase;
import mc.alk.arena.BattleArena;
import mc.alk.arena.Defaults;
import mc.alk.arena.controllers.BattleArenaController;
import mc.alk.arena.controllers.BukkitServer;
import mc.alk.arena.controllers.ParamController;
import mc.alk.arena.executors.BAExecutor;
import mc.alk.arena.objects.ArenaPlayer;
import mc.alk.arena.objects.CompetitionSize;
import mc.alk.arena.objects.MatchParams;
import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.arenas.ArenaType;
import mc.alk.arena.objects.messaging.AnnouncementOptions;
import mc.alk.arena.serializers.ArenaSerializer;
import mc.alk.arena.serializers.BAClassesSerializer;
import mc.alk.arena.serializers.BAConfigSerializer;
import mc.alk.arena.serializers.MessageSerializer;
import mc.alk.arena.util.MessageUtil;
import mc.alk.arena.util.MinMax;
import mc.alk.mc.MCServer;
import org.bukkit.entity.Player;
import mc.alk.arena.objects.TestPlugin;
import mc.alk.arena.util.Helper;
import mc.alk.tests.testbukkit.TestBukkitPlayer;
import mc.alk.tests.testbukkit.TestBukkitServer;
import mc.alk.tests.testbukkit.TestMCBukkitServer;

import java.io.File;
import java.lang.reflect.Field;


@SuppressWarnings("unused")
public class TestJoinMessages extends TestCase{
	TestPlugin plugin = null;
	BattleArenaController bac;
	private static final BAConfigSerializer baConfigSerializer = new BAConfigSerializer();
	BattleArena ba = new BattleArena();
	ArenaPlayer[] ap = new ArenaPlayer[10];
    public static String dir = "../arena/BattleArena";

    @Override
	protected void setUp() throws Exception {
        Defaults.DEBUG_MSGS = true;
        Defaults.TESTSERVER = true;

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        plugin = new TestPlugin();
        ArenaType.register("arena", Arena.class, plugin);
        BukkitServer.setServer(new TestBukkitServer());
        plugin.onEnable();

        /// Set test server
        MCServer.setInstance(new TestMCBukkitServer());
        baConfigSerializer.setConfig(new File(dir + "/test_files/config.yml"));
        baConfigSerializer.loadDefaults();
        MatchParams mp = ParamController.getMatchParamCopy(Defaults.DEFAULT_CONFIG_NAME);
        assertNotNull(mp);
        for (int i = 0; i < ap.length; i++) {
            ap[i] = createArenaPlayer("p" + i);
        }

        /// load classes
        BAClassesSerializer classesSerializer = new BAClassesSerializer();
        classesSerializer.setConfig(new File(dir + "/test_files/classes.yml"));
        classesSerializer.loadAll();
        /// Controller
        bac = new BattleArenaController(null);
        Field field = BattleArena.class.getDeclaredField("arenaController");
        field.setAccessible(true);
        field.set(null, bac);

        /// Messages
        MessageSerializer ms = new MessageSerializer("default", null);
        ms.setConfig(new File(dir + "/default_files/messages.yml"));
        MessageSerializer.setDefaultConfig(ms);
        AnnouncementOptions an = new AnnouncementOptions();
        AnnouncementOptions.setDefaultOptions(an);

        mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");
        assertNotNull(mp);

        /// Arenas
        ArenaSerializer as = new ArenaSerializer(plugin, new File(dir + "/test_files/arenas.yml"));
        ArenaSerializer.setBAC(bac);
        as.loadArenas(plugin);
    }

	public static ArenaPlayer createArenaPlayer(String name){
		Player p1 = new TestBukkitPlayer(name);
		return BattleArena.toArenaPlayer(p1);
	}

    public void testFixed_stillTime_inQltMin() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");

        mp.setTeamSize(1);
        mp.setNTeams(new MinMax(2));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();
        
        // mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max "
        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString()+ " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = 1; inQ < maxPlayers; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, null)).trim();
            String e = "Match starts immediately when " + (maxPlayers - inQ) + " more players join. " + inQ + "/" + maxPlayers;
            assertEquals(e, m);
        }
    }


    public void testFixed_stillTime2() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");
        mp.setTeamSize(1);
        mp.setNTeams(new MinMax(2));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = maxPlayers; inQ < maxPlayers * 2; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, null)).trim();
            String e = "Your match will start when an arena is free";
            assertEquals(e, m);
        }
    }


    public void testVar_stillTime_inQltMin() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");

        mp.setTeamSize(1);
        mp.setNTeams(new MinMax(2, 4));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = 1; inQ < minPlayers; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, null)).trim();
            String e = "Match starts when " + (maxPlayers - inQ) +
                    " more players join or in " + seconds + " second with at least " + (minPlayers) + " players";
            assertEquals(e, m);
        }
    }

    public void testVar_stillTime_inQltMax() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");

        mp.setTeamSize(1);
        mp.setNTeams(new MinMax(2, 4));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = minPlayers; inQ < maxPlayers; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, null)).trim();
            String e = "Match starts when " + (maxPlayers - inQ) + " more players join or in " + (seconds) + " second";
            assertEquals(e, m);
        }
    }

    public void testVar_stillTime_inQgeMax_NoPosition() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");
        mp.setTeamSize(1);
        mp.setNTeams(new MinMax(2, 4));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = maxPlayers; inQ < maxPlayers * 2; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, null)).trim();
            String e = "Your match will start when an arena is free";
            assertEquals(e, m);
        }
    }

    public void testVar_stillTime_inQgeMax_Position() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");
        mp.setTeamSize(1);
        mp.setNTeams(new MinMax(2, 4));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = maxPlayers; inQ < maxPlayers * 2; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, inQ)).trim();
            String e = "Position: " + inQ + ". your match will start when an arena is free";
            assertEquals(e, m);
        }
    }


    public void testMax_stillTime_inQltMin() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");
        mp.setTeamSize(2);
        mp.setNTeams(new MinMax(3, CompetitionSize.MAX));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = 1; inQ < minPlayers; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, null)).trim();
            String e = "Match starts in " + seconds + " second with at least " + (minPlayers - inQ) + " players";
            assertEquals(e, m);
        }
    }

    public void testMax_stillTime_inQltMax() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");
        mp.setTeamSize(2);
        mp.setNTeams(new MinMax(3, CompetitionSize.MAX));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = minPlayers; inQ < minPlayers * 2; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, null)).trim();
            String e = "Match starts in " + (seconds) + " second";
            assertEquals(e, m);
        }
    }

    public void testMax_stillTime_inQgeMax_NoPosition() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");
        mp.setTeamSize(2);
        mp.setNTeams(new MinMax(3, CompetitionSize.MAX));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = maxPlayers; inQ < maxPlayers * 2; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, null)).trim();
            String e = "Your match will start when an arena is free";
            assertEquals(e, m);
        }
    }

    public void testMax_stillTime_inQgeMax_Position() throws Exception {
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");
        mp.setTeamSize(2);
        mp.setNTeams(new MinMax(3, CompetitionSize.MAX));
        long millisRemaining = 1000;
        int seconds = (int) (millisRemaining / 1000);
        int maxPlayers = mp.getMaxPlayers();
        int minPlayers = mp.getMinPlayers();

        msg(mp.getNTeams().toString() + " - " + mp.getTeamSize().toString() + " ,min:max " + minPlayers + " : " + maxPlayers + " , " +
                millisRemaining + " remaining ");
        for (int inQ = maxPlayers; inQ < maxPlayers * 2; inQ++) {
            String m = MessageUtil.decolorChat(BAExecutor.constructMessage(mp, millisRemaining, inQ, inQ)).trim();
            String e = "Position: " + inQ + ". your match will start when an arena is free";
            assertEquals(e, m);
        }
    }

    public static void msg(String msg) {
        System.out.println(MessageUtil.decolorChat(msg));
    }

    public void offtestQueue() throws Exception {
		String[] args = new String[]{"add", "a1"};
		String[] args2 = new String[]{"add","a2"};
		assertNull(BattleArena.getArena("DoesntExist"));
		assertNotNull(BattleArena.getArena("a1"));
		assertNotNull(BattleArena.getArena("a2"));
		BAExecutor exec = new BAExecutor();
        MatchParams mp = Helper.loadParams(dir + "/test_files/competitions/ArenaConfig.yml", plugin, "Arena");

		exec.join(ap[0], mp, args);
		exec.join(ap[1], mp, args);

		exec.join(ap[2], mp, args2);
		exec.join(ap[3], mp, args2);
		exec.join(ap[4], mp, args2);
		exec.join(ap[5], mp, args2);
//		for (MatchParams params : ParamController.getAllParams()){
//			System.out.println("param  =  "+ params);
//		}
		delay(50);
	}

	private void delay(long millis) {
		try {Thread.sleep(millis);}catch(Exception e){}
	}

}
