    package com.rkin1234t.gpspl.command;

import com.rkin1234t.gpspl.GpsPL;
import com.rkin1234t.gpspl.data.GpsPoint;
import com.rkin1234t.gpspl.manager.NavigationManager;
import com.rkin1234t.gpspl.manager.PointManager;
import com.rkin1234t.gpspl.util.MessageUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GpsCommand implements CommandExecutor, TabCompleter {
   private final GpsPL plugin;
   private final PointManager pointManager;
   private final NavigationManager navigationManager;
   private static final List<String> SUBCOMMANDS = Arrays.asList("create", "delete", "reload", "goto", "gotoc", "stop", "cancel", "list");
   private static final List<String> ADMIN_SUBCOMMANDS = Arrays.asList("create", "delete", "reload", "list");

   public GpsCommand(GpsPL plugin) {
      this.plugin = plugin;
      this.pointManager = plugin.getPointManager();
      this.navigationManager = plugin.getNavigationManager();
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
      if (!(sender instanceof Player)) {
         sender.sendMessage("Эта команда доступна только для игроков!");
         return true;
      } else {
         Player player = (Player)sender;
         if (args.length == 0) {
            this.showHelp(player);
            return true;
         } else {
            String subCommand = args[0].toLowerCase();
            byte var8 = -1;
            switch(subCommand.hashCode()) {
            case -1367724422:
               if (subCommand.equals("cancel")) {
                  var8 = 6;
               }
               break;
            case -1352294148:
               if (subCommand.equals("create")) {
                  var8 = 0;
               }
               break;
            case -1335458389:
               if (subCommand.equals("delete")) {
                  var8 = 1;
               }
               break;
            case -934641255:
               if (subCommand.equals("reload")) {
                  var8 = 2;
               }
               break;
            case 3178851:
               if (subCommand.equals("goto")) {
                  var8 = 3;
               }
               break;
            case 3322014:
               if (subCommand.equals("list")) {
                  var8 = 7;
               }
               break;
            case 3540994:
               if (subCommand.equals("stop")) {
                  var8 = 5;
               }
               break;
            case 98544480:
               if (subCommand.equals("gotoc")) {
                  var8 = 4;
               }
            }

            switch(var8) {
            case 0:
               this.handleCreate(player, args);
               break;
            case 1:
               this.handleDelete(player, args);
               break;
            case 2:
               this.handleReload(player);
               break;
            case 3:
               this.handleGoto(player, args);
               break;
            case 4:
               this.handleGotoCoords(player, args);
               break;
            case 5:
            case 6:
               this.handleStop(player);
               break;
            case 7:
               this.handleList(player);
               break;
            default:
               this.showHelp(player);
            }

            return true;
         }
      }
   }

   private void handleCreate(Player player, String[] args) {
      if (!player.hasPermission("gpspl.create")) {
         MessageUtil.sendMessage(player, "no-permission");
      } else if (args.length < 2) {
         player.sendMessage("§cИспользование: /" + (player.hasPermission("gpspl.admin") ? "gpspl" : "gps") + " create <название>");
      } else {
         String pointName = args[1];
         if (this.pointManager.pointExists(pointName)) {
            MessageUtil.sendMessage(player, "point-already-exists", "name", pointName);
         } else {
            boolean success = this.pointManager.createPoint(pointName, player.getLocation());
            if (success) {
               MessageUtil.sendMessage(player, "point-created", "name", pointName);
            } else {
               player.sendMessage("§cНе удалось создать точку!");
            }

         }
      }
   }

   private void handleDelete(Player player, String[] args) {
      if (!player.hasPermission("gpspl.delete")) {
         MessageUtil.sendMessage(player, "no-permission");
      } else if (args.length < 2) {
         player.sendMessage("§cИспользование: /" + (player.hasPermission("gpspl.admin") ? "gpspl" : "gps") + " delete <название>");
      } else {
         String pointName = args[1];
         boolean success = this.pointManager.deletePoint(pointName);
         if (success) {
            MessageUtil.sendMessage(player, "point-deleted", "name", pointName);
         } else {
            MessageUtil.sendMessage(player, "point-not-found", "name", pointName);
         }

      }
   }

   private void handleReload(Player player) {
      if (!player.hasPermission("gpspl.reload")) {
         MessageUtil.sendMessage(player, "no-permission");
      } else {
         this.plugin.reloadConfig();
         this.pointManager.loadPoints();
         MessageUtil.sendMessage(player, "plugin-reloaded");
      }
   }

   private void handleGoto(Player player, String[] args) {
      if (args.length < 2) {
         player.sendMessage("§cИспользование: /" + (player.hasPermission("gpspl.admin") ? "gpspl" : "gps") + " goto <название>");
      } else {
         String pointName = args[1];
         Optional<GpsPoint> optionalPoint = this.pointManager.getPoint(pointName);
         if (!optionalPoint.isPresent()) {
            MessageUtil.sendMessage(player, "point-not-found", "name", pointName);
         } else {
            GpsPoint point = (GpsPoint)optionalPoint.get();
            boolean success = this.navigationManager.startNavigation(player, point);
            if (success) {
               MessageUtil.sendMessage(player, "navigation-started", "name", pointName);
               String cmdPrefix = player.hasPermission("gpspl.admin") ? "gpspl" : "gps";
               player.sendMessage("§7Чтобы отменить навигацию, используйте команды: §f/" + cmdPrefix + " stop §7или §f/" + cmdPrefix + " cancel");
            } else {
               player.sendMessage("§cНе удалось начать навигацию!");
            }

         }
      }
   }

   private void handleGotoCoords(Player player, String[] args) {
      if (args.length < 4) {
         player.sendMessage("§cИспользование: /" + (player.hasPermission("gpspl.admin") ? "gpspl" : "gps") + " gotoc <x> <y> <z>");
      } else {
         try {
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            boolean success = this.navigationManager.startNavigationToCoords(player, x, y, z);
            if (success) {
               MessageUtil.sendMessage(player, "navigation-started-coords", "x", String.format("%.1f", x), "y", String.format("%.1f", y), "z", String.format("%.1f", z));
               String cmdPrefix = player.hasPermission("gpspl.admin") ? "gpspl" : "gps";
               player.sendMessage("§7Чтобы отменить навигацию, используйте команды: §f/" + cmdPrefix + " stop §7или §f/" + cmdPrefix + " cancel");
            } else {
               player.sendMessage("§cНе удалось начать навигацию!");
            }
         } catch (NumberFormatException var11) {
            player.sendMessage("§cКоординаты должны быть числами!");
         }

      }
   }

   private void handleStop(Player player) {
      boolean success = this.navigationManager.stopNavigation(player);
      if (success) {
         MessageUtil.sendMessage(player, "navigation-stopped");
      } else {
         MessageUtil.sendMessage(player, "navigation-no-active");
      }

   }

   private void handleList(Player player) {
      if (!player.hasPermission("gpspl.list")) {
         MessageUtil.sendMessage(player, "no-permission");
      } else {
         Collection<GpsPoint> points = this.pointManager.getAllPoints();
         if (points.isEmpty()) {
            MessageUtil.sendMessage(player, "list-empty");
         } else {
            MessageUtil.sendMessage(player, "list-header");
            Iterator var3 = points.iterator();

            while(var3.hasNext()) {
               GpsPoint point = (GpsPoint)var3.next();
               MessageUtil.sendMessage(player, "list-item", "name", point.getName(), "x", String.format("%.1f", point.getX()), "y", String.format("%.1f", point.getY()), "z", String.format("%.1f", point.getZ()));
            }

            MessageUtil.sendMessage(player, "list-footer");
         }
      }
   }

   private void showHelp(Player player) {
      player.sendMessage("§e=== Помощь по GpsPL ===");
      String cmdPrefix = player.hasPermission("gpspl.admin") ? "gpspl" : "gps";
      if (player.hasPermission("gpspl.create")) {
         player.sendMessage("§7/" + cmdPrefix + " create <название> §f- Создать GPS точку");
      }

      if (player.hasPermission("gpspl.delete")) {
         player.sendMessage("§7/" + cmdPrefix + " delete <название> §f- Удалить GPS точку");
      }

      if (player.hasPermission("gpspl.reload")) {
         player.sendMessage("§7/" + cmdPrefix + " reload §f- Перезагрузить плагин");
      }

      player.sendMessage("§7/" + cmdPrefix + " goto <название> §f- Начать навигацию к точке");
      player.sendMessage("§7/" + cmdPrefix + " gotoc <x> <y> <z> §f- Начать навигацию к координатам");
      player.sendMessage("§7/" + cmdPrefix + " stop/cancel §f- Остановить навигацию");
      if (player.hasPermission("gpspl.list")) {
         player.sendMessage("§7/" + cmdPrefix + " list §f- Показать список всех GPS точек");
      }

      player.sendMessage("§e=======================");
   }

   @Nullable
   public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
      if (!(sender instanceof Player)) {
         return new ArrayList();
      } else {
         Player player = (Player)sender;
         if (args.length == 1) {
            return (List)SUBCOMMANDS.stream().filter((subCommandx) -> {
               return !ADMIN_SUBCOMMANDS.contains(subCommandx) || player.hasPermission("gpspl." + subCommandx);
            }).filter((subCommandx) -> {
               return subCommandx.startsWith(args[0].toLowerCase());
            }).collect(Collectors.toList());
         } else {
            if (args.length == 2) {
               String subCommand = args[0].toLowerCase();
               if ((subCommand.equals("delete") || subCommand.equals("goto")) && (subCommand.equals("goto") || player.hasPermission("gpspl.delete"))) {
                  return (List)this.pointManager.getAllPoints().stream().map(GpsPoint::getName).filter((name) -> {
                     return name.toLowerCase().startsWith(args[1].toLowerCase());
                  }).collect(Collectors.toList());
               }
            }

            return new ArrayList();
         }
      }
   }
}
    
