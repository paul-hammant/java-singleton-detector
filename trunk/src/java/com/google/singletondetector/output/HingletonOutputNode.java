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
 * The OutputNode representation of a Hingleton. A hingleton is defined as a
 * class which makes another class into a singleton, enforcing that classes
 * singularity. This helper class is the "hingleton", and the class it turns
 * into a singleton is the "hingled" class. The name is short for "helper
 * singleton."
 * 
 * @author David Rubel
 */
public class HingletonOutputNode extends OutputNode {
  // Name of the class which this hingleton "hingles"
  private String classIHingle;

  /**
   * Default constructor. Assigns the appropriate values to color and shape to
   * represent a Hingleton, and sets the hingled class.
   * 
   * @param className Name of the class this node represents
   * @param classIHingle Name of the hingled class
   */
  public HingletonOutputNode(String className, String classIHingle) {
    super(className);
    fillColor = "FF9900";
    textColor = "000000";
    shape = "rectangle";
    this.classIHingle = classIHingle;
  }

  /**
   * Overrides the getLabel() method to inject a more complicated label into the
   * GraphML methods (a hingleton label includes the hingleton's name as well as
   * the hingled class's name).
   * 
   * @return The label for a hingleton
   */
  @Override
  public String getLabel() {
    String label = "(" + separate(classIHingle).replace("\\n", ")\\n(") + ")";
    return super.getLabel() + "\\n" + label;
  }

}
