    package com.rkin1234t.gpspl.manager;

import com.rkin1234t.gpspl.GpsPL;
import com.rkin1234t.gpspl.data.GpsPoint;
import com.rkin1234t.gpspl.util.MessageUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class NavigationManager {
   private final GpsPL plugin;
   private final Map<UUID, NavigationManager.NavigationSession> activeSessions = new HashMap();

   public NavigationManager(GpsPL plugin) {
      this.plugin = plugin;
   }

   public boolean startNavigation(Player player, GpsPoint point) {
      this.stopNavigation(player);
      World world = Bukkit.getWorld(point.getWorldName());
      if (world == null) {
         return false;
      } else {
         NavigationManager.NavigationSession session = new NavigationManager.NavigationSession(player.getUniqueId(), point);
         this.activeSessions.put(player.getUniqueId(), session);
         int taskId = Bukkit.getScheduler().runTaskTimer(this.plugin, () -> {
            this.updateNavigation(player, point);
         }, 5L, 5L).getTaskId();
         session.setTaskId(taskId);
         return true;
      }
   }

   public boolean startNavigationToCoords(Player player, double x, double y, double z) {
      GpsPoint tempPoint = new GpsPoint("temp", x, y, z, player.getWorld().getName());
      return this.startNavigation(player, tempPoint);
   }

   public boolean stopNavigation(Player player) {
      NavigationManager.NavigationSession session = (NavigationManager.NavigationSession)this.activeSessions.remove(player.getUniqueId());
      if (session != null) {
         Bukkit.getScheduler().cancelTask(session.getTaskId());
         return true;
      } else {
         return false;
      }
   }

   public boolean hasActiveNavigation(Player player) {
      return this.activeSessions.containsKey(player.getUniqueId());
   }

   public Optional<NavigationManager.NavigationSession> getActiveSession(Player player) {
      return Optional.ofNullable((NavigationManager.NavigationSession)this.activeSessions.get(player.getUniqueId()));
   }

   private void updateNavigation(Player player, GpsPoint point) {
      if (!player.isOnline()) {
         this.stopNavigation(player);
      } else {
         Location playerLocation = player.getLocation();
         World targetWorld = Bukkit.getWorld(point.getWorldName());
         if (targetWorld != null && targetWorld.getName().equals(playerLocation.getWorld().getName())) {
            double distance = point.distanceTo(playerLocation);
            int arrivalRadius = this.plugin.getConfig().getInt("navigation.arrival-radius", 5);
            String directionArrow;
            String title;
            if (distance <= (double)arrivalRadius) {
               directionArrow = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("messages.arrived-title", "&a✓"));
               title = ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("messages.arrived-subtitle", "&fВы достигли точки назначения!"));
               MessageUtil.sendTitle(player, directionArrow, title, 10, 70, 20);
               this.stopNavigation(player);
            } else {
               directionArrow = this.getDirectionArrow(playerLocation, point);
               String var10000 = String.valueOf(ChatColor.YELLOW);
               title = var10000 + directionArrow;
               var10000 = String.valueOf(ChatColor.WHITE);
               String subtitle = var10000 + "До точки: " + String.valueOf(ChatColor.YELLOW) + String.format("%.1f", distance) + String.valueOf(ChatColor.WHITE) + " блоков";
               MessageUtil.sendTitle(player, title, subtitle, 0, 25, 0);
            }
         } else {
            MessageUtil.sendTitle(player, String.valueOf(ChatColor.RED) + "⚠", String.valueOf(ChatColor.RED) + "Вы находитесь в другом мире!", 0, 20, 0);
         }
      }
   }

   private String getDirectionArrow(Location playerLocation, GpsPoint point) {
      double dx = point.getX() - playerLocation.getX();
      double dy = point.getY() - playerLocation.getY();
      double dz = point.getZ() - playerLocation.getZ();
      double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
      if (horizontalDistance < 2.0D) {
         if (dy > 2.0D) {
            return "↑";
         }

         if (dy < -2.0D) {
            return "↓";
         }
      }

      double yaw = (double)playerLocation.getYaw();
      double angleToPoint = Math.atan2(-dx, dz);
      double angleToPointDegrees = Math.toDegrees(angleToPoint);
      if (angleToPointDegrees < 0.0D) {
         angleToPointDegrees += 360.0D;
      }

      double difference = (angleToPointDegrees - yaw) % 360.0D;
      if (difference < 0.0D) {
         difference += 360.0D;
      }

      if (!(difference >= 337.5D) && !(difference < 22.5D)) {
         if (difference >= 22.5D && difference < 67.5D) {
            return "↗";
         } else if (difference >= 67.5D && difference < 112.5D) {
            return "→";
         } else if (difference >= 112.5D && difference < 157.5D) {
            return "↘";
         } else if (difference >= 157.5D && difference < 202.5D) {
            return "↓";
         } else if (difference >= 202.5D && difference < 247.5D) {
            return "↙";
         } else {
            return difference >= 247.5D && difference < 292.5D ? "←" : "↖";
         }
      } else {
         return "↑";
      }
   }

   public static class NavigationSession {
      private final UUID playerId;
      private final GpsPoint targetPoint;
      private int taskId;

      public NavigationSession(UUID playerId, GpsPoint targetPoint) {
         this.playerId = playerId;
         this.targetPoint = targetPoint;
      }

      public UUID getPlayerId() {
         return this.playerId;
      }

      public GpsPoint getTargetPoint() {
         return this.targetPoint;
      }

      public int getTaskId() {
         return this.taskId;
      }

      public void setTaskId(int taskId) {
         this.taskId = taskId;
      }
   }
}
    
