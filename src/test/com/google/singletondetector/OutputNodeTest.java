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

import com.google.singletondetector.output.FingletonOutputNode;
import com.google.singletondetector.output.HingletonOutputNode;
import com.google.singletondetector.output.MingletonOutputNode;
import com.google.singletondetector.output.OtherOutputNode;
import com.google.singletondetector.output.OutputNode;
import com.google.singletondetector.output.SingletonOutputNode;

import junit.framework.TestCase;

public class OutputNodeTest extends TestCase {

  public void testChop() {
    OutputNode chopper = new OutputNode("chopper");
    assertEquals("sub1\\ntest/p1", chopper.separate("test/p1/sub1"));
    assertEquals("testp1", chopper.separate("testp1"));
  }

  public void testGetGraphMlEdges() {
    String expectedOutput =
        "    <edge source='source' target='dest1'>\n"
            + "      <data key='d1'>\n" + "        <y:PolyLineEdge>\n"
            + "          <y:LineStyle color = '#CCFFFF'/>\n"
            + "          <y:Arrows source='none' target='standard'/>\n"
            + "        </y:PolyLineEdge>\n" + "      </data>\n"
            + "    </edge>\n";
    OutputNode source = new OtherOutputNode("source");
    OutputNode dest1 = new OtherOutputNode("dest1");
    source.addEdge(dest1);
    String edgeOutput = source.getGraphMlEdges().replace("\"", "'");
    assertEquals(edgeOutput, expectedOutput);
  }

  public void testGetGraphMlNodeForOutputNode() {
    String expectedOutput =
        "    <node id='te\\nst'>\n"
            + "      <data key='d0'>\n"
            + "        <y:ShapeNode>\n"
            + "          <y:Fill color = '#CCFFFF'/>\n"
            + "          <y:NodeLabel textColor='#000000'>te&#xA;st</y:NodeLabel>\n"
            + "          <y:Shape type='ellipse'/>\n"
            + "        </y:ShapeNode>\n" + "      </data>\n" + "    </node>\n";
    OutputNode node = new OtherOutputNode("te\\nst");
    String nodeOutput = node.getGraphMlNode().replace("\"", "'");
    assertEquals(nodeOutput, expectedOutput);
  }

  public void testGetGraphMlNodeForSingletonOutputNode() {
    String expectedOutput =
        "    <node id='te\\nst'>\n"
            + "      <data key='d0'>\n"
            + "        <y:ShapeNode>\n"
            + "          <y:Fill color = '#FF0000'/>\n"
            + "          <y:NodeLabel textColor='#FFFFFF'>te&#xA;st</y:NodeLabel>\n"
            + "          <y:Shape type='rectangle'/>\n"
            + "        </y:ShapeNode>\n" + "      </data>\n" + "    </node>\n";
    OutputNode node = new SingletonOutputNode("te\\nst");
    String nodeOutput = node.getGraphMlNode().replace("\"", "'");
    assertEquals(nodeOutput, expectedOutput);
  }

  public void testGetGraphMlNodeForHingletonOutputNode() {
    String expectedOutput =
        "    <node id='te\\nst'>\n"
            + "      <data key='d0'>\n"
            + "        <y:ShapeNode>\n"
            + "          <y:Fill color = '#FF9900'/>\n"
            + "          <y:NodeLabel textColor='#000000'>te&#xA;st&#xA;(test)&#xA;(hingle)</y:NodeLabel>\n"
            + "          <y:Shape type='rectangle'/>\n"
            + "        </y:ShapeNode>\n" + "      </data>\n" + "    </node>\n";
    OutputNode node = new HingletonOutputNode("te\\nst", "test\\nhingle");
    String nodeOutput = node.getGraphMlNode().replace("\"", "'");
    assertEquals(nodeOutput, expectedOutput);
  }

  public void testGetGraphMlNodeForMingletonOutputNode() {
    String expectedOutput =
        "    <node id='te\\nst'>\n"
            + "      <data key='d0'>\n"
            + "        <y:ShapeNode>\n"
            + "          <y:Fill color = '#FFFF00'/>\n"
            + "          <y:NodeLabel textColor='#000000'>te&#xA;st</y:NodeLabel>\n"
            + "          <y:Shape type='rectangle'/>\n"
            + "        </y:ShapeNode>\n" + "      </data>\n" + "    </node>\n";
    OutputNode node = new MingletonOutputNode("te\\nst");
    String nodeOutput = node.getGraphMlNode().replace("\"", "'");
    assertEquals(nodeOutput, expectedOutput);
  }

  public void testGetGraphMlNodeForFingletonOutputNode() {
    String expectedOutput =
        "    <node id='te\\nst'>\n"
            + "      <data key='d0'>\n"
            + "        <y:ShapeNode>\n"
            + "          <y:Fill color = '#00FF00'/>\n"
            + "          <y:NodeLabel textColor='#000000'>te&#xA;st</y:NodeLabel>\n"
            + "          <y:Shape type='rectangle'/>\n"
            + "        </y:ShapeNode>\n" + "      </data>\n" + "    </node>\n";
    OutputNode node = new FingletonOutputNode("te\\nst");
    String nodeOutput = node.getGraphMlNode().replace("\"", "'");
    assertEquals(nodeOutput, expectedOutput);
  }

  public void testGetGraphMlNodeForOtherOutputNode() {
    String expectedOutput =
        "    <node id='te\\nst'>\n"
            + "      <data key='d0'>\n"
            + "        <y:ShapeNode>\n"
            + "          <y:Fill color = '#CCFFFF'/>\n"
            + "          <y:NodeLabel textColor='#000000'>te&#xA;st</y:NodeLabel>\n"
            + "          <y:Shape type='ellipse'/>\n"
            + "        </y:ShapeNode>\n" + "      </data>\n" + "    </node>\n";
    OutputNode node = new OtherOutputNode("te\\nst");
    String nodeOutput = node.getGraphMlNode().replace("\"", "'");
    assertEquals(nodeOutput, expectedOutput);
  }
}
