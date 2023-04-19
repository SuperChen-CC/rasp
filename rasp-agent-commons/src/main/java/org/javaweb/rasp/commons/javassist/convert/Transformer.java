/*
 * Javassist, a Java-bytecode translator toolkit.
 * Copyright (C) 1999- Shigeru Chiba. All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License.  Alternatively, the contents of this file may be used under
 * the terms of the GNU Lesser General Public License Version 2.1 or later,
 * or the Apache License Version 2.0.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 */

package org.javaweb.rasp.commons.javassist.convert;

import org.javaweb.rasp.commons.javassist.CannotCompileException;
import org.javaweb.rasp.commons.javassist.CtClass;
import org.javaweb.rasp.commons.javassist.CodeConverter;
import org.javaweb.rasp.commons.javassist.bytecode.BadBytecode;
import org.javaweb.rasp.commons.javassist.bytecode.CodeAttribute;
import org.javaweb.rasp.commons.javassist.bytecode.CodeIterator;
import org.javaweb.rasp.commons.javassist.bytecode.ConstPool;
import org.javaweb.rasp.commons.javassist.bytecode.MethodInfo;
import org.javaweb.rasp.commons.javassist.bytecode.Opcode;

/**
 * Transformer and its subclasses are used for executing
 * code transformation specified by CodeConverter.
 *
 * @see CodeConverter
 */
public abstract class Transformer implements Opcode {
    private Transformer next;

    public Transformer(Transformer t) {
        next = t;
    }

    public Transformer getNext() { return next; }

    public void initialize(ConstPool cp, CodeAttribute attr) {}
    
    public void initialize(ConstPool cp, CtClass clazz, MethodInfo minfo) throws CannotCompileException {
    	initialize(cp, minfo.getCodeAttribute());
    }

    public void clean() {}

    public abstract int transform(CtClass clazz, int pos, CodeIterator it,
                ConstPool cp) throws CannotCompileException, BadBytecode;

    public int extraLocals() { return 0; }

    public int extraStack() { return 0; }
}
