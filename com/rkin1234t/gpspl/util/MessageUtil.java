    package com.rkin1234t.gpspl.util;

import com.rkin1234t.gpspl.GpsPL;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtil {
   private static GpsPL plugin;

   public static void init(GpsPL plugin) {
      MessageUtil.plugin = plugin;
   }

   public static void sendMessage(CommandSender sender, String messageKey, Object... args) {
      String message = getMessage(messageKey, args);
      if (!message.isEmpty()) {
         sender.sendMessage(message);
      }

   }

   public static String getMessage(String messageKey, Object... args) {
      String message = plugin.getConfig().getString("messages." + messageKey);
      if (message != null && !message.isEmpty()) {
         if (!messageKey.equals("prefix")) {
            String prefix = plugin.getConfig().getString("messages.prefix", "");
            message = prefix + message;
         }

         for(int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
               String placeholder = "%" + String.valueOf(args[i]) + "%";
               String value = String.valueOf(args[i + 1]);
               message = message.replace(placeholder, value);
            }
         }

         return ChatColor.translateAlternateColorCodes('&', message);
      } else {
         return "";
      }
   }

   public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
      title = ChatColor.translateAlternateColorCodes('&', title);
      subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
      player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
   }
}
    
