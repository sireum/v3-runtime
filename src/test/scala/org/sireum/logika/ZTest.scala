/*
 * Copyright (c) 2016, Robby, Kansas State University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.logika

import org.junit.Test


class ZTest {
  final val z5 = Z("5")
  final val bigVal = "10000000000000000000000000000000000000000000000000000000000"
  final val zBig = Z(bigVal)

  @Test
  def eqs(): Unit = {
    assert(!(5 == z5)) // does not support Int == Z
    assert(5 != z5) // does not support Int != Z
    assert(z5 == 5)
    assert(z5 == java.lang.Byte.valueOf("5"))
    assert(z5 == new java.lang.Character(5.toChar))
    assert(z5 == java.lang.Short.valueOf("5"))
    assert(z5 == java.lang.Integer.valueOf("5"))
    assert(z5 == java.lang.Long.valueOf("5"))
    assert(z5 == new java.math.BigInteger("5"))
    assert(z5 == BigInt("5"))
    assert(zBig == new java.math.BigInteger(bigVal))
    assert(zBig == BigInt(bigVal))
  }

  @Test
  def comps(): Unit = {
    assert(z5 < 6)
    assert(z5 <= 6)
    assert(z5 <= 5)
    assert(!(z5 < 5))
    assert(!(z5 <= 4))
    assert(z5 > 4)
    assert(z5 >= 4)
    assert(!(z5 > 5))
    assert(!(z5 >= 6))
  }
}
