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
package com.google.singletondetector.output;

/**
 * The OutputNode representation of a Singleton. A singleton is defined as a
 * class which enfoces its own singularity.
 * 
 * @author David Rubel
 */
public class SingletonOutputNode extends OutputNode {

  /**
   * Default constructor. Assigns the appropriate values to color and shape to
   * represent a Singleton.
   * 
   * @param className Name of the class this node represents
   */
  public SingletonOutputNode(String className) {
    super(className);
    fillColor = "FF0000";
    textColor = "FFFFFF";
    shape = "rectangle";
  }

}
