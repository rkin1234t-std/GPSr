    package com.rkin1234t.gpspl;

import com.rkin1234t.gpspl.command.GpsCommand;
import com.rkin1234t.gpspl.data.GpsPoint;
import com.rkin1234t.gpspl.listener.PlayerListener;
import com.rkin1234t.gpspl.manager.NavigationManager;
import com.rkin1234t.gpspl.manager.PointManager;
import com.rkin1234t.gpspl.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class GpsPL extends JavaPlugin {
   private static GpsPL instance;
   private final Logger pluginLogger = this.getSLF4JLogger();
   private PointManager pointManager;
   private NavigationManager navigationManager;

   public void onEnable() {
      instance = this;
      ConfigurationSerialization.registerClass(GpsPoint.class);
      this.saveDefaultConfig();
      MessageUtil.init(this);
      this.pointManager = new PointManager(this);
      this.navigationManager = new NavigationManager(this);
      GpsCommand gpsCommand = new GpsCommand(this);
      this.getCommand("gpspl").setExecutor(gpsCommand);
      this.getCommand("gpspl").setTabCompleter(gpsCommand);
      this.getCommand("gps").setExecutor(gpsCommand);
      this.getCommand("gps").setTabCompleter(gpsCommand);
      this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
      this.pluginLogger.info("Плагин GpsPL успешно запущен!");
   }

   public void onDisable() {
      Bukkit.getScheduler().cancelTasks(this);
      this.pluginLogger.info("Плагин GpsPL отключен!");
   }

   public static GpsPL getInstance() {
      return instance;
   }

   public PointManager getPointManager() {
      return this.pointManager;
   }

   public NavigationManager getNavigationManager() {
      return this.navigationManager;
   }
}
    
