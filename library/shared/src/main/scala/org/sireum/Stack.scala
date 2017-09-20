// #Sireum
/*
 Copyright (c) 2017, Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum

import org.sireum.ops.ISOps
import org.sireum.ops.ISZOps._


object Stack {
  def empty[T]: Stack[T] = {
    return Stack[T](ISZ())
  }
}

@datatype class Stack[T](elements: ISZ[T]) {
  def isEmpty: B = {
    return elements.isEmpty
  }

  def nonEmpty: B = {
    return elements.nonEmpty
  }

  def peek: Option[T] = {
    if (nonEmpty) {
      return Some(elements(elements.size - 1))
    } else {
      return None()
    }
  }

  def push(e: T): Stack[T] = {
    return Stack(elements :+ e)
  }

  def pop(): Option[(T, Stack[T])] = {
    if (nonEmpty) {
      return Some((elements(elements.size - 1), Stack(ISOps(elements).dropRight(1))))
    } else {
      return None()
    }
  }
}