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

import java.util.HashSet;
import java.util.Set;

/**
 * Contains all of the information about an idividual node in the graph produced
 * by SingletonDetector. Stores the color, shape and label of the node, as well
 * as all pointers to all adjacent nodes.
 * 
 * @author David Rubel
 */
public class OutputNode {
  // Background color of the node
  protected String fillColor;

  // Text color of the node
  protected String textColor;

  // Shape of the node
  protected String shape;

  // The Name
  private String name;

  // The set of all nodes that this node uses
  private Set<OutputNode> adjacent = new HashSet<OutputNode>();

  /**
   * Default constructor, sets only the classname. It is up to subclasses to
   * reassign colors and shape.
   * 
   * @param className The name of the class this node represents
   */
  public OutputNode(String className) {
    name = className;
  }

  /**
   * Returns the class name of this node.
   * 
   * @return Class name
   */
  public String getClassName() {
    return name;
  }

  /**
   * Returns the fill color of this node.
   * 
   * @return Fill color
   */
  public String getFillColor() {
    return fillColor;
  }

  /**
   * Adds an edge from this node to the dest node, implying that this class
   * accesses the dest class.
   * 
   * @param dest The node to point to
   */
  public void addEdge(OutputNode dest) {
    adjacent.add(dest);
  }

  /**
   * Splits the string at the last '/', replacing it with a new line and
   * swapping the beginning and ending. This is used to shorten the width of
   * nodes, by placing the name of the class at top of the node and its path at
   * the bottom.
   * 
   * @param str The string to separate
   * @return The separated string
   */
  public String separate(String str) {
    String ret = str;
    if (ret.indexOf('/') >= 0) {
      ret = str.substring(str.lastIndexOf('/') + 1, str.length());
      ret += "\\n" + str.substring(0, str.lastIndexOf('/'));
    }
    return ret;
  }

  /**
   * Returns the class name in a form better suited for a node label. This
   * method is overriden to produce different labels (i.e. Hingleton labels,
   * which include more information).
   * 
   * @return The 'label' form of the class name
   */
  public String getLabel() {
    return separate(name);
  }

  /**
   * Gets this node in GraphML format, using formatting specific to yEd (a graph
   * viewer/editor).
   * 
   * @return String of GraphML representing this node
   */
  public String getGraphMlNode() {
    String ret;
    ret =
        "    <node id=\"" + name + "\">\n" + "      <data key=\"d0\">\n"
            + "        <y:ShapeNode>\n" + "          <y:Fill color = \"#"
            + fillColor + "\"/>\n" + "          <y:NodeLabel textColor=\"#"
            + textColor + "\">"
            + getLabel().replace("\\n", "&#xA;").replace("/", ".")
            + "</y:NodeLabel>\n" + "          <y:Shape type=\"" + shape
            + "\"/>\n" + "        </y:ShapeNode>\n" + "      </data>\n"
            + "    </node>\n";
    return ret;
  }

  /**
   * Gets all edges originating from this node in GraphML format, using
   * formattinc specific to yEd (a graph viewer/editor).
   * 
   * @return String of GraphML represting all edges originating from this node
   */
  public String getGraphMlEdges() {
    String ret = "";

    for (OutputNode node : adjacent) {
      ret +=
          "" + "    <edge source=\"" + name + "\" target=\"" + node.name
              + "\">\n" + "      <data key=\"d1\">\n"
              + "        <y:PolyLineEdge>\n"
              + "          <y:LineStyle color = \"#" + node.getFillColor()
              + "\"/>\n"
              + "          <y:Arrows source=\"none\" target=\"standard\"/>\n"
              + "        </y:PolyLineEdge>\n" + "      </data>\n"
              + "    </edge>\n";
    }

    return ret;
  }
}
