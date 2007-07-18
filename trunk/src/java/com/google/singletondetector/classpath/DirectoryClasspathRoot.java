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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DirectoryClasspathRoot implements ClasspathRoot {

  private URLClassLoader classloader;
  private URL url;

  public DirectoryClasspathRoot(URL url) {
    this.url = url;
    classloader = new URLClassLoader(new URL[] {url}, null);
  }

  public InputStream getResourceAsStream(String resourceName) {
    return classloader.getResourceAsStream(resourceName);
  }

  public Collection<String> getResources(String packageName) {
    List<String> resources = new LinkedList<String>();
    File dir = new File(url.getFile() + "/" + packageName);
    File[] files = dir.listFiles();
    if (files == null) {
      return resources;
    }
    for (File file : files) {
      resources.add(file.getName());
    }
    return resources;
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
