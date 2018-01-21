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
package com.google.singletondetector.visitors;

import com.google.singletondetector.SingletonDetector;

import org.objectweb.asm.MethodVisitor;

/**
 * Visitor object that is responsible for traversing a class and calling a
 * SingletonUsageMethodvisitor on each method. Helps SingletonDetector determine
 * which classes use other classes.
 * 
 * @author David Rubel
 */
public class SingletonUsageClassVisitor extends NoopClassVisitor {
  // Reference to the singleton detector, used to call visit methods
  SingletonDetector sd;

  public SingletonUsageClassVisitor(SingletonDetector sd) {
    super();
    this.sd = sd;
  }

  @Override
  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    sd.setCurrClass(name);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {
    return new SingletonUsageMethodVisitor(sd);
  }
}
