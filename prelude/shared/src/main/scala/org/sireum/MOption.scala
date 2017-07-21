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

@record trait MOption[T] {
  @pure def isEmpty: B

  @pure def nonEmpty: B

  @pure def map[T2](f: T => T2): MOption[T2]

  @pure def flatMap[T2](f: T => MOption[T2]): MOption[T2]

  @pure def forall(f: T => B): B

  @pure def exists(f: T => B): B

  @pure def getOrElse(default: T): T

  def foreach(f: T => Unit): Unit
}

@record class MNone[T] extends MOption[T] {

  @pure def isEmpty: B = {
    l""" ensures  result ≡ T """

    return T
  }

  @pure def nonEmpty: B = {
    l""" ensures  result ≡ F """
    return F
  }

  @pure def map[T2](f: T => T2): MOption[T2] = {
    l""" ensures  result ≡ MNone[T2]() """
    return MNone[T2]()
  }

  @pure def flatMap[T2](f: T => MOption[T2]): MOption[T2] = {
    l""" ensures  result ≡ MNone[T2]() """
    return MNone[T2]()
  }

  @pure def forall(f: T => B): B = {
    l""" ensures  result ≡ T """ // or simply: result
    return T
  }

  @pure def exists(f: T => B): B = {
    l""" ensures  result ≡ F """ // or simply: ¬result
    return F
  }

  @pure def getOrElse(default: T): T = {
    l""" ensures  result ≡ default """
    return default
  }

  def foreach(f: T => Unit): Unit = {}
}


@record class MSome[T](value: T) extends MOption[T] {

  @pure def isEmpty: B = {
    l""" ensures  result ≡ F """
    return F
  }

  @pure def nonEmpty: B = {
    l""" ensures  result ≡ T """
    return T
  }

  @pure def map[T2](f: T => T2): MOption[T2] = {
    l""" ensures  result ≡ f(value) """
    return MSome(f(value))
  }

  @pure def flatMap[T2](f: T => MOption[T2]): MOption[T2] = {
    l""" ensures  result ≡ f(value) """
    return f(value)
  }

  @pure def forall(f: T => B): B = {
    l""" ensures  result ≡ f(value) """
    return f(value)
  }

  @pure def exists(f: T => B): B = {
    l""" ensures  result ≡ f(value) """
    return f(value)
  }

  @pure def getOrElse(default: T): T = {
    l""" ensures  result ≡ value """
    return value
  }

  def foreach(f: T => Unit): Unit = {
    l""" reads    f_reads
         requires f_requires(value)
         modifies f_modifies
         ensures  f_ensures(value) """
    f(value)
  }
}