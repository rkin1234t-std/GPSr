    package com.rkin1234t.gpspl.manager;

import com.rkin1234t.gpspl.GpsPL;
import com.rkin1234t.gpspl.data.GpsPoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class PointManager {
   private final GpsPL plugin;
   private final Map<String, GpsPoint> points = new HashMap();

   public PointManager(GpsPL plugin) {
      this.plugin = plugin;
      this.loadPoints();
   }

   public void loadPoints() {
      this.points.clear();
      ConfigurationSection pointsSection = this.plugin.getConfig().getConfigurationSection("points");
      if (pointsSection != null) {
         Iterator var2 = pointsSection.getKeys(false).iterator();

         while(var2.hasNext()) {
            String pointName = (String)var2.next();
            ConfigurationSection pointSection = pointsSection.getConfigurationSection(pointName);
            if (pointSection != null) {
               double x = pointSection.getDouble("x");
               double y = pointSection.getDouble("y");
               double z = pointSection.getDouble("z");
               String worldName = pointSection.getString("world");
               GpsPoint point = new GpsPoint(pointName, x, y, z, worldName);
               this.points.put(pointName, point);
            }
         }

      }
   }

   public void savePoints() {
      this.plugin.getConfig().set("points", (Object)null);
      Iterator var1 = this.points.values().iterator();

      while(var1.hasNext()) {
         GpsPoint point = (GpsPoint)var1.next();
         String path = "points." + point.getName();
         this.plugin.getConfig().set(path + ".x", point.getX());
         this.plugin.getConfig().set(path + ".y", point.getY());
         this.plugin.getConfig().set(path + ".z", point.getZ());
         this.plugin.getConfig().set(path + ".world", point.getWorldName());
      }

      this.plugin.saveConfig();
   }

   public boolean createPoint(String name, Location location) {
      if (this.points.containsKey(name)) {
         return false;
      } else {
         GpsPoint point = new GpsPoint(name, location);
         this.points.put(name, point);
         this.savePoints();
         return true;
      }
   }

   public boolean deletePoint(String name) {
      if (!this.points.containsKey(name)) {
         return false;
      } else {
         this.points.remove(name);
         this.savePoints();
         return true;
      }
   }

   public Optional<GpsPoint> getPoint(String name) {
      return Optional.ofNullable((GpsPoint)this.points.get(name));
   }

   public Collection<GpsPoint> getAllPoints() {
      return this.points.values();
   }

   public boolean pointExists(String name) {
      return this.points.containsKey(name);
   }
}
    
