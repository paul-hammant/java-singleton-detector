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

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Visitor object that is responsible for traversing a class and noting static
 * fields and static method calls. Helps SingletonDetector figure out which
 * classes are special and what type of special class they are.
 * 
 * @author David Rubel
 */
public class SingletonClassVisitor extends NoopClassVisitor {
  // Reference to the singleton detector, used to call visit methods
  SingletonDetector sd;

  public SingletonClassVisitor(SingletonDetector sd) {
    super();
    this.sd = sd;
  }

  @Override
  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    sd.setCurrClass(name);
  }
  
  @Override
  public FieldVisitor visitField(int access, String name, String desc,
      String signature, Object value) {
    if ((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {
      if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
        sd.visitStaticField(name, desc);
      }

      if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE
          && (access & Opcodes.ACC_FINAL) != Opcodes.ACC_FINAL) {
        sd.addStaticField(name, desc);
      }
    }
    return null;
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {
    if (((access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC && (access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC)) {
      sd.visitStaticMethod(name, desc);
    }

    return super.visitMethod(access, name, desc, signature, exceptions);
  }
}
