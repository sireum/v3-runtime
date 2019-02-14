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
package org.sireum

import U8._
import java.io.{FileReader => FR, File => JFile, FileInputStream => FIS, FileOutputStream => FOS, InputStreamReader => ISR, OutputStreamWriter => OSW}
import java.nio.{ByteBuffer => BB }
import java.nio.charset.{StandardCharsets => SC}
import java.nio.file.{AtomicMoveNotSupportedException, FileAlreadyExistsException, Files => JFiles, LinkOption => LO, Path => JPath, Paths => JPaths, StandardCopyOption => SCO}
import java.util.concurrent.{TimeUnit => TU}

import com.zaxxer.nuprocess._

import scala.collection.JavaConverters._

object Os_Ext {

  val fileSep: String = java.io.File.separator

  val lineSep: String = System.lineSeparator

  val pathSep: String = java.io.File.pathSeparator

  lazy val cwd: String = canon(System.getProperty("user.dir"))

  lazy val home: String = canon(System.getProperty("user.home"))

  lazy val isNative: B = java.lang.Boolean.getBoolean("com.oracle.graalvm.isaot")

  lazy val roots: ISZ[String] = ISZ((for (f <- java.io.File.listRoots) yield String(f.getCanonicalPath)): _*)

  val os: Os.Kind.Type =
    if (scala.util.Properties.isMac) Os.Kind.Mac
    else if (scala.util.Properties.isLinux) Os.Kind.Linux
    else if (scala.util.Properties.isWin) Os.Kind.Win
    else Os.Kind.Unsupported

  def abs(path: String): String = toIO(path).getAbsolutePath

  def canon(path: String): String = toIO(path).getCanonicalPath

  def cliArgs: ISZ[String] = App.args

  def chmod(path: String, mask: String, all: B): Unit = {
    if (os == Os.Kind.Win) return
    if (all) Os.proc(ISZ("chmod", "-fR", mask, path))
    else Os.proc(ISZ("chmod", mask, path))
  }

  def copy(path: String, target: String, over: B): Unit = {
    val p = toNIO(path)
    val t = toNIO(target)
    if (over) JFiles.copy(p, t, SCO.REPLACE_EXISTING, SCO.COPY_ATTRIBUTES)
    else JFiles.copy(p, t, SCO.COPY_ATTRIBUTES)
  }

  def env(name: String): Option[String] = {
    val value = System.getenv(name.value)
    if (value != null) Some(value) else None()
  }

  def envs: Map[String, String] = {
    var r = Map.empty[String, String]
    for ((k, v) <- System.getenv().asScala) {
      r = r + k ~> v
    }
    r
  }

  def exists(path: String): B = JFiles.exists(toNIO(path), LO.NOFOLLOW_LINKS)

  def exit(code: Z): Unit = System.exit(code.toInt)

  def isAbs(path: String): B = toIO(path).isAbsolute

  def kind(path: String): Os.Path.Kind.Type = {
    val p = toNIO(path)
    if (JFiles.isSymbolicLink(p)) Os.Path.Kind.SymLink
    else if (JFiles.isDirectory(p)) Os.Path.Kind.Dir
    else if (JFiles.isRegularFile(p)) Os.Path.Kind.File
    else Os.Path.Kind.Other
  }

  def lastModified(path: String): Z = toIO(path).lastModified

  @pure def lines(path: String, count: Z): ISZ[String] =
    if (count <= 0) ISZ(JFiles.lines(toNIO(path), SC.UTF_8).toArray.map(s => String(s.toString)): _*)
    else ISZ(JFiles.lines(toNIO(path), SC.UTF_8).limit(count.toLong).toArray.map(s => String(s.toString)): _*)

  def list(path: String): ISZ[String] = ISZ(toIO(path).list.map(String(_)): _*)

  def move(path: String, target: String, over: B): Unit = {
    val p = toNIO(path)
    val t = toNIO(target)
    try if (over) {
      JFiles.move(p, t, SCO.COPY_ATTRIBUTES, SCO.ATOMIC_MOVE)
    } else {
      JFiles.move(p, t, SCO.COPY_ATTRIBUTES, SCO.REPLACE_EXISTING, SCO.ATOMIC_MOVE)
    } catch {
      case _: AtomicMoveNotSupportedException =>
        if (over) {
          JFiles.move(p, t, SCO.COPY_ATTRIBUTES)
        } else {
          JFiles.move(p, t, SCO.COPY_ATTRIBUTES, SCO.REPLACE_EXISTING)
        }
    }
  }

