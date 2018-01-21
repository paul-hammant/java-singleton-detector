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
 * Simple class which stores flags for a SingletonDetector. Each boolean can be
 * set or read.
 * 
 * @author David Rubel
 */
public class Flags {
  private boolean verbose = false;
  private boolean showStats = false;
  private boolean showBanner = false;
  private boolean ignoreSingletons = false;
  private boolean ignoreHingletons = false;
  private boolean ignoreMingletons = false;
  private boolean ignoreFingletons = false;
  private boolean ignoreOthers = false;
  private int threshold = -1;

  public boolean isVerbose() {
    return verbose;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  public boolean showStats() {
    return showStats;
  }

  public void setShowStats(boolean showStats) {
    this.showStats = showStats;
  }

  public boolean showBanner() {
    return showBanner;
  }

  public void setShowBanner(boolean showBanner) {
    this.showBanner = showBanner;
  }

  public boolean ignoreSingletons() {
    return ignoreSingletons;
  }

  public void setIgnoreSingletons(boolean ignoreSingletons) {
    this.ignoreSingletons = ignoreSingletons;
  }

  public boolean ignoreHingletons() {
    return ignoreHingletons;
  }

  public void setIgnoreHingletons(boolean ignoreHingletons) {
    this.ignoreHingletons = ignoreHingletons;
  }

  public boolean ignoreMingletons() {
    return ignoreMingletons;
  }

  public void setIgnoreMingletons(boolean ignoreMingletons) {
    this.ignoreMingletons = ignoreMingletons;
  }

  public boolean ignoreFingletons() {
    return ignoreFingletons;
  }

  public void setIgnoreFingletons(boolean ignoreFingletons) {
    this.ignoreFingletons = ignoreFingletons;
  }

  public boolean ignoreOthers() {
    return ignoreOthers;
  }

  public void setIgnoreOthers(boolean ignoreOthers) {
    this.ignoreOthers = ignoreOthers;
  }

  public int getThreshold() {
    return threshold;
  }

  public void setThreshold(int threshold) {
    this.threshold = threshold;
  }
}
