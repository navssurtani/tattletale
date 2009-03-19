/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.tattletale.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Archive
 * @author Jesper Pedersen <jesper.pedersen@jboss.org>
 */
public abstract class Archive implements Serializable, Comparable
{
   /** SerialVersionUID */
   //static final long serialVersionUID = 6584569765298595390L;

   /** Archve type */
   private int type;

   /** The name */
   private String name;

   /** Manifest */
   private List<String> manifest;

   /** Requires */
   private SortedSet<String> requires;

   /** Provides */
   private SortedMap<String, Long> provides;

   /** Package dependencies */
   private SortedMap<String, SortedSet<String>> packageDependencies;

   /** Locations */
   private SortedSet<Location> locations;

   /** OSGi archive */
   private transient Boolean osgi;

   /**
    * Constructor
    * @param type The type
    * @param name The name
    * @param manifest The manifest
    * @param requires The requires
    * @param provides The provides
    * @param packageDependencies The package dependencies
    * @param location The location
    */
   public Archive(int type, 
                  String name, 
                  List<String> manifest, 
                  SortedSet<String> requires, 
                  SortedMap<String, Long> provides, 
                  SortedMap<String, SortedSet<String>> packageDependencies,
                  Location location)
   {
      this.type = type;
      this.name = name;
      this.manifest = manifest;
      this.requires = requires;
      this.provides = provides;
      this.packageDependencies = packageDependencies;
      this.locations = new TreeSet<Location>();
      this.osgi = null;

      if (location != null)
         this.locations.add(location);
   }

   /**
    * Get the type
    * @return The value
    */
   public int getType()
   {
      return type;
   }

   /**
    * Get the name
    * @return The value
    */
   public String getName()
   {
      return name;
   }

   /**
    * Get the manifest
    * @return The value
    */
   public List<String> getManifest()
   {
      return manifest;
   }

   /**
    * Has manifest key
    * @param key The manifest key
    * @return True if the key is found; otherwise false
    */
   public boolean hasManifestKey(String key)
   {
      if (manifest != null)
      {
         for (String s : manifest)
         {
            if (s.startsWith(key))
               return true;
         }
      }

      return false;
   }

   /**
    * Get manifest value
    * @param key The manifest key
    * @return The value; <code>null</code> if not found
    */
   public String getManifestValue(String key)
   {
      if (manifest != null)
      {
         StringBuffer value = new StringBuffer();
         boolean found = false;

         Iterator<String> it = manifest.iterator();
         while (it.hasNext())
         {
            String s = it.next();
            if (s.startsWith(key))
            {
               int idx = s.indexOf(":");
               value = value.append(s.substring(idx + 1).trim());
               found = true;
            } 
            else if (found)
            {
               int idx = s.indexOf(":");
               if (idx != -1)
               {
                  return value.toString().trim();
               }
               else
               {
                  value = value.append(s.trim());
               }
            }
         }
      }

      return null;
   }

   /**
    * Get the requires
    * @return The value
    */
   public SortedSet<String> getRequires()
   {
      return requires;
   }

   /**
    * Get the provides
    * @return The value
    */
   public SortedMap<String, Long> getProvides()
   {
      return provides;
   }

   /**
    * Get the package dependencies
    * @return The value
    */
   public SortedMap<String, SortedSet<String>> getPackageDependencies()
   {
      return packageDependencies;
   }

   /**
    * Get the locations
    * @return The value
    */
   public SortedSet<Location> getLocations()
   {
      return locations;
   }

   /**
    * Add a location
    * @param value The value
    */
   public void addLocation(Location value)
   {
      locations.add(value);
   }

   /**
    * Does the archives provide this class
    * @param clz The class name
    * @return True if the class is provided; otherwise false
    */
   public boolean doesProvide(String clz)
   {
      return provides.containsKey(clz);
   }

   /**
    * Is this an OSGi archive ?
    * @return True if OSGi; otherwise false
    */
   public boolean isOSGi()
   {
      if (osgi == null)
         initOSGi();

      return osgi.booleanValue();
   }

   /**
    * Init OSGi
    */
   private void initOSGi()
   {
      osgi = hasManifestKey("Bundle-SymbolicName");
   }

   /**
    * Comparable
    * @param o The other object
    * @return The compareTo value
    */
   public int compareTo(Object o)
   {
      Archive a = (Archive)o;

      return name.compareTo(a.getName());
   }

   /**
    * Equals
    * @param obj The other object
    * @return True if equals; otherwise false
    */
   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof Archive))
      {
         return false;
      }

      Archive a = (Archive)obj;

      return name.equals(a.getName());
   }

   /**
    * Hash code
    * @return The hash code
    */
   public int hashCode()
   {
      return 7 + 31 * name.hashCode();
   }

   /**
    * String representation
    * @return The string
    */
   public String toString()
   {
      StringBuffer sb = new StringBuffer();

      sb = sb.append(getClass().getName());
      sb = sb.append("(\n");

      sb = sb.append("type=");
      sb = sb.append(type);
      sb = sb.append("\n");

      sb = sb.append("name=");
      sb = sb.append(name);
      sb = sb.append("\n");

      sb = sb.append("manifest=");
      sb = sb.append(manifest);
      sb = sb.append("\n");

      sb = sb.append("requires=");
      sb = sb.append(requires);
      sb = sb.append("\n");

      sb = sb.append("provides=");
      sb = sb.append(provides);
      sb = sb.append("\n");

      sb = sb.append("packagedependencies=");
      sb = sb.append(packageDependencies);
      sb = sb.append("\n");

      sb = sb.append("locations=");
      sb = sb.append(locations);
      sb = sb.append("\n");

      sb = sb.append(")");

      return sb.toString();
   }
}
