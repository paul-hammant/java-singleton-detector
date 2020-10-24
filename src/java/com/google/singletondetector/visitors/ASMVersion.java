package com.google.singletondetector.visitors;

import org.objectweb.asm.Opcodes;

public class ASMVersion {

	public static int getVersion() {
		return Opcodes.ASM7;
	}
}
