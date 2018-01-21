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
package com.google.singletondetector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single class from the input set. Stores information determining
 * whether this class is a special class (i.e. Singleton, Hingleton, etc.),
 * relevant information (classIHingle for Hingletons), and a set of classes it
 * uses, as well as information about the actual class read by ASM.
 * 
 * @author David Rubel
 */
public class Clazz {
  // The name of the class, stored as the full package name minus a prefix
  private final String name;
  
  // If this class is a hingleton, this is the name of the class it "hingles"
  private String classIHingle = null;
  
  // Flags storing the type of class
  private boolean isSingleton = false;
  private boolean isHingleton = false;
  private boolean isMingleton = false;
  private boolean isFingleton = false;
  
  // Map of all of the static fields inside the class this object represents
  private final HashMap<String, String> staticFields =
      new HashMap<String, String>();

  // A set of classes which are referenced by this class
  private Set<Clazz> classesIUse = new HashSet<Clazz>();
  
  // A set of classes which reference this class
  private Set<Clazz> classesUsingMe = new HashSet<Clazz>();

  // 0 if not drawn, 1 or 2 if drawn in 1st/2nd pass
  private int drawn = 0;

  public Clazz(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addStaticField(String fieldName, String fieldType) {
    staticFields.put(fieldName, fieldType);

  }

  public void visitStaticMethod(String methodName, String params,
      String returnType, Flags flags) {
    boolean returnsSelf = returnType.equals(name);

    if (!flags.ignoreSingletons() && returnsSelf && staticFields.values().contains(name)) {
      isSingleton = true;
    } else if (!flags.ignoreHingletons() && staticFields.values().contains(returnType)) {
      isHingleton = true;
      classIHingle = returnType;
    } else if (!flags.ignoreMingletons() && params.equals("")) {
      isMingleton = true;
    }
  }

  public void visitStaticField(String fieldName, String fieldType, Flags flags) {
    // Remove leading '['
    if (fieldType.startsWith("[")) {
      fieldType = fieldType.substring(1);
    }

    if (!flags.ignoreFingletons() && fieldType.startsWith("L")
        && !fieldType.startsWith("Ljava/")) {
      isFingleton = true;
    }
  }
  
  public void setIsDrawn(int threshold) {
    if (isSpecial() || !classesIUse.isEmpty()) {
      if (classesUsingMe.size() >= threshold) {
        drawn = 1;
      }
    }
  }
  
  public void updateIsDrawn() {
    if (drawn == 0) {
      for (Clazz cl : classesIUse) {
        if (cl.drawn == 1) {
          drawn = 2;
          break;
        }
      }
    }
  }
  
  public boolean isDrawn() {
    return drawn != 0;
  }

  public boolean isSpecial() {
    return isSingleton || isHingleton || isMingleton || isFingleton;
  }

  public boolean isSingleton() {
    return isSingleton;
  }

  public boolean isHingleton() {
    return isHingleton;
  }

  public boolean isMingleton() {
    return isMingleton;
  }

  public boolean isFingleton() {
    return isFingleton;
  }

  public void addClassIUse(Clazz clazz) {
    classesIUse.add(clazz);
    clazz.classesUsingMe.add(this);
  }

  public boolean uses(Clazz clazz) {
    return classesIUse.contains(clazz);
  }

  public Set<Clazz> getClassesIUse() {
    return classesIUse;
  }

  public boolean doIHingle(String className) {
    boolean ret = false;
    if (classIHingle != null) {
      ret = className.equals(classIHingle);
    }
    return ret;
  }

  public String getClassIHingle() {
    return classIHingle;
  }

  @Override
  public String toString() {
    return name;
  }



}
