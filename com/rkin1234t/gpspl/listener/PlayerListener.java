    package com.rkin1234t.gpspl.listener;

import com.rkin1234t.gpspl.GpsPL;
import com.rkin1234t.gpspl.manager.NavigationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
   private final NavigationManager navigationManager;

   public PlayerListener(GpsPL plugin) {
      this.navigationManager = plugin.getNavigationManager();
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      if (this.navigationManager.hasActiveNavigation(player)) {
         this.navigationManager.stopNavigation(player);
      }

   }
}
    
