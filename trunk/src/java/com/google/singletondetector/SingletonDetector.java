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

import com.google.singletondetector.classpath.ClasspathRoot;
import com.google.singletondetector.classpath.DirectoryClasspathRoot;
import com.google.singletondetector.classpath.JarClasspathRoot;
import com.google.singletondetector.output.FingletonOutputNode;
import com.google.singletondetector.output.HingletonOutputNode;
import com.google.singletondetector.output.MingletonOutputNode;
import com.google.singletondetector.output.OtherOutputNode;
import com.google.singletondetector.output.OutputNode;
import com.google.singletondetector.output.SingletonOutputNode;
import com.google.singletondetector.visitors.SingletonClassVisitor;
import com.google.singletondetector.visitors.SingletonUsageClassVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The main SingletonDetector class, which generates the visitors for each class
 * and starts them. Also responsible for constructing the OutputNodes and
 * formatting their output.
 * 
 * @author David Rubel
 */
public class SingletonDetector implements Opcodes {
  // Map of all of the classes ready by SingletonDetector
  private Map<String, Clazz> classes = new HashMap<String, Clazz>();

  // The class currently being read by the visitor objects
  private Clazz currClass;

  // The common prefix for all classes
  private String prefix;

  // Command line flags passed in from the main() method
  private Flags flags;

  // Statistics kept on the SingletonDetector
  private Stats stats;

  /**
   * Test constructor, used to pass in a classpath and a list of class names
   * directly, avoiding a directory or jar.  Uses default flags.
   */
  public SingletonDetector(ClasspathRoot classpath, String prefix,
      String... classNames) {
    this(classpath, prefix, new Flags(), classNames);
  }
  
  /**
   * Test constructor, used to pass in a classpath and a list of class names
   * directly, avoiding a directory or jar.
   */
  public SingletonDetector(ClasspathRoot classpath, String prefix, Flags flags,
      String... classNames) {
    this(getClassReaders(classpath, classNames, prefix), prefix, flags);
  }
  
  /**
   * Default constructor, used to pass in a string which represents a directory
   * or a jar.
   */
  public SingletonDetector(String dir, String prefix, Flags flags) throws MalformedURLException {
    this(getClassReaders(dir, prefix, flags), prefix, flags); 
  }

  /**
   * Master constructor, called by all other constructors.  Requires a list of
   * ClassReaders, which is generated in the other constructors by different
   * methods.
   */
  public SingletonDetector(List<ClassReader> crlist, String prefix, Flags flags) {
    stats = new Stats();
    this.prefix = prefix;
    this.flags = flags;
    
    // Verbose: Begin processing
    if (flags.isVerbose()) {
      System.out.print("Processing... ");
    }

    // First pass: determine the type of each class (i.e. Singleton), count the
    //             number of read classes and remove enums
    Iterator<ClassReader> iter = crlist.iterator();
    for (ClassReader cr : crlist) {
      cr.accept(new SingletonClassVisitor(this), ClassReader.SKIP_DEBUG);
      stats.incClassesRead();
    }

    // Second pass: determine which special classes each class uses
    for (ClassReader cr : crlist) {
      cr.accept(new SingletonUsageClassVisitor(this), ClassReader.SKIP_DEBUG);
    }
    
    // Third pass: set isDrawn for each class
    for (Clazz cl : classes.values()) {
      cl.setIsDrawn(flags.getThreshold());
    }
    
    // Fourth pass: update isDrawn
    if (flags.getThreshold() > 0) {
      for (Clazz cl : classes.values()) {
        cl.updateIsDrawn();
      }
    }

    // Fifth pass: gather statistics
    if (flags.showBanner() || flags.showStats()) {
      // Get stats on current class
      for (Clazz cl : classes.values()) {
        if (cl.isDrawn()) {
          stats.incClassesDrawn();
  
          if (cl.isSingleton()) {
            stats.incSingletons();
          } else if (cl.isHingleton()) {
            stats.incHingletons();
          } else if (cl.isMingleton()) {
            stats.incMingletons();
          } else if (cl.isFingleton()) {
            stats.incFingletons();
          }
          
          // Get stats for all used classes
          for (Clazz used : cl.getClassesIUse()) {
            if (used.isDrawn()) {    
              if (used.isSingleton()) {
                stats.incSingletonUsers();
              } else if (used.isHingleton()) {
                stats.incHingletonUsers();
              } else if (used.isMingleton()) {
                stats.incMingletonUsers();
              } else if (used.isFingleton()) {
                stats.incFingletonUsers();
              }
            }
          }
        }
      }
    }
    
    // Verbose: end processing
    if (flags.isVerbose()) {
      System.out.println("done.");
    }
  }

