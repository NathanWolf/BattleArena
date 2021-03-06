package mc.alk.arena.listeners;

import mc.alk.arena.BattleArena;
import mc.alk.arena.Defaults;
import mc.alk.arena.controllers.MoneyController;
import mc.alk.arena.controllers.plugins.DisguiseInterface;
import mc.alk.arena.controllers.plugins.EssentialsController;
import mc.alk.arena.controllers.plugins.FactionsController;
import mc.alk.arena.controllers.plugins.HeroesController;
import mc.alk.arena.controllers.plugins.McMMOController;
import mc.alk.arena.controllers.plugins.MobArenaInterface;
import mc.alk.arena.controllers.plugins.TagAPIController;
import mc.alk.arena.controllers.plugins.TrackerController;
import mc.alk.arena.controllers.plugins.VanishNoPacketInterface;
import mc.alk.arena.objects.messaging.AnnouncementOptions;
import mc.alk.arena.objects.messaging.plugins.HerochatPlugin;
import mc.alk.arena.plugins.BAPlaceholderExtension;
import mc.alk.arena.plugins.combattag.TagsOff;
import mc.alk.arena.plugins.combattag.TagsOn;
import mc.alk.arena.util.Log;
import mc.alk.arena.util.PermissionsUtil;
import mc.alk.arena.util.plugins.CombatTagUtil;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

import org.battleplugins.arenaregenutil.ArenaRegenController;
import org.battleplugins.arenaregenutil.RegenPlugin;
import org.battleplugins.worldguardutil.controllers.WorldGuardController;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author alkarin
 *
 */
