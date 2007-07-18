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

/**
 * Simply class which contains statistics for a SingletonDetector. Allows each
 * variable to be incremented or read, and contains the logic to generate an
 * output string.
 * 
 * @author David Rubel
 */
public class Stats {
  public int classesRead;
  public int classesDrawn;
  public int singletons;
  public int hingletons;
  public int mingletons;
  public int fingletons;
  public int singletonUsers;
  public int hingletonUsers;
  public int mingletonUsers;
  public int fingletonUsers;

  public Stats() {
    classesRead = 0;
    classesDrawn = 0;
    singletons = 0;
    hingletons = 0;
    mingletons = 0;
    fingletons = 0;
    singletonUsers = 0;
    hingletonUsers = 0;
    mingletonUsers = 0;
    fingletonUsers = 0;
  }

  public int getClassesDrawn() {
    return classesDrawn;
  }

  public void incClassesDrawn() {
    classesDrawn++;
  }

  public int getClassesRead() {
    return classesRead;
  }

  public void incClassesRead() {
    classesRead++;
  }

  public int getFingletons() {
    return fingletons;
  }

  public void incFingletons() {
    fingletons++;
  }

  public int getFingletonUsers() {
    return fingletonUsers;
  }

  public void incFingletonUsers() {
    fingletonUsers++;
  }

  public int getHingletons() {
    return hingletons;
  }

  public void incHingletons() {
    hingletons++;
  }

  public int getHingletonUsers() {
    return hingletonUsers;
  }

  public void incHingletonUsers() {
    hingletonUsers++;
  }

  public int getMingletons() {
    return mingletons;
  }

  public void incMingletons() {
    mingletons++;
  }

  public int getMingletonUsers() {
    return mingletonUsers;
  }

  public void incMingletonUsers() {
    mingletonUsers++;
  }

  public int getSingletons() {
    return singletons;
  }

  public void incSingletons() {
    singletons++;
  }

  public int getSingletonUsers() {
    return singletonUsers;
  }

  public void incSingletonUsers() {
    singletonUsers++;
  }

  public String getOutput(Flags flags, boolean pad) {
    int size = pad ? 4 : 0;

    String ret = "Classes drawn: " + classesDrawn + " of " + classesRead;
    if (!flags.ignoreSingletons()) {
      ret +=
          "\nSingletons: " + pad(singletons, size) + "     Singleton users: "
              + pad(singletonUsers, size);
    }
    if (!flags.ignoreHingletons()) {
      ret +=
          "\nHingletons: " + pad(hingletons, size) + "     Hingleton users: "
              + pad(hingletonUsers, size);
    }
    if (!flags.ignoreMingletons()) {
      ret +=
          "\nMingletons: " + pad(mingletons, size) + "     Mingleton users: "
              + pad(mingletonUsers, size);
    }
    if (!flags.ignoreFingletons()) {
      ret +=
          "\nFingletons: " + pad(fingletons, size) + "     Fingleton users: "
              + pad(fingletonUsers, size);
    }
    return ret;
  }

  private String pad(int str, int size) {
    String pstr = "" + str;
    while (pstr.length() < size) {
      pstr = " " + pstr;
    }
    return pstr;
  }
}