  def mkdir(path: String, all: B): Unit = {
    if (all) JFiles.createDirectory(toNIO(path))
    else JFiles.createDirectories(toNIO(path))
  }

  def name(path: String): String = toIO(path).getName

  @pure def norm(path: String): String = toIO(path).getPath

  def properties(path: String): Map[String, String] = {
    val p = new java.util.Properties()
    val reader = new FR(toIO(path))
    try p.load(reader)
    finally reader.close()
    var r = Map.empty[String, String]
    for (k <- p.stringPropertyNames.asScala) {
      r = r + k ~> p.getProperty(k)
    }
    r
  }

  def readSymLink(path: String): Option[String] = {
    try Some(JFiles.readSymbolicLink(toNIO(path)).toFile.getCanonicalPath)
    catch {
      case _: Throwable => None()
    }
  }

  def relativize(path: String, other: String): String = toNIO(path).relativize(toNIO(other)).toString

  def read(path: String): String = new Predef.String(JFiles.readAllBytes(toNIO(path)), SC.UTF_8)

  def readLines(path: String): ISZ[String] =
    ISZ(JFiles.readAllLines(toNIO(path), SC.UTF_8).asScala.map(String.apply): _*)

  def readU8s(path: String): ISZ[U8] = {
    val f = toIO(path)
    val length = f.length
    val r = ISZ.create(length, u8"0")
    val data = r.data.asInstanceOf[Array[Byte]]
    val is = new FIS(f)
    try is.read(data, 0, length.toInt) finally is.close()
    r
  }

  def readU8ms(path: String): MSZ[U8] = {
    val f = toIO(path)
    val length = f.length
    val r = MSZ.create(length, u8"0")
    val data = r.data.asInstanceOf[Array[Byte]]
    val is = new FIS(f)
    try is.read(data, 0, length.toInt) finally is.close()
    r
  }

  def readLineStream(p: String): Os.Path.Generator[String] =
    new Os.Path.Generator[String] {
      override def path: Os.Path = Os.Path.Impl(p)
      override def generate(f: String => Generator.Action): Generator.Action = {
        val stream = JFiles.lines(toNIO(p), SC.UTF_8)
        var last = Generator.Continue
        for (e <- stream.iterator.asScala) {
          last = f(e)
          if (!last) {
            return Generator.End
          }
        }
        return last
      }
    }

  def readU8Stream(p: String): Os.Path.Generator[U8] =
    new Os.Path.Generator[U8] {
      override def path: Os.Path = Os.Path.Impl(p)
      override def generate(f: U8 => Generator.Action): Generator.Action = {
        val is = new FIS(toIO(p))
        try {
          var last = Generator.Continue
          var b = is.read()
          while (b != -1) {
            last = f(U8(b))
            if (!last) {
              return Generator.End
            }
            b = is.read()
          }
          return last
        } finally is.close()
      }
    }

  def readCStream(p: String): Os.Path.Generator[C] =
    new Os.Path.Generator[C] {
      override def path: Os.Path = Os.Path.Impl(p)
      override def generate(f: C => Generator.Action): Generator.Action = {
        val fr = new ISR(new FIS(toIO(p)), SC.UTF_8)
        try {
          var last = Generator.Continue
          var c = fr.read()
          while (c != -1) {
            last = f(C(c))
            if (!last) {
              return Generator.End
            }
            c = fr.read()
          }
          return last
        } finally fr.close()
      }
    }

  def readLineMStream(p: String): Os.Path.MGenerator[String] = {
    class G extends Os.Path.MGenerator[String] {
      private var _owned: Boolean = false
      def $owned_=(owned: Boolean): G = { _owned = owned; this }
      def $owned: Boolean = _owned
      def $clone: G = new G

      override def path: Os.Path = Os.Path.Impl(p)

      override def generate(f: String => Generator.Action): Generator.Action = {
        val stream = JFiles.lines(toNIO(p), SC.UTF_8)
        var last = Generator.Continue
        for (e <- stream.iterator.asScala) {
          last = f(e)
          if (!last) {
            return Generator.End
          }
        }
        return last
      }
    }
    new G
  }

  def readU8MStream(p: String): Os.Path.MGenerator[U8] = {
    class G extends Os.Path.MGenerator[U8] {
      private var _owned: Boolean = false
      def $owned_=(owned: Boolean): G = { _owned = owned; this }
      def $owned: Boolean = _owned
      def $clone: G = new G

      override def path: Os.Path = Os.Path.Impl(p)

      override def generate(f: U8 => Generator.Action): Generator.Action = {
        val is = new FIS(toIO(p))
        try {
          var last = Generator.Continue
          var b = is.read()
          while (b != -1) {
            last = f(U8(b))
            if (!last) {
              return Generator.End
            }
            b = is.read()
          }
          return last
        } finally is.close()
      }
    }
    new G
  }

