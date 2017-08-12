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

package org.sireum_prototype

import scala.meta._

object helper {
  def hasHashEquals(tpe: Type, stats: Seq[Stat]): (Boolean, Boolean) = {
    var hasEquals = false
    var hasHash = false
    for (stat <- stats if !(hasEquals && hasHash)) {
      stat match {
        case q"..$_ def hash: Z = $_" => hasHash = true
        case q"..$_ def isEqual($_ : ${atpeopt: Option[Type.Arg]}): B = $_" =>
          atpeopt match {
            case Some(t: Type) if tpe.structure == t.structure => hasEquals = true
            case _ =>
          }
        case _ =>
      }
    }
    (hasHash, hasEquals)
  }

  def isZ(tree: Defn.Type): Boolean = tree.body match {
    case Type.Name("Z") if tree.tparams.isEmpty => true
    case _ => false
  }

  def sIndexValue(tree: Defn.Type): Option[(Boolean, Type, Type)] = tree.body match {
    case t"IS[$index, $value]" => Some((true, index, value))
    case t"MS[$index, $value]" => Some((true, index, value))
    case _ => None
  }
}

final class helper extends scala.annotation.StaticAnnotation