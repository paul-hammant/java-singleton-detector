/*
 * Copyright 2007 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.singletondetector.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClasspathRoot implements ClasspathRoot {

  private URLClassLoader classloader;
  private URL url;
  private Map<String, Set<String>> resourceNamesByPackage =
      new HashMap<String, Set<String>>();

  public JarClasspathRoot(URL url) {
    this.url = url;
    classloader = new URLClassLoader(new URL[] {url}, null);
    preloadNamesFromJar();
  }

  public InputStream getResourceAsStream(String resourceName) {
    return classloader.getResourceAsStream(resourceName);
  }

  public Collection<String> getResources(String packageName) {
    if (packageName.endsWith("/")) {
      packageName = packageName.substring(0, packageName.length() - 1);
    }
    Set<String> resources = resourceNamesByPackage.get(packageName);
    return resources == null ? new HashSet<String>() : resources;
  }

  public void preloadNamesFromJar() {
    Enumeration<JarEntry> enumeration = jarFileForUrl().entries();
    while (enumeration.hasMoreElements()) {
      JarEntry entry = enumeration.nextElement();
      String path = entry.getName();
      int index = Math.max(0, path.lastIndexOf('/'));
      String location = path.substring(0, index);
      String name = path.substring(index);
      name = name.replace("/", "");
      addName(location, name);
    }
  }

  private JarFile jarFileForUrl() {
    try {
      return new JarFile(url.toURI().getPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private void addName(String location, String name) {
    int slash = location.lastIndexOf("/");
    if (slash >= 0) {
      String child = location.substring(slash + 1);
      String parent = location.substring(0, slash);
      addName(parent, child);
    } else if (!location.equals("")) {
      addName("", location);
    }
    Set<String> names = resourceNamesByPackage.get(location);
    if (names == null) {
      names = new HashSet<String>();
      resourceNamesByPackage.put(location, names);
    }
    if (!name.equals("")) {
      names.add(name);
    }
  }

  @Override
  public String toString() {
    String url = this.url.toString();
    if (url.endsWith("/")) {
      url = url.substring(0, url.length() - 1);
    }
    int index = Math.max(0, url.lastIndexOf('/') + 1);
    return url.substring(index);
  }

}