  public void setCurrClass(String name) {
    String label = removePrefix(name);
    currClass = classes.get(label);
    if (currClass == null) {
      currClass = new Clazz(label);
      classes.put(currClass.getName(), currClass);
    }
  }

  public Clazz getCurrClass() {
    return currClass;
  }

  public Clazz getClass(String name) {
    return classes.get(removePrefix(name));
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String[] splitDesc(String desc) {
    String[] ret = new String[2];
    if (desc.startsWith("(")) {
      ret[0] = desc.substring(1, desc.indexOf(')'));
      ret[1] = desc.substring(desc.indexOf(')') + 1);
      ret[1] = ret[1].substring(0, ret[1].length() - 1);
    }
    return ret;
  }

  public String removePrefix(String str) {
    String ret = str;
    if (str.startsWith(prefix)) {
      ret = ret.substring(prefix.length());
    }
    return ret;
  }
  
  /*************************************************************************/
  /*                         CLASS READER METHODS                          */
  /*************************************************************************/
  
  private static ClassReader getClassReader(ClasspathRoot classpath,
      String className) throws IOException {
    String resourceName = className;
    if (!resourceName.endsWith(".class")) {
      resourceName += ".class";
    }
    ClassReader cr =
        new ClassReader(classpath.getResourceAsStream(resourceName));
    return cr;
  }

  private static boolean validcr(ClassReader cr, String prefix) {
    return !cr.getSuperName().equals("java/lang/Enum")
        && cr.getClassName().startsWith(prefix);
  }

  private static List<ClassReader> getClassReaders(ClasspathRoot classpath,
      String[] classNames, String prefix) {
    List<ClassReader> crlist = new ArrayList<ClassReader>();
    for (String className : classNames) {
      ClassReader cr;
      try {
        cr = getClassReader(classpath, className);
        if (validcr(cr, prefix)) {
          crlist.add(cr);
        }
      } catch (IOException e) {
        System.out.println("Failed to read " + className);
      }
    }
    return crlist;
  }
  
  private static List<ClassReader> getClassReaders(String dir, String prefix,
      Flags flags) throws MalformedURLException {
    List<ClassReader> crlist = new ArrayList<ClassReader>();

    // Get the classpath for the classes directory or jar
    URL root = new File(dir).toURI().toURL();
    ClasspathRoot classpath;
    if (dir.endsWith(".jar")) {
      classpath = new JarClasspathRoot(root);
    } else {
      classpath = new DirectoryClasspathRoot(root);
    }

    // Get the class readers for each class
    buildCrlist(root, classpath, prefix, "", crlist, flags.isVerbose());
    
    return crlist;
  }
  
  private static void buildCrlist(URL root, ClasspathRoot classpath,
      String prefix, String packageName, List<ClassReader> crlist,
      boolean verbose) throws MalformedURLException {
    for (String resource : classpath.getResources(packageName)) {
      if (resource.endsWith(".class")) {
        String className = packageName + resource;
        //className = className.replace(".class", "").replace('/', '.');
        if (!className.contains("$")) {
          ClassReader cr;
          try {
            cr = getClassReader(classpath, className);
            if (validcr(cr, prefix)) {
              if (verbose) {
                System.out.println("Found: "
                    + cr.getClassName().replace("/", "."));
              }
              crlist.add(cr);
            }
          } catch (IOException e) {
            System.out.println("Failed to read " + className);
          }
        }
      } else if (resource.endsWith(".jar")) {
        String temp = root.getPath() + packageName + resource;
        URL jarRoot = new File(temp).toURI().toURL();
        ClasspathRoot jarPath = new JarClasspathRoot(jarRoot);
        buildCrlist(jarRoot, jarPath, prefix, "", crlist, verbose);
      } else {
        buildCrlist(root, classpath, prefix, packageName + resource + "/", crlist,
            verbose);
      }
    }
  }

  /*************************************************************************/
  /*                          VISITOR METHODS                              */
  /*************************************************************************/

  public void addStaticField(String name, String desc) {
    // Visitor guarantees field is private, static and not final
    if (desc.startsWith("L")) {
      desc = desc.substring(1, desc.length() - 1);
      currClass.addStaticField(name, removePrefix(desc));
    }
  }

  public void visitStaticField(String name, String desc) {
    // Visitor guarantees field is public and static
    currClass.visitStaticField(name, desc, flags);
  }

  public void visitStaticMethod(String name, String desc) {
    String[] ret = splitDesc(desc);
    String params = ret[0];
    String returnType = ret[1];
    if (returnType.startsWith("L")) {
      returnType = returnType.substring(1);
      if (!returnType.startsWith("java/")
          && returnType.startsWith(prefix)) {
        currClass.visitStaticMethod(name, params, removePrefix(returnType), flags);
      }
    }
  }

  public void invokeStatic(String referencedClass, String desc) {
    String[] ret = splitDesc(desc);
    String params = ret[0];
    String returnType = ret[1];
    if (returnType.startsWith("L")) {
      returnType = returnType.substring(1);
      returnType = removePrefix(returnType);
      referencedClass = removePrefix(referencedClass);

      Clazz cl = classes.get(referencedClass);
      if (cl != null && cl != currClass
          && (currClass.isSpecial() || !flags.ignoreOthers())) {
        if (returnType.equals(referencedClass) && cl.isSingleton()) {
          currClass.addClassIUse(cl);
        } else if (cl.doIHingle(returnType) && cl.isHingleton()) {
          currClass.addClassIUse(cl);
        } else if (params.equals("") && cl.isMingleton()) {
          currClass.addClassIUse(cl);
        }
      }
    }

  }

  public void fieldInstruction(String owner) {
    Clazz cl = classes.get(removePrefix(owner));
    if (cl != null && cl != currClass && cl.isFingleton()) {
      currClass.addClassIUse(cl);
    }
  }

  /*************************************************************************/
  /*                             OUTPUT METHODS                            */
  /*************************************************************************/

  public String getOutput(boolean pad) {
    return stats.getOutput(flags, pad);
  }

  public String getGraphMlOutput() {
    HashMap<String, OutputNode> nodes = getOutputNodes();
    String nodeOutput = "";
    String edgeOutput = "";
    
    // Verbose: begin output
    if (flags.isVerbose()) {
      System.out.print("Generating output graph... ");
    }
    
    // Fill buffers
    for (OutputNode node : nodes.values()) {
      nodeOutput += node.getGraphMlNode();
      edgeOutput += node.getGraphMlEdges();
    }

    String banner = "";
    if (flags.showBanner()) {
      banner =
          "    <node id=\"banner\">\n" + "      <data key=\"d0\">\n"
              + "        <y:ShapeNode>\n"
              + "          <y:Fill color = \"#0000FF\"/>\n"
              + "          <y:NodeLabel textColor=\"#FFFFFF\" fontSize=\"24\">"
              + stats.getOutput(flags, false).replace("\n", "&#xA;")
              + "</y:NodeLabel>\n"
              + "          <y:Shape type=\"rectangle\"/>\n"
              + "        </y:ShapeNode>\n" + "      </data>\n"
              + "    </node>\n";
    }
    
    // Verbose: end output
    if (flags.isVerbose()) {
      System.out.println("done.");
    }

    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\" xmlns:y=\"http://www.yworks.com/xml/graphml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\">\n"
        + "<key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>\n"
        + "<key id=\"d1\" for=\"edge\" yfiles.type=\"edgegraphics\"/>\n"
        + "  <graph id=\"SingletonDetector\" edgedefault=\"directed\">\n"
        + banner + nodeOutput + edgeOutput + "  </graph>\n" + "</graphml>";
  }

  private HashMap<String, OutputNode> getOutputNodes() {
    HashMap<String, OutputNode> nodes = new HashMap<String, OutputNode>();

    for (String className : classes.keySet()) {
      Clazz clazz = classes.get(className);

      if (clazz.isDrawn()) {
        if (!nodes.containsKey(className)) {
          nodes.put(className, newOutputNode(clazz));
        }

        // Process all classes that this class uses
        for (Clazz usedClazz : clazz.getClassesIUse()) {
          if (usedClazz.isDrawn()) {
            String usedName = usedClazz.getName();

            // Add the using class
            if (!nodes.containsKey(usedName)) {
              nodes.put(usedName, newOutputNode(usedClazz));
            }

            // Add the edge
            nodes.get(className).addEdge(nodes.get(usedName));
          }
        }
      }
    }

    return nodes;
  }

  private OutputNode newOutputNode(Clazz clazz) {
    OutputNode node;

    if (clazz.isSingleton()) {
      node = new SingletonOutputNode(clazz.getName());
    } else if (clazz.isHingleton()) {
      String classIHingle = clazz.getClassIHingle();
      node = new HingletonOutputNode(clazz.getName(), classIHingle);
    } else if (clazz.isMingleton()) {
      node = new MingletonOutputNode(clazz.getName());
    } else if (clazz.isFingleton()) {
      node = new FingletonOutputNode(clazz.getName());
    } else {
      node = new OtherOutputNode(clazz.getName());
    }

    return node;
  }
}
