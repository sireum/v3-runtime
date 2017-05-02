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

import scala.meta._

// TODO: clean up quasiquotes due to IntelliJ's macro annotation inference workaround
object record {

  def tranformTrait(tree: Defn.Trait): Defn.Trait = {
    val q"..$mods trait $tname[..$tparams] extends { ..$estats } with ..$ctorcalls { $param => ..$stats }" = tree
    if (mods.nonEmpty || estats.nonEmpty || !param.name.isInstanceOf[Name.Anonymous])
      abort("Slang @record traits have to be of the form '@record trait <id> ... { ... }'.")
    q"sealed trait $tname[..$tparams] extends { ..$estats } with org.sireum._Record with ..$ctorcalls { $param => ..$stats }"
  }

  def transformClass(tree: Defn.Class, o: Defn.Object): Term.Block = {
    val q"..$mods class $tname[..$tparams] ..$ctorMods (...$paramss) extends { ..$estats } with ..$ctorcalls { $param => ..$stats }" = tree
    if (mods.nonEmpty || ctorMods.nonEmpty || paramss.size > 1 ||
      estats.nonEmpty || !param.name.isInstanceOf[Name.Anonymous])
      abort("Slang @record classes have to be of the form '@record class <id> ... (...) ... { ... }'.")
    val tVars = tparams.map { tp =>
      val tparam"..$mods $tparamname[..$_] >: $_ <: $_ <% ..$_ : ..$_" = tp
      Type.Name(tparamname.value)
    }
    val tpe = if (tVars.isEmpty) tname else t"$tname[..$tVars]"
    val ctorName = Ctor.Name(tname.value)
    var inVars = Vector[Term.Assign]()
    for (stat <- stats) stat match {
      case stat: Defn.Var =>
        for (pat <- stat.pats) pat match {
          case q"${name: Pat.Var.Term}" =>
            val varName = name.name
            inVars :+= q"r.$varName = $varName"
          case _ => abort(stat.pos, s"Unsupported var definition form in Slang @record: ${pat.syntax}")
        }
      case _ =>
    }
    if (paramss.nonEmpty && paramss.head.nonEmpty) {
      var varNames: Vector[Term.Name] = Vector()
      var cparams: Vector[Term.Param] = Vector()
      var applyParams: Vector[Term.Param] = Vector()
      var oApplyParams: Vector[Term.Param] = Vector()
      var applyArgs: Vector[Term.Name] = Vector()
      var unapplyTypes: Vector[Type] = Vector()
      var unapplyArgs: Vector[Term.Name] = Vector()
      var vars: Vector[Stat] = Vector()
      for (param <- paramss.head) param match {
        case param"..$mods $paramname: ${atpeopt: Option[Type.Arg]} = $expropt" if (atpeopt match {
          case Some(targ"${tpe: Type}") => true
          case _ => false
        }) =>
          val varName = Term.Name("_" + paramname.value)
          val paramName = Term.Name(paramname.value)
          var hidden = false
          var isVar = false
          mods.foreach {
            case mod"@hidden" => hidden = true
            case mod"varparam" => isVar = true
            case _ => false
          }
          varNames :+= varName
          cparams :+= param"private var $varName: $atpeopt"
          vars :+= q"def $paramName = $varName"
          vars :+= q"def ${Term.Name(paramname.value + "_=")}($paramname: $atpeopt): this.type = { dirty = true; $varName = $paramName; this }"
          applyParams :+= param"$paramname: $atpeopt = this.$varName"
          oApplyParams :+= param"$paramname: $atpeopt"
          applyArgs :+= paramName
          if (!hidden) {
            val Some(targ"${tpe: Type}") = atpeopt
            unapplyTypes :+= tpe
            unapplyArgs :+= varName
          }
        case _ => abort(param.pos, "Unsupported Slang @record parameter form.")
      }
      val cls = {
        val clone = {
          val cloneNew = q"val r = new $ctorName(..${applyArgs.map(arg => q"org.sireum._Clonable.clone($arg)")})"

          q"override def clone: $tpe = { ..${cloneNew +: inVars :+ q"r" } }"
        }
        val hashCodeDirty = q"private var dirty: Boolean = true"
        val hashCodeVar = q"private var _hashCode: Int = _"
        val hashCodeDef = q"private def computeHashCode: Int = (this.getClass, ..$unapplyArgs).hashCode"
        val hashCode = q"override def hashCode: Int = { if (dirty) { dirty = false; _hashCode = computeHashCode}; _hashCode }"
        val equals = {
          val eCaseEqs = unapplyArgs.map(arg => q"$arg == o.$arg")
          val eCaseExp = eCaseEqs.tail.foldLeft(eCaseEqs.head)((t1, t2) => q"$t1 && $t2")
          val eCases =
            Vector(if (tparams.isEmpty) p"case o: $tname => $eCaseExp"
            else p"case (o: $tname[..$tVars] @unchecked) => $eCaseExp",
              p"case _ => false")
          q"override def equals(o: Any): Boolean = if (this eq o.asInstanceOf[AnyRef]) true else o match { ..case $eCases }"
        }
        val eq = q"def ===(other: $tpe): B = this == other"
        val ne = q"def =!=(other: $tpe): B = this != other"
        val apply = q"def apply(..$applyParams): $tpe = new $ctorName(..${applyArgs.map(arg => q"org.sireum._macro._assign($arg)")})"
        val toString = {
          var appends = applyArgs.map(arg => q"org.sireum._Helper.append(sb, $arg)")
          appends =
            if (appends.isEmpty) appends
            else appends.head +: appends.tail.flatMap(a => Vector(q"""sb.append(", ")""", a))
          q"""override def toString: java.lang.String = {
                    val sb = new StringBuilder
                    sb.append(${Lit.String(tname.value)})
                    sb.append('(')
                    ..$appends
                    sb.append(')')
                    sb.toString
                  }"""
        }
        q"final class $tname[..$tparams](...${Vector(cparams)}) extends {} with org.sireum._Record with ..$ctorcalls { ..${(hashCodeDirty +: vars) ++ Vector(hashCodeVar, hashCodeDef, hashCode, equals, eq, ne, clone, apply, toString) ++ stats} }"
      }
      val companion = {
        val (apply, unapply) =
          if (tparams.isEmpty)
            (q"def apply(..$oApplyParams): $tpe = new $ctorName(..$applyArgs)",
              unapplyTypes.size match {
                case 0 => q"def unapply(o: $tpe): Boolean = true"
                case 1 => q"def unapply(o: $tpe): scala.Option[${unapplyTypes.head}] = scala.Some(org.sireum._Clonable.clone(o.${unapplyArgs.head}))"
                case _ => q"def unapply(o: $tpe): scala.Option[(..$unapplyTypes)] = scala.Some((..${unapplyArgs.map(arg => q"org.sireum._Clonable.clone(o.$arg)")}))"
              })
          else
            (q"def apply[..$tparams](..$oApplyParams): $tpe = new $ctorName(..$applyArgs)",
              unapplyTypes.size match {
                case 0 => q"def unapply[..$tparams](o: $tpe): Boolean = true"
                case 1 => q"def unapply[..$tparams](o: $tpe): scala.Option[${unapplyTypes.head}] = scala.Some(org.sireum._Clonable.clone(o.${unapplyArgs.head}))"
                case _ => q"def unapply[..$tparams](o: $tpe): scala.Option[(..$unapplyTypes)] = scala.Some((..${unapplyArgs.map(arg => q"org.sireum._Clonable.clone(o.$arg)")}))"
              })
        o.copy(templ = o.templ.copy(stats = Some(o.templ.stats.getOrElse(List()) ++ List(apply, unapply))))
      }
      Term.Block(Vector(cls, companion))
    } else {
      val cls = {
        val clone = {
          val cloneNew = q"val r = new $ctorName()"
          q"override def clone: $tpe = { ..${cloneNew +: inVars :+ q"r"} }"
        }
        val hashCode = q"override val hashCode: Int = this.getClass.hashCode"
        val equals = {
          val eCases =
            Vector(if (tparams.isEmpty) p"case o: $tname => true"
            else p"case o: $tname[..$tVars] => true",
              p"case _ => false")
          q"override def equals(o: Any): Boolean = if (this eq o.asInstanceOf[AnyRef]) true else o match { ..case $eCases }"
        }
        val toString = {
          val r = tname.value + "()"
          q"""override def toString: java.lang.String = ${Lit.String(r)}"""
        }
        q"final class $tname[..$tparams](...$paramss) extends {} with org.sireum._Record with ..$ctorcalls { ..${Vector(hashCode, equals, clone, toString) ++ stats} }"
      }
      val companion = {
        val (v, apply, unapply) =
          if (tparams.isEmpty)
            (q"private[this] val v: AnyRef = new $ctorName()",
              q"def apply(): $tpe = v.asInstanceOf[$tpe]",
              q"def unapply(o: $tpe): Boolean = true")
          else
            (q"private[this] val v: AnyRef = new $ctorName[..${tparams.map(_ => t"Nothing")}]()",
              q"def apply[..$tparams](): $tpe = v.asInstanceOf[$tpe]",
              q"def unapply[..$tparams](o: $tpe): Boolean = true")
        o.copy(templ = o.templ.copy(stats = Some(o.templ.stats.getOrElse(List()) ++ List(v, apply, unapply))))
      }
      Term.Block(Vector(cls, companion))
    }
  }
}

class record extends scala.annotation.StaticAnnotation {
  inline def apply(tree: Any): Any = meta {
    val result: Stat = tree match {
      case tree: Defn.Trait => record.tranformTrait(tree)
      case Term.Block(Seq(t: Defn.Trait, o: Defn.Object)) => Term.Block(List[Stat](record.tranformTrait(t), o))
      case tree: Defn.Class => record.transformClass(tree, q"object ${Term.Name(tree.name.value)} {}")
      case Term.Block(Seq(t: Defn.Class, o: Defn.Object)) => record.transformClass(t, o)
      case _ =>
        abort(s"Invalid Slang @record on: ${tree.syntax}.")
    }
    //println(result.syntax)
    result
  }
}