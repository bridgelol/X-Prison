package me.drawethree.ultraprisoncore;

import lombok.Getter;
import me.drawethree.ultraprisoncore.autominer.UltraPrisonAutoMiner;
import me.drawethree.ultraprisoncore.autosell.UltraPrisonAutoSell;
import me.drawethree.ultraprisoncore.config.FileManager;
import me.drawethree.ultraprisoncore.database.MySQLDatabase;
import me.drawethree.ultraprisoncore.enchants.UltraPrisonEnchants;
import me.drawethree.ultraprisoncore.gems.UltraPrisonGems;
import me.drawethree.ultraprisoncore.multipliers.UltraPrisonMultipliers;
import me.drawethree.ultraprisoncore.placeholders.UltraPrisonPlaceholder;
import me.drawethree.ultraprisoncore.ranks.UltraPrisonRankup;
import me.drawethree.ultraprisoncore.tokens.UltraPrisonTokens;
import me.drawethree.ultraprisoncore.utils.gui.ClearDBGui;
import me.jet315.prisonmines.JetsPrisonMines;
import me.jet315.prisonmines.JetsPrisonMinesAPI;
import me.lucko.helper.Commands;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.text.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Getter
public final class UltraPrisonCore extends ExtendedJavaPlugin {


    private List<UltraPrisonModule> loadedModules;

    @Getter
    private static UltraPrisonCore instance;
    private MySQLDatabase sqlDatabase;
    private Economy economy;
    private FileManager fileManager;

    private UltraPrisonTokens tokens;
    private UltraPrisonGems gems;
    private UltraPrisonRankup ranks;
    private UltraPrisonMultipliers multipliers;
    private UltraPrisonEnchants enchants;
    private UltraPrisonAutoSell autoSell;
    private UltraPrisonAutoMiner autoMiner;

    private JetsPrisonMinesAPI jetsPrisonMinesAPI;

    @Override
    protected void enable() {

        instance = this;
        this.loadedModules = new ArrayList<>();
        this.fileManager = new FileManager(this);
        this.fileManager.getConfig("config.yml").copyDefaults(true).save();

        this.sqlDatabase = new MySQLDatabase(this);

        this.tokens = new UltraPrisonTokens(this);
        this.gems = new UltraPrisonGems(this);
        this.ranks = new UltraPrisonRankup(this);
        this.multipliers = new UltraPrisonMultipliers(this);
        this.enchants = new UltraPrisonEnchants(this);
        this.autoSell = new UltraPrisonAutoSell(this);
        this.autoMiner = new UltraPrisonAutoMiner(this);

        this.setupEconomy();
        this.registerPlaceholders();
        this.registerJetsPrisonMines();


        if (this.getConfig().getBoolean("modules.tokens")) {
            this.loadModule(tokens);
        }
        if (this.getConfig().getBoolean("modules.gems")) {
            this.loadModule(gems);
        }
        if (this.getConfig().getBoolean("modules.ranks")) {
            this.loadModule(ranks);
        }
        if (this.getConfig().getBoolean("modules.multipliers")) {
            this.loadModule(multipliers);
        }
        if (this.getConfig().getBoolean("modules.enchants")) {
            this.loadModule(enchants);
        }
        if (this.getConfig().getBoolean("modules.autosell")) {
            this.loadModule(autoSell);
        }
        if (this.getConfig().getBoolean("modules.autominer")) {
            this.loadModule(autoMiner);
        }

        this.startEvents();
        this.registerMainCommand();
    }

    private void loadModule(UltraPrisonModule module) {
        this.loadedModules.add(module);
        module.enable();
        this.getLogger().info(Text.colorize(String.format("&aUltraPrisonCore - Module %s loaded.", module.getName())));
    }

    private void unloadModule(UltraPrisonModule module) {
        this.loadedModules.remove(module);
        module.disable();
        this.getLogger().info(Text.colorize(String.format("&aUltraPrisonCore - Module %s unloaded.", module.getName())));
    }

    private void reloadModule(UltraPrisonModule module) {
        module.reload();
        this.getLogger().info(Text.colorize(String.format("&aUltraPrisonCore - Module %s reloaded.", module.getName())));

    }

    public boolean isModuleLoaded(UltraPrisonModule module) {
        return this.loadedModules.contains(module);
    }

    private void registerMainCommand() {
        Commands.create()
                .assertOp()
                .handler(c -> {
                    if (c.args().size() == 1 && c.rawArg(0).equalsIgnoreCase("reload")) {
                        this.reload(c.sender());
                    } else if (c.args().size() == 1 && c.rawArg(0).equalsIgnoreCase("cleardb")) {
                        if (c.sender() instanceof Player) {
                            new ClearDBGui(this.sqlDatabase, (Player) c.sender()).open();
                        } else {
                            this.sqlDatabase.resetAllTables(c.sender());
                        }
                    }
                }).registerAndBind(this, "prisoncore", "ultraprison", "prison");
    }

    private void reload(CommandSender sender) {
        for (UltraPrisonModule module : this.loadedModules) {
            this.reloadModule(module);
        }
        sender.sendMessage(Text.colorize("&aUltraPrisonCore - Reloaded."));
    }


    @Override
    protected void disable() {

        Iterator<UltraPrisonModule> it = this.loadedModules.iterator();

        while (it.hasNext()) {
            this.unloadModule(it.next());
        }

        this.sqlDatabase.close();
    }

    private void startEvents() {

    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new UltraPrisonPlaceholder(this).register();
        }
    }

    private void registerJetsPrisonMines() {
        if (Bukkit.getPluginManager().getPlugin("JetsPrisonMines") != null) {
            this.jetsPrisonMinesAPI = ((JetsPrisonMines) getServer().getPluginManager().getPlugin("JetsPrisonMines")).getAPI();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }
}