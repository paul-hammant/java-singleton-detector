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

import org.objectweb.asm.Opcodes;

/**
 * Visitor object that is responsible for traversing a method and noting all
 * method calls and field references.Helps SingletonDetector determine which
 * classes use other classes.
 * 
 * @author David Rubel
 */
public class SingletonUsageMethodVisitor extends NoopMethodVisitor {
  // Reference to the singleton detector, used to call visit methods  
  SingletonDetector sd;

  public SingletonUsageMethodVisitor(SingletonDetector sd) {
    super();
    this.sd = sd;
  }

  @Override
  public void visitMethodInsn(int opcode, String referencedClass,
      String methodName, String desc) {
    if (opcode == Opcodes.INVOKESTATIC) {
      sd.invokeStatic(referencedClass, desc);
    }
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String desc) {
    if ((opcode == Opcodes.GETSTATIC || opcode == Opcodes.PUTSTATIC)
        && desc.startsWith("L")) {
      sd.fieldInstruction(owner);
    }
  }
}
