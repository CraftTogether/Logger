package com.github.crafttogether.logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Logger extends JavaPlugin {

    public static FileConfiguration config;

    @Override
    public void onEnable() {
        config = this.getConfig();
        Bukkit.getServer().getPluginManager().registerEvents(new Listener(), this);
    }

}