  def readCMStream(p: String): Os.Path.MGenerator[C] = {
    class G extends Os.Path.MGenerator[C] {
      private var _owned: Boolean = false
      def $owned_=(owned: Boolean): G = { _owned = owned; this }
      def $owned: Boolean = _owned
      def $clone: G = new G

      override def path: Os.Path = Os.Path.Impl(p)

      override def generate(f: C => Generator.Action): Generator.Action = {
        val fr = new ISR(new FIS(toIO(p)), SC.UTF_8)
        try {
          var last = Generator.Continue
          var c = fr.read()
          while (c != -1) {
            last = f(C(c))
            if (!last) {
              return Generator.End
            }
            c = fr.read()
          }
          return last
        } finally fr.close()
      }
    }
    new G
  }

  def remove(path: String): Unit = {
    JFiles.delete(toNIO(path))
  }

  def removeAll(path: String): Unit = if (exists(path)) {
    os match {
      case Os.Kind.Win => proc(Os.proc(ISZ("RD", "/S", "/Q", path)))
      case _ => proc(Os.proc(ISZ("rm", "-fR", path)))
    }
  }

  def removeOnExit(path: String): Unit = {
    toIO(path).deleteOnExit()
  }

  def temp(prefix: String, suffix: String): String = {
    JFiles.createTempFile(prefix.value, suffix.value).toFile.getCanonicalPath
  }

  def tempDir(prefix: String): String = {
    JFiles.createTempDirectory(prefix.value).toFile.getCanonicalPath
  }

  def writeAppend(path: String, mode: Os.Path.WriteMode.Type): Boolean = {
    val f = toIO(path)
    if (f.exists && mode == Os.Path.WriteMode.Regular)
      throw new FileAlreadyExistsException(s"$path already exists")
    mkdir(f.getParentFile.getCanonicalPath, T)
    if (mode == Os.Path.WriteMode.Append) true
    else {
      removeAll(path)
      false
    }
  }

  def write(path: String, content: String, mode: Os.Path.WriteMode.Type): Unit = {
    val os = new FOS(toIO(path), writeAppend(path, mode))
    try os.write(content.value.getBytes(SC.UTF_8))
    finally os.close()
  }

  def writeU8s(path: String, content: ISZ[U8], mode: Os.Path.WriteMode.Type): Unit = {
    val os = new FOS(toIO(path), writeAppend(path, mode))
    try os.write(content.data.asInstanceOf[Array[Byte]], 0, content.size.toInt)
    finally os.close()
  }

  def writeU8ms(path: String, content: MSZ[U8], mode: Os.Path.WriteMode.Type): Unit = {
    val os = new FOS(toIO(path), writeAppend(path, mode))
    try os.write(content.data.asInstanceOf[Array[Byte]], 0, content.size.toInt)
    finally os.close()
  }

  def writeLineStream(path: String, lines: Generator[String], mode: Os.Path.WriteMode.Type): Unit = {
    val os = new FOS(toIO(path), writeAppend(path, mode))
    try for (l <- lines) os.write(l.value.getBytes(SC.UTF_8))
    finally os.close()
  }

  def writeU8Stream(path: String, u8s: Generator[U8], mode: Os.Path.WriteMode.Type): Unit = {
    val os = new FOS(toIO(path), writeAppend(path, mode))
    try for (b <- u8s) os.write(b.value)
    finally os.close()
  }

  def writeCStream(path: String, cs: Generator[C], mode: Os.Path.WriteMode.Type): Unit = {
    val os = new OSW(new FOS(toIO(path), writeAppend(path, mode)), SC.UTF_8)
    try for (c <- cs) os.write(c.value)
    finally os.close()
  }

  def writeLineMStream(path: String, lines: MGenerator[String], mode: Os.Path.WriteMode.Type): Unit = {
    val os = new FOS(toIO(path), writeAppend(path, mode))
    try for (l <- lines) os.write(l.value.getBytes(SC.UTF_8))
    finally os.close()
  }

  def writeU8MStream(path: String, u8s: MGenerator[U8], mode: Os.Path.WriteMode.Type): Unit = {
    val os = new FOS(toIO(path), writeAppend(path, mode))
    try for (b <- u8s) os.write(b.value)
    finally os.close()
  }

