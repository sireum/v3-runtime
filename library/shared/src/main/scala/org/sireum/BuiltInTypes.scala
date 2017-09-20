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


@ext trait Immutable {

  @pure def string: String

}


@ext trait Ordered[O <: Ordered[O]] extends Immutable {

  @pure def <(other: O): B

  @pure def <=(other: O): B

  @pure def >(other: O): B

  @pure def >=(other: O): B

}


@ext trait Number[N <: Number[N]] extends Ordered[N] {

  @pure def +(other: N): N

  @pure def -(other: N): N

  @pure def *(other: N): N

  @pure def /(other: N): N

  @pure def %(other: N): N
}


@ext trait B extends Immutable {

  @pure def &(other: B): B

  @pure def |(other: B): B

  @pure def |^(other: B): B

  @pure def &&(other: => B): B

  @pure def ||(other: => B): B

  @pure def unary_! : B

  @pure def unary_~ : B

}


@ext trait C extends Ordered[C] {
  def <(other: C): B

  def <=(other: C): B

  def >(other: C): B

  def >=(other: C): B

  def >>>(other: C): C

  def <<(other: C): C

  def &(other: C): C

  def |(other: C): C

  def |^(other: C): C
}


@ext trait Z extends Number[Z] {

  @pure def unary_- : Z

  @pure def >>(other: Z): Z

  @pure def >>>(other: Z): Z

  @pure def <<(other: Z): Z

  @pure def &(other: Z): Z

  @pure def |(other: Z): Z

  @pure def |^(other: Z): Z

  @pure def unary_~ : Z

  @pure def increase: Z

  @pure def decrease: Z

}


@ext trait FloatingPoint[F <: FloatingPoint[F]] extends Number[F]


@ext trait F32 extends FloatingPoint[F32] {

  @pure def unary_- : F32

}


@ext trait F64 extends FloatingPoint[F64] {

  @pure def unary_- : F64

}


@ext trait R extends Number[R] {

  @pure def unary_- : R

}


@ext trait String extends Immutable {

  def at(i: Z): C

  def size: Z

  def toCis: IS[Z, C]

  def toCms: MS[Z, C]

}


@ext trait IS[I <: Z, V] extends Immutable {

  @pure def isEmpty: B

  @pure def nonEmpty: B

  @pure def :+(e: V): IS[I, V]

  @pure def +:(e: V): IS[I, V]

  @pure def ++(other: IS[I, V]): IS[I, V]

  @pure def --(other: IS[I, V]): IS[I, V]

  @pure def -(e: V): IS[I, V]

  @pure def map[V2](f: V => V2): IS[I, V2]

  @pure def flatMap[V2](f: V => IS[I, V2]): IS[I, V2]

  @pure def withFilter(p: V => B): IS[I, V]

  @pure def foreach(p: V => Unit): Unit

  @pure def size: I

  @pure def toMS: MS[I, V] =
    l""" ensures result.size ≡ s.size
                 ∀i: [0, result.size)  result(i) ≡ s(i) """

}

@ext trait EnumSig extends Immutable {
  def numOfElements: Z
}

@ext trait DatatypeSig extends Immutable


@ext trait RichSig extends Immutable


@ext trait ST extends Immutable


@ext trait Mutable {

  @pure def string: String

}


@ext trait MOrdered[O <: MOrdered[O]] extends Mutable {

  @pure def <(other: O): B

  @pure def <=(other: O): B

  @pure def >(other: O): B

  @pure def >=(other: O): B

}


@ext trait MS[I <: Z, V] extends Mutable {

  @pure def isEmpty: B

  @pure def nonEmpty: B

  @pure def :+(e: V): MS[I, V]

  @pure def +:(e: V): MS[I, V]

  @pure def ++(other: MS[I, V]): MS[I, V]

  @pure def --(other: MS[I, V]): MS[I, V]

  @pure def -(e: V): MS[I, V]

  @pure def map[V2](f: V => V2): MS[I, V2]

  @pure def flatMap[V2](f: V => MS[I, V2]): MS[I, V2]

  @pure def withFilter(p: V => B): MS[I, V]

  @pure def foreach(p: V => Unit): Unit

  @pure def size: I

  @pure def toIS: IS[I, V] =
    l""" ensures result.size ≡ s.size
                 ∀i: [0, result.size)  result(i) ≡ s(i) """

}


@ext trait RecordSig extends Mutable

@ext trait MSig extends Mutable

@range(min = -128, max = 127) class Z8

@range(min = -32768, max = 32767) class Z16

@range(min = -2147483648, max = 2147483647) class Z32

@range(min = -9223372036854775808l, max = 9223372036854775807l) class Z64

@range(min = 0) class N

@range(min = 0, max = 255) class N8

@range(min = 0, max = 65535) class N16

@range(min = 0, max = 4294967295l) class N32

@range(min = 0, max = z"18,446,744,073,709,551,617") class N64

@bits(signed = T, width = 8) class S8

@bits(signed = F, width = 8) class U8

@bits(signed = T, width = 16) class S16

@bits(signed = F, width = 16) class U16

@bits(signed = T, width = 32) class S32

@bits(signed = F, width = 32) class U32

@bits(signed = T, width = 64) class S64

@bits(signed = F, width = 64) class U64