public class BAPluginListener implements Listener {

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        // TODO: Clean this up
        if (event.getPlugin().getName().equalsIgnoreCase("BattleTracker")) {
            loadBattleTracker();
        } else if (event.getPlugin().getName().equalsIgnoreCase("CombatTag")) {
            loadCombatTag();
        } else if (event.getPlugin().getName().equalsIgnoreCase("DisguiseCraft")) {
            loadDisguiseCraft();
        } else if (event.getPlugin().getName().equalsIgnoreCase("Essentials")) {
            loadEssentials();
        } else if (event.getPlugin().getName().equalsIgnoreCase("Factions")) {
            loadFactions();
        } else if (event.getPlugin().getName().equalsIgnoreCase("Herochat")) {
            loadHeroChat();
        } else if (event.getPlugin().getName().equalsIgnoreCase("Heroes")) {
            loadHeroes();
        } else if (event.getPlugin().getName().equalsIgnoreCase("LibsDisguises")) {
            loadLibsDisguise();
        } else if (event.getPlugin().getName().equalsIgnoreCase("mcMMO")) {
            loadMcMMO();
        } else if (event.getPlugin().getName().equalsIgnoreCase("MobArena")) {
            loadMobArena();
        } else if (event.getPlugin().getName().equalsIgnoreCase("MultiInv")) {
            loadMultiInv();
        } else if (event.getPlugin().getName().equalsIgnoreCase("Multiverse-Core")) {
            loadMultiverseCore();
        } else if (event.getPlugin().getName().equalsIgnoreCase("Multiverse-Inventories")) {
            loadMultiverseInventory();
        } else if (event.getPlugin().getName().equalsIgnoreCase("PlaceholderAPI")) {
            loadPlaceholderAPI();
        } else if (event.getPlugin().getName().equalsIgnoreCase("TagAPI")) {
            loadTagAPI();
        } else if (event.getPlugin().getName().equalsIgnoreCase("WorldEdit")) {
            loadWorldEdit();
        } else if (event.getPlugin().getName().equalsIgnoreCase("WorldGuard")) {
            loadWorldGuard();
        } else if (event.getPlugin().getName().equalsIgnoreCase("VanishNoPacket")) {
            loadVanishNoPacket();
        } else if (event.getPlugin().getName().equalsIgnoreCase("Vault")) {
            loadVault();
        } else {
            loadOthers();
        }
    }

    public void loadAll() {
        loadBattleTracker();
        loadCombatTag();
        loadDisguiseCraft();
        loadEssentials();
        loadFactions();
        loadHeroChat();
        loadHeroes();
        loadLibsDisguise();
        loadMcMMO();
        loadMobArena();
        loadMultiInv();
        loadMultiverseCore();
        loadMultiverseInventory();
        loadPlaceholderAPI();
        loadTagAPI();
        loadWorldEdit();
        loadWorldGuard();
        loadVanishNoPacket();
        loadVault();
        loadOthers();
    }

    public void loadBattleTracker() {
        if (!TrackerController.enabled()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("BattleTracker");
            if (plugin != null) {
                TrackerController.setPlugin(plugin);
            } else {
                Log.info("[BattleArena] BattleTracker not detected, not tracking wins");
            }
        }
    }

    public void loadCombatTag() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CombatTag");
        if (plugin != null) {
            // CombatTagUtil will statically load and use CombatTagInterface
            // so there's no need to do anything here
            // except alert server admins that CombatTag was detected.
            Log.info("[BattleArena] CombatTag detected, enabling limited tag support");
        }
        if (CombatTagUtil.getCombatTagInterface() instanceof TagsOn) {
            Log.info("[BattleArena] CombatTagInterface is turned ON");
        } else if (CombatTagUtil.getCombatTagInterface() instanceof TagsOff) {
            Log.info("[BattleArena] CombatTagInterface is turned OFF");
        }
    }

    public void loadDisguiseCraft() {
        if (!DisguiseInterface.hasDC()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("DisguiseCraft");
            if (plugin != null) {
                DisguiseInterface.setDisguiseCraft(plugin);
                Log.info("[BattleArena] DisguiseCraft detected, enabling disguises");
            }
        }
    }

    public void loadEssentials() {
        if (!EssentialsController.enabled()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");
            if (plugin != null) {
                if (EssentialsController.setPlugin(plugin)) {
                    Log.info("[BattleArena] Essentials detected. God mode handling activated");
                } else {
                    Log.info("[BattleArena] Essentials detected but could not hook properly");
                }
            }
        }
    }

    public void loadFactions() {
        if (!FactionsController.enabled()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Factions");
            if (plugin != null) {
                if (FactionsController.setPlugin(true)) {
                    Log.info("[BattleArena] Factions detected. Configurable power loss enabled (default no powerloss)");
                } else {
                    Log.info("[BattleArena] Old Factions detected that does not have a PowerLossEvent");
                }
            }
        }
    }

    public void loadHeroChat() {
        if (AnnouncementOptions.chatPlugin == null) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Herochat");
            if (plugin != null) {
                AnnouncementOptions.setPlugin(new HerochatPlugin());
                Log.info("[BattleArena] Herochat detected, adding channel options");
            }
        }
    }

    public void loadHeroes() {
        if (!HeroesController.enabled()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Heroes");
            if (plugin != null) {
                HeroesController.setPlugin(plugin);
                Log.info("[BattleArena] Heroes detected. Implementing heroes class options");
            }
        }
    }

    public void loadLibsDisguise() {
        if (!DisguiseInterface.hasLibs()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("LibsDisguises");
            if (plugin != null) {
                DisguiseInterface.setLibsDisguise(plugin);
                Log.info("[BattleArena] LibsDisguises detected. Implementing disguises");
            }
        }
    }

    public void loadMcMMO() {
        if (!McMMOController.enabled()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("mcMMO");
            if (plugin != null) {
                McMMOController.setEnable(true);
                Log.info("[BattleArena] mcMMO detected. Implementing disabled skills options");
            }
        }
    }

    public void loadMobArena() {
        if (!MobArenaInterface.hasMobArena()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("MobArena");
            if (plugin != null) {
                MobArenaInterface.setPlugin(plugin);
                Log.info("[BattleArena] MobArena detected.  Implementing no add when in MobArena");
            }
        }
    }

    public void loadMultiInv() {
        if (!Defaults.PLUGIN_MULTI_INV) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("MultiInv");
            if (plugin != null) {
                Defaults.PLUGIN_MULTI_INV = true;
                Log.info("[BattleArena] MultiInv detected.  Implementing teleport/gamemode workarounds");
            }
        }
    }

    public void loadMultiverseCore() {
        if (!Defaults.PLUGIN_MULITVERSE_CORE) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
            if (plugin != null) {
                Defaults.PLUGIN_MULITVERSE_CORE = true;
                Log.info("[BattleArena] Multiverse-Core detected. Implementing teleport/gamemode workarounds");
            }
        }
    }

    public void loadMultiverseInventory() {
        if (!Defaults.PLUGIN_MULITVERSE_INV) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Inventories");
            if (plugin != null) {
                Defaults.PLUGIN_MULITVERSE_INV = true;
                Log.info("[BattleArena] Multiverse-Inventories detected. Implementing teleport/gamemode workarounds");
            }
        }
    }

    public void loadPlaceholderAPI() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (plugin != null) {
            new BAPlaceholderExtension().register();
            Log.info(BattleArena.getPluginName() + " PlaceholderAPI detected. Implementing placeholder hook.");
        }
    }

    public void loadWorldEdit() {
        ArenaRegenController.setPlugin(BattleArena.getSelf());
        ArenaRegenController.initialize();

        if (ArenaRegenController.hasRegenPlugin(RegenPlugin.WORLDEDIT)) {
            ArenaRegenController.setDefaultRegenPlugin(RegenPlugin.WORLDEDIT);
            Log.info("[BattleArena] WorldEdit detected.");
        }
    }

    public void loadWorldGuard() {
        if (!WorldGuardController.hasWorldGuard()) {
            Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
            if (plugin != null) {
                if (WorldGuardController.setWorldGuard(plugin)) {
                    Log.info("[BattleArena] WorldGuard detected. WorldGuard regions can now be used");
                }
            }
        }
    }

    public void loadTagAPI() {
        if (!TagAPIController.enabled()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("TagAPI");
            if (plugin != null) {
                TagAPIController.setEnable(true);
                Log.info("[BattleArena] TagAPI detected. Implementing Team colored player names");
            }
        }
    }

    public void loadVanishNoPacket() {
        if (!VanishNoPacketInterface.enabled()) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("VanishNoPacket");
            if (plugin != null) {
                VanishNoPacketInterface.setPlugin(plugin);
                Log.info("[BattleArena] VanishNoPacket detected. Invisibility fix is disabled for vanished players not in an arena");
            }
        }
    }

    public void loadVault() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Vault");
        if (plugin != null) {
            /// Load vault economy
            if (!MoneyController.hasEconomy()) {
                try {
                    RegisteredServiceProvider<Economy> provider = Bukkit.getServer().
                            getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                    if (provider == null || provider.getProvider() == null) {
                        Log.warn(BattleArena.getPluginName() + " found no economy plugin. Attempts to use money in arenas might result in errors.");
                        return;
                    } else {
                        MoneyController.setEconomy(provider.getProvider());
                        Log.info(BattleArena.getPluginName() + " found economy plugin Vault. [Default]");
                    }
                } catch (Error e) {
                    Log.err(BattleArena.getPluginName() + " exception loading economy through Vault");
                    Log.printStackTrace(e);
                }
            }
            /// Load Vault chat
            if (AnnouncementOptions.chat == null) {
                try {
                    RegisteredServiceProvider<Chat> provider = Bukkit.getServer().
                            getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
                    if (provider != null && provider.getProvider() != null) {
                        AnnouncementOptions.setVaultChat(provider.getProvider());
                    } else if (AnnouncementOptions.chatPlugin == null) {
                        Log.info("[BattleArena] Vault chat not detected, ignoring channel options");
                    }
                } catch (Error e) {
                    Log.err(BattleArena.getPluginName() + " exception loading chat through Vault");
                    Log.printStackTrace(e);
                }
            }
            /// Load Vault Permissions
            PermissionsUtil.setPermission(plugin);
        }
    }

    private void loadOthers() {
        if (Bukkit.getPluginManager().getPlugin("AntiLootSteal") != null) {
            Defaults.PLUGIN_ANTILOOT = true;
        }

        if (ArenaRegenController.hasRegenPlugin(RegenPlugin.ROLLBACK_CORE)) {
            ArenaRegenController.setDefaultRegenPlugin(RegenPlugin.ROLLBACK_CORE);
            Log.info("[BattleArena] RollbackCore detected. Using it for pastes and schematic saving.");
        }
    }

}