  def writeCMStream(path: String, cs: MGenerator[C], mode: Os.Path.WriteMode.Type): Unit = {
    val os = new OSW(new FOS(toIO(path), writeAppend(path, mode)), SC.UTF_8)
    try for (c <- cs) os.write(c.value)
    finally os.close()
  }

  def parent(path: String): String = toIO(path).getParent

  def proc(e: Os.Proc): Os.Proc.Result = if (isNative) {
    val m = scala.collection.mutable.Map[Predef.String, Predef.String]()
    val env = if (e.addEnv) System.getenv().asScala ++ e.envMap.entries.elements else e.envMap.entries.elements
    for ((k, v) <- env) {
      m(k.toString) = v.toString
    }
    val (stdout, stderr) =
      if (e.outputConsole) (_root_.os.Inherit: _root_.os.ProcessOutput, _root_.os.Inherit: _root_.os.ProcessOutput)
      else (_root_.os.Pipe: _root_.os.ProcessOutput, _root_.os.Pipe: _root_.os.ProcessOutput)
    val stdin: _root_.os.ProcessInput = e.input match {
      case Some(s) => s.value
      case _ => _root_.os.Pipe
    }
    val sp = _root_.os.proc(e.commands.elements.map(_.value: _root_.os.Shellable)).
      spawn(cwd = _root_.os.Path(e.wd.value.value), env = m.toMap, stdin = stdin, stdout = stdout, stderr = stderr,
        mergeErrIntoOut = e.errAsOut, propagateEnv = e.addEnv)
    val term = sp.waitFor(e.timeoutInMillis.toLong)
    if (term) return Os.Proc.Result.Normal(sp.exitCode, new Predef.String(sp.stdout.bytes(), SC.UTF_8),
      new Predef.String(sp.stderr.bytes(), SC.UTF_8))
    if (sp.isAlive) try {
      sp.destroy()
      sp.wrapped.waitFor(500, TU.MICROSECONDS)
    } catch {
      case _: Throwable =>
    }
    if (sp.isAlive)
      try sp.destroyForcibly()
      catch {
        case _: Throwable =>
      }
    Os.Proc.Result.Timeout()
  } else {
    val commands = new java.util.ArrayList(e.commands.elements.map(_.value).asJavaCollection)
    val m = scala.collection.mutable.Map[Predef.String, Predef.String]()
    val env = if (e.addEnv) System.getenv().asScala ++ e.envMap.entries.elements else e.envMap.entries.elements
    for ((k, v) <- env) {
      m(k.toString) = v.toString
    }
    val npb = new NuProcessBuilder(commands, m.asJava)
    val out = new java.lang.StringBuilder()
    val err = new java.lang.StringBuilder()
    npb.setProcessListener(new NuAbstractProcessHandler {
      def append(isOut: B, buffer: BB): Unit = {
        val bytes = new Array[Byte](buffer.remaining)
        buffer.get(bytes)
        val s = new Predef.String(bytes, SC.UTF_8)
        if (isOut || e.errAsOut)
          if (e.outputConsole) System.out.print(s) else out.append(s)
        else if (e.outputConsole) System.err.print(s) else err.append(s)
      }

      override def onStderr(buffer: BB, closed: Boolean): Unit = {
        if (!closed) append(F, buffer)
      }

      override def onStdout(buffer: BB, closed: Boolean): Unit = {
        if (!closed) append(T, buffer)
      }
    })
    val p = npb.start()
    if (p != null && p.isRunning) {
      e.input match {
        case Some(in) => p.writeStdin(BB.wrap(in.value.getBytes(SC.UTF_8)))
        case _ =>
      }
      p.closeStdin(false)
      val exitCode = p.waitFor(e.timeoutInMillis.toLong, TU.MILLISECONDS)
      if (exitCode != scala.Int.MinValue) return Os.Proc.Result.Normal(exitCode, out.toString, err.toString)
      if (p.isRunning) try {
        p.destroy(false)
        p.waitFor(500, TU.MICROSECONDS)
      } catch {
        case _: Throwable =>
      }
      if (p.isRunning)
        try p.destroy(true)
        catch {
          case _: Throwable =>
        }
      Os.Proc.Result.Timeout()
    } else Os.Proc.Result.Exception(s"Could not execute command: ${e.commands.elements.mkString(" ")}")
  }


  private def toIO(path: String): JFile = new JFile(path.value)

  private def toNIO(path: String): JPath = JPaths.get(path.value)
}
