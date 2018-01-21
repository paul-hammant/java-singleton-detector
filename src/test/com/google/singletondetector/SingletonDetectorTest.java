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

import com.google.singletondetector.Clazz;
import com.google.singletondetector.Flags;
import com.google.singletondetector.SingletonDetector;
import com.google.singletondetector.classpath.ClasspathRoot;
import com.google.singletondetector.classpath.DirectoryClasspathRoot;
import com.google.singletondetector.p1.FingletonOne;
import com.google.singletondetector.p1.FingletonUserOne;
import com.google.singletondetector.p1.HingletonOne;
import com.google.singletondetector.p1.MingletonOne;
import com.google.singletondetector.p1.NotAFingleton;
import com.google.singletondetector.p2.MingletonUserOne;
import com.google.singletondetector.p2.NotASingletonOne;
import com.google.singletondetector.p2.NotASingletonTwo;
import com.google.singletondetector.p2.SingletonOne;
import com.google.singletondetector.p2.SingletonUserOne;
import com.google.singletondetector.p3.EnumClass;
import com.google.singletondetector.p3.HingletonUserOne;
import com.google.singletondetector.p3.JavaHingleton;
import com.google.singletondetector.p3.NotReallyASingleton;
import com.google.singletondetector.p3.SingletonUserTwo;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class SingletonDetectorTest extends TestCase {
  // The SingletonDetector which is to be tested
  private SingletonDetector detector;

  // The common prefix for all test classes
  private String path = "com/google/singletondetector/";

  // The common classpath for all test classes
  private ClasspathRoot root;
  
  /**
   * Default constructor, sets classpath root
   */
  public SingletonDetectorTest() throws MalformedURLException {
    URL url = new File("target/test-classes").toURI().toURL();
    root = new DirectoryClasspathRoot(url);
  }

  /*************************************************************************/
  /*                           UTILITY METHODS                             */
  /*************************************************************************/

  private String getAsmStyleClassName(Class clazz) {
    return clazz.getName().replace('.', '/');
  }

  private String read(String resourceName) throws IOException {
    StringBuilder buf = new StringBuilder();
    InputStream is = getClass().getResourceAsStream(resourceName);
    int ch;
    while ((ch = is.read()) != -1) {
      buf.append((char) ch);
    }
    is.close();
    return buf.toString();
  }

  /*************************************************************************/
  /*                           SINGLETON TESTS                             */
  /*************************************************************************/

  public void testRemovePrefix() {
    detector = new SingletonDetector(root, "");
    detector.setPrefix("foo/test/");
    assertEquals("p1/sub1", detector.removePrefix("foo/test/p1/sub1"));
    assertEquals("foo/test2/p1/sub1", detector
        .removePrefix("foo/test2/p1/sub1"));
  }

  public void testIsSingletonOnANonSingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(NotASingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(NotASingletonOne.class));
    assertFalse(cl.isSingleton());
  }

  public void testIsSingletonOnASingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(SingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(SingletonOne.class));
    assertTrue(cl.isSingleton());
  }

  public void testIsSingletonOnAHingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(HingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(HingletonOne.class));
    assertFalse(cl.isSingleton());
  }

  public void testIsSingletonOnNotReallyASingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(NotReallyASingleton.class));
    Clazz cl =
        detector.getClass(getAsmStyleClassName(NotReallyASingleton.class));
    assertFalse(cl.isSingleton());
  }

  public void testCheckSingletonUsage() {
    Flags flags = new Flags();
    flags.setIgnoreHingletons(true);
    flags.setIgnoreMingletons(true);
    flags.setIgnoreFingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(SingletonOne.class),
            getAsmStyleClassName(SingletonUserOne.class));

    Clazz singletonOne =
        detector.getClass(getAsmStyleClassName(SingletonOne.class));
    Clazz singletonUserOne =
        detector.getClass(getAsmStyleClassName(SingletonUserOne.class));
    assertFalse(singletonOne.uses(singletonUserOne));
    assertTrue(singletonUserOne.uses(singletonOne));
  }

  public void testStaticUsesOfSingletonsAreDetected() {
    Flags flags = new Flags();
    flags.setIgnoreHingletons(true);
    flags.setIgnoreMingletons(true);
    flags.setIgnoreFingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(SingletonOne.class),
            getAsmStyleClassName(SingletonUserTwo.class));

    Clazz singletonOne =
        detector.getClass(getAsmStyleClassName(SingletonOne.class));
    Clazz singletonUserTwo =
        detector.getClass(getAsmStyleClassName(SingletonUserTwo.class));
    assertTrue(singletonUserTwo.uses(singletonOne));
  }

  /*************************************************************************/
  /*                           HINGLETON TESTS                             */
  /*************************************************************************/

  public void testIsHingletonOnANonHingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(NotASingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(NotASingletonOne.class));
    assertFalse(cl.isHingleton());
  }

  public void testIsHingletonOnAHingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(HingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(HingletonOne.class));
    assertTrue(cl.isHingleton());
  }

  public void testIsHingletonOnASingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(SingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(SingletonOne.class));
    assertFalse(cl.isHingleton());
  }

  public void testIsHingletonOnAJavaHingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(JavaHingleton.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(JavaHingleton.class));
    assertFalse(cl.isHingleton());
  }

  public void testCheckHingletonUsage() {
    Flags flags = new Flags();
    flags.setIgnoreSingletons(true);
    flags.setIgnoreMingletons(true);
    flags.setIgnoreFingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(HingletonOne.class),
            getAsmStyleClassName(HingletonUserOne.class));

    Clazz hingletonOne =
        detector.getClass(getAsmStyleClassName(HingletonOne.class));
    Clazz hingletonUserOne =
        detector.getClass(getAsmStyleClassName(HingletonUserOne.class));
    assertFalse(hingletonOne.uses(hingletonUserOne));
    assertTrue(hingletonUserOne.uses(hingletonOne));
  }

  /*************************************************************************/
  /*                           MINGLETON TESTS                             */
  /*************************************************************************/

  public void testIsMingletonOnAMingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(MingletonOne.class),
            getAsmStyleClassName(NotASingletonTwo.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(MingletonOne.class));
    assertTrue(cl.isMingleton());
  }

  public void testCheckMingletonUsage() {
    Flags flags = new Flags();
    flags.setIgnoreSingletons(true);
    flags.setIgnoreHingletons(true);
    flags.setIgnoreFingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(MingletonOne.class),
            getAsmStyleClassName(MingletonUserOne.class));

    Clazz mingletonOne =
        detector.getClass(getAsmStyleClassName(MingletonOne.class));
    Clazz mingletonUserOne =
        detector.getClass(getAsmStyleClassName(MingletonUserOne.class));
    assertFalse(mingletonOne.uses(mingletonUserOne));
    assertTrue(mingletonUserOne.uses(mingletonOne));
  }

  /*************************************************************************/
  /*                           FINGLETON TESTS                             */
  /*************************************************************************/

  public void testIsFingletonOnAFingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(FingletonOne.class),
            getAsmStyleClassName(NotAFingleton.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(FingletonOne.class));
    assertTrue(cl.isFingleton());
  }

  public void testIsFingletonOnANonFingleton() {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(FingletonOne.class),
            getAsmStyleClassName(NotAFingleton.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(NotAFingleton.class));
    assertFalse(cl.isFingleton());
  }

  public void testCheckFingletonUsage() {
    Flags flags = new Flags();
    flags.setIgnoreSingletons(true);
    flags.setIgnoreHingletons(true);
    flags.setIgnoreMingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(FingletonOne.class),
            getAsmStyleClassName(FingletonUserOne.class));

    Clazz fingletonOne =
        detector.getClass(getAsmStyleClassName(FingletonOne.class));
    Clazz fingletonUserOne =
        detector.getClass(getAsmStyleClassName(FingletonUserOne.class));
    assertFalse(fingletonOne.uses(fingletonUserOne));
    assertTrue(fingletonUserOne.uses(fingletonOne));
  }

  /*************************************************************************/
  /*                               FLAG TESTS                              */
  /*************************************************************************/

  public void testSingletonFlag() {
    Flags flags = new Flags();
    flags.setIgnoreSingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(SingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(SingletonOne.class));
    assertFalse(cl.isSingleton());
  }

  public void testHingletonFlag() {
    Flags flags = new Flags();
    flags.setIgnoreHingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(HingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(HingletonOne.class));
    assertFalse(cl.isHingleton());
  }

  public void testMingletonFlag() {
    Flags flags = new Flags();
    flags.setIgnoreMingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(MingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(MingletonOne.class));
    assertFalse(cl.isMingleton());
  }

  public void testFingletonFlag() {
    Flags flags = new Flags();
    flags.setIgnoreFingletons(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(FingletonOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(FingletonOne.class));
    assertFalse(cl.isFingleton());
  }

  public void testOtherFlag() {
    Flags flags = new Flags();
    flags.setIgnoreOthers(true);
    detector =
        new SingletonDetector(root, path, flags,
            getAsmStyleClassName(SingletonOne.class),
            getAsmStyleClassName(SingletonUserOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(SingletonUserOne.class));
    assertFalse(cl.isDrawn());
  }

  /*************************************************************************/
  /*                             OTHER TESTS                               */
  /*************************************************************************/

  public void testEnumClass() {
    detector =
        new SingletonDetector(root, path, getAsmStyleClassName(EnumClass.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(EnumClass.class));
    assertTrue(cl == null);
  }
  
  public void testThreshold() {
    Flags flags = new Flags();
    flags.setThreshold(2);
    detector = new SingletonDetector(root, path, flags,
        getAsmStyleClassName(SingletonOne.class),
        getAsmStyleClassName(SingletonUserOne.class),
        getAsmStyleClassName(SingletonUserTwo.class),
        getAsmStyleClassName(NotASingletonTwo.class),
        getAsmStyleClassName(HingletonOne.class),
        getAsmStyleClassName(HingletonUserOne.class));
    Clazz cl = detector.getClass(getAsmStyleClassName(SingletonOne.class));
    assertTrue(cl.isDrawn());
    cl = detector.getClass(getAsmStyleClassName(SingletonUserOne.class));
    assertTrue(cl.isDrawn());
    cl = detector.getClass(getAsmStyleClassName(HingletonOne.class));
    assertFalse(cl.isDrawn());
  }

  public void testGraphMlOutput() throws IOException {
    detector =
        new SingletonDetector(root, path,
            getAsmStyleClassName(SingletonOne.class),
            getAsmStyleClassName(SingletonUserOne.class),
            getAsmStyleClassName(NotASingletonTwo.class),
            getAsmStyleClassName(HingletonOne.class),
            getAsmStyleClassName(HingletonUserOne.class));

    String graphMlOutput = detector.getGraphMlOutput();
    assertEquals(read("GraphMLOutput.out"), graphMlOutput);
  }
}
