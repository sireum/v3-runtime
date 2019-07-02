// #Sireum
/*
 Copyright (c) 2019, Robby, Kansas State University
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
package org.sireum.bitcodec

import org.sireum._


@datatype trait Spec {
  def name: String

  def toJSON(isCompact: B): String = {
    return Spec.Ext.toJSON(this, isCompact)
  }
}

object Spec {

  @datatype trait Base extends Spec

  @datatype trait Composite extends Base

  @datatype class Boolean(val name: String) extends Base

  @datatype class Bits(val name: String, size: Z) extends Base

  @datatype class Bytes(val name: String, size: Z) extends Base

  @datatype class Shorts(val name: String, size: Z) extends Base

  @datatype class Ints(val name: String, size: Z) extends Base

  @datatype class Longs(val name: String, size: Z) extends Base

  @datatype class Enum(val name: String, objectName: String) extends Base

  @datatype class Concat(val name: String, elements: ISZ[Spec]) extends Composite

  @datatype trait Poly {
    def polyDesc: Spec.PolyDesc
  }

  @datatype class PolyDesc(compName: String, name: String, dependsOn: ISZ[String], elementsOpt: Option[ISZ[Spec]])

  @datatype class Union[T](val name: String,
                           dependsOn: ISZ[String],
                           @hidden choice: T => Z@pure,
                           subs: ISZ[Spec]) extends Composite with Poly {
    val polyDesc: Spec.PolyDesc = PolyDesc("Union", name, dependsOn, Some(subs))
  }

  @datatype class Repeat[T](val name: String,
                            dependsOn: ISZ[String],
                            @hidden size: T => Z@pure,
                            element: Base) extends Spec with Poly {
    val polyDesc: Spec.PolyDesc = PolyDesc("Repeat", name, dependsOn, Some(ISZ(element)))
  }

  @datatype class Raw[T](val name: String,
                         dependsOn: ISZ[String],
                         @hidden size: T => Z@pure) extends Spec with Poly {
    val polyDesc: Spec.PolyDesc = PolyDesc("Raw", name, dependsOn, None())
  }

  @datatype class GenUnion(val name: String, subs: ISZ[Spec]) extends Composite

  @datatype class GenRepeat(val name: String, element: Base) extends Spec

  @datatype class GenRaw(val name: String) extends Spec

  @datatype class Pads(size: Z) extends Spec {
    def name: String = {
      return ""
    }
  }

  @pure def fromJSON(s: String): Either[Spec, Json.ErrorMsg] = {
    return Ext.fromJSON(s)
  }

  @ext("Spec_Ext") object Ext {
    def toJSON(o: Spec, isCompact: B): String = $
    def fromJSON(s: String): Either[Spec, Json.ErrorMsg] = $
  }
}