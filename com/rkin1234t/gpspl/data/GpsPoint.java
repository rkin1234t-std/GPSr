    package com.rkin1234t.gpspl.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

@SerializableAs("GpsPoint")
public class GpsPoint implements ConfigurationSerializable {
   private final String name;
   private final double x;
   private final double y;
   private final double z;
   private final String worldName;

   public GpsPoint(String name, Location location) {
      this.name = name;
      this.x = location.getX();
      this.y = location.getY();
      this.z = location.getZ();
      this.worldName = location.getWorld().getName();
   }

   public GpsPoint(String name, double x, double y, double z, String worldName) {
      this.name = name;
      this.x = x;
      this.y = y;
      this.z = z;
      this.worldName = worldName;
   }

   public String getName() {
      return this.name;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public String getWorldName() {
      return this.worldName;
   }

   public double distanceTo(Location location) {
      return !location.getWorld().getName().equals(this.worldName) ? Double.MAX_VALUE : Math.sqrt(Math.pow(location.getX() - this.x, 2.0D) + Math.pow(location.getY() - this.y, 2.0D) + Math.pow(location.getZ() - this.z, 2.0D));
   }

   public Location toLocation(World world) {
      return new Location(world, this.x, this.y, this.z);
   }

   @NotNull
   public Map<String, Object> serialize() {
      Map<String, Object> result = new HashMap();
      result.put("name", this.name);
      result.put("x", this.x);
      result.put("y", this.y);
      result.put("z", this.z);
      result.put("world", this.worldName);
      return result;
   }

   public static GpsPoint deserialize(Map<String, Object> map) {
      String name = (String)map.get("name");
      double x = ((Number)map.get("x")).doubleValue();
      double y = ((Number)map.get("y")).doubleValue();
      double z = ((Number)map.get("z")).doubleValue();
      String worldName = (String)map.get("world");
      return new GpsPoint(name, x, y, z, worldName);
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         GpsPoint gpsPoint = (GpsPoint)o;
         return Objects.equals(this.name, gpsPoint.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name});
   }
}
    
