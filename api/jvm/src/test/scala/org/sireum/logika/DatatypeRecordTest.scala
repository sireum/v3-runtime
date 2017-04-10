/*
 * Copyright (c) 2017, Robby, Kansas State University
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

import org.sireum.logika.test.LogikaSpec

@record trait F2
@record class Foo(x: Z, var bar: Bar) extends F2
@record class Bar(x: Z, var y: Z) extends F2
@datatype class Baz(x: Z, y: Z)
@datatype class Bazz()

@record class Bazzz[T](var x: T) {
  def updateX(newX: T): Unit = {
    x = newX.copy
  }
}

class RecordTest extends LogikaSpec {
  * {
    val foo = Foo(1, Bar(2, 3))
    val fooClone: Foo = foo.copy
    foo.bar.y = 4
    fooClone.bar.y != foo.bar.y && foo(bar = foo.bar(y = 4)) == foo
  }

  * {
    val bazzz = Bazzz[Z](5)
    bazzz.updateX(4)
    bazzz.x == 4
  }
}