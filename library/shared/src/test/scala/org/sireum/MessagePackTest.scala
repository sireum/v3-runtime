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

import org.scalactic.source.Position
import org.sireum.test.SireumRuntimeSpec

class MessagePackTest extends SireumRuntimeSpec {

  {
    import org.sireum.U32._
    import org.sireum.U64._
    val o = message.PosInfo(message.DocInfo(None(), ISZ(u32"0", u32"10", u32"40")), (u64"1" << u64"32") | u64"5")
    check(T, o, (w) => w.writePosition(o), (r) => r.readPosition())
  }


  for (pooling <- Seq(T, F)) {
    check(pooling, T, { w =>
      w.writeB(T)
    }, { r =>
      r.readB()
    })

    check(pooling, F, { w =>
      w.writeB(F)
    }, { r =>
      r.readB()
    })

    check(pooling, -1, { w =>
      w.writeZ(-1)
    }, { r =>
      r.readZ()
    })

    for (s <- Seq[String]("∧", "∨", "→", "¬", "≡", "≠", "≥", "≤", "∀", "∃", "⊤", "⊥", "⊢")) {
      check(pooling, s, { w =>
        w.writeString(s)
      }, { r =>
        r.readString()
      })
    }

    for (_ <- 0 until 10) {
      {
        val c: C = C.random
        check(pooling, c, { w =>
          w.writeC(c)
        }, { r =>
          r.readC()
        })
      }

      {
        val n: Z = Z.random
        check(pooling, n, { w =>
          w.writeZ(n)
        }, { r =>
          r.readZ()
        })
      }

      {
        val n: Z8 = Z8(-21)
        check(pooling, n, { w =>
          w.writeZ8(n)
        }, { r =>
          r.readZ8()
        })
      }

      {
        val n: Z16 = Z16.random
        check(pooling, n, { w =>
          w.writeZ16(n)
        }, { r =>
          r.readZ16()
        })
      }

      {
        val n: Z32 = Z32.random
        check(pooling, n, { w =>
          w.writeZ32(n)
        }, { r =>
          r.readZ32()
        })
      }

      {
        val n: Z64 = Z64.random
        check(pooling, n, { w =>
          w.writeZ64(n)
        }, { r =>
          r.readZ64()
        })
      }

      {
        val n: N = N.random
        check(pooling, n, { w =>
          w.writeN(n)
        }, { r =>
          r.readN()
        })
      }

      {
        val n: N8 = N8.random
        check(pooling, n, { w =>
          w.writeN8(n)
        }, { r =>
          r.readN8()
        })
      }

      {
        val n: N16 = N16.random
        check(pooling, n, { w =>
          w.writeN16(n)
        }, { r =>
          r.readN16()
        })
      }

      {
        val n: N32 = N32.random
        check(pooling, n, { w =>
          w.writeN32(n)
        }, { r =>
          r.readN32()
        })
      }

      {
        val n: N64 = N64.random
        check(pooling, n, { w =>
          w.writeN64(n)
        }, { r =>
          r.readN64()
        })
      }

      {
        val n: S8 = S8.random
        check(pooling, n, { w =>
          w.writeS8(n)
        }, { r =>
          r.readS8()
        })
      }

      {
        val n: S16 = S16.random
        check(pooling, n, { w =>
          w.writeS16(n)
        }, { r =>
          r.readS16()
        })
      }

      {
        val n: S32 = S32.random
        check(pooling, n, { w =>
          w.writeS32(n)
        }, { r =>
          r.readS32()
        })
      }

      {
        val n: S64 = S64.random
        check(pooling, n, { w =>
          w.writeS64(n)
        }, { r =>
          r.readS64()
        })
      }

      {
        val n: U8 = U8.random
        check(pooling, n, { w =>
          w.writeU8(n)
        }, { r =>
          r.readU8()
        })
      }

      {
        val n: U16 = U16.random
        check(pooling, n, { w =>
          w.writeU16(n)
        }, { r =>
          r.readU16()
        })
      }

      {
        val n: U32 = U32.random
        check(pooling, n, { w =>
          w.writeU32(n)
        }, { r =>
          r.readU32()
        })
      }

      {
        val n: U64 = U64.random
        check(pooling, n, { w =>
          w.writeU64(n)
        }, { r =>
          r.readU64()
        })
      }

      {
        val n: R = R.random
        check(pooling, n, { w =>
          w.writeR(n)
        }, { r =>
          r.readR()
        })
      }

      {
        val n: F32 = F32.random
        check(pooling, n, { w =>
          w.writeF32(n)
        }, { r =>
          r.readF32()
        })
      }

      {
        val n: F64 = F64.random
        check(pooling, n, { w =>
          w.writeF64(n)
        }, { r =>
          r.readF64()
        })
      }

      {
        val s: String = String.random
        check(pooling, s, { w =>
          w.writeString(s)
        }, { r =>
          r.readString()
        })
      }

      {
        import Z8._
        val size = {
          var r = Z8.random
          while (r < z8"0") {
            r = Z8.random
          }
          r
        }

        val a = {
          val r = MS.create[Z8, Z8](size, z8"0")
          for (i <- z8"0" until size) {
            r(i) = Z8.random
          }
          r.toIS
        }
        check(pooling, a, (w) => w.writeISZ8(a, w.writeZ8 _), (r) => r.readISZ8(r.readZ8 _))
      }

      {
        val size = {
          var r = Z.random % 1024
          while (r < 0) {
            r = Z.random % 1024
          }
          r
        }
        val a = MSZ.create(size, z"0")
        for (i <- z"0" until size) {
          a(i) = Z.random % 1024
        }
        check(pooling, a, (w) => w.writeMSZ(a, w.writeZ _), (r) => r.readMSZ(r.readZ _))
      }

      * {
        val o = Foo(Z.random, Bar(Z.random, Z.random))
        val w = F2MessagePack.writer(pooling)
        w.writeF2(o)
        val data = w.writer.result
        val r = F2MessagePack.reader(data)
        r.reader.init()
        assert(o == r.readF2())
        true
      }
      * {
        val o = Bar(Z.random, Z.random)
        val w = F2MessagePack.writer(pooling)
        w.writeF2(o)
        val data = w.writer.result
        val r = F2MessagePack.reader(data)
        r.reader.init()
        assert(o == r.readF2())
        true
      }
    }

    {
      val o = Either.Left[Z, String](Z.random)
      check(
        pooling,
        o,
        (w) => w.writeEither(o, w.writeZ _, w.writeString _),
        (r) => r.readEither(r.readZ _, r.readString _)
      )
    }

    {
      val o = MEither.Right[Z, String](String.random)
      check(
        pooling,
        o,
        (w) => w.writeMEither(o, w.writeZ _, w.writeString _),
        (r) => r.readMEither(r.readZ _, r.readString _)
      )
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => (String.random, Z.random)))) {
      val o = Map ++ is
      check(pooling, o, (w) => w.writeMap(o, w.writeString _, w.writeZ _), (r) => r.readMap(r.readString _, r.readZ _))
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => String.random))) {
      val o = Set ++ is
      check(pooling, o, (w) => w.writeSet(o, w.writeString _), (r) => r.readSet(r.readString _))
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => (String.random, Z.random)))) {
      val o = HashMap ++ is
      check(
        pooling,
        o,
        (w) => w.writeHashMap(o, w.writeString _, w.writeZ _),
        (r) => r.readHashMap(r.readString _, r.readZ _)
      )
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => String.random))) {
      val o = HashSet ++ is
      check(pooling, o, (w) => w.writeHashSet(o, w.writeString _), (r) => r.readHashSet(r.readString _))
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => (String.random, Z.random)))) {
      val o = HashSMap ++ is
      check(
        pooling,
        o,
        (w) => w.writeHashSMap(o, w.writeString _, w.writeZ _),
        (r) => r.readHashSMap(r.readString _, r.readZ _)
      )
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => String.random))) {
      val o = HashSSet ++ is
      check(pooling, o, (w) => w.writeHashSSet(o, w.writeString _), (r) => r.readHashSSet(r.readString _))
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => String.random))) {
      val o = {
        var s = Stack.empty[String]
        for (e <- is) {
          s = s.push(e)
        }
        s
      }
      check(pooling, o, (w) => w.writeStack(o, w.writeString _), (r) => r.readStack(r.readString _))
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => String.random))) {
      val o = Bag ++ is
      check(pooling, o, (w) => w.writeBag(o, w.writeString _), (r) => r.readBag(r.readString _))
    }

    for (is <- (0 until 3).map(i => (z"0" until i).map(_ => String.random))) {
      val o = HashBag ++ is
      check(pooling, o, (w) => w.writeHashBag(o, w.writeString _), (r) => r.readHashBag(r.readString _))
    }

    {
      val o = PosetTest.poset
      check(pooling, o, (w) => w.writePoset(o, w.writeString _), (r) => r.readPoset(r.readString _))
    }

    {
      val o = {
        val graph = Graph.empty[Z, String]
        val n1 = Z.random
        val n2 = Z.random
        var g = graph + n1 ~> n2
        g = g + n2 ~> n1
        g = g + n1 ~> n2
        g
      }
      check(
        pooling,
        o,
        (w) => w.writeGraph(o, w.writeZ _, w.writeString _),
        (r) => r.readGraph(r.readZ _, r.readString _)
      )
    }

    {
      val o = {
        var uf = UnionFind.create[Z](ISZ(1, 2, 3, 4, 5))
        uf = uf.merge(1, 2)
        uf = uf.merge(3, 4)
        uf = uf.merge(4, 5)
        uf
      }
      check(pooling, o, (w) => w.writeUnionFind(o, w.writeZ _), (r) => r.readUnionFind(r.readZ _))
    }

    {
      import org.sireum.U32._
      val o = message.FlatPos(Some("Hi.txt"), u32"1", u32"1", u32"1", u32"1", u32"1", u32"1")
      check(pooling, o, (w) => w.writePosition(o), (r) => r.readPosition())
    }

    {
      import org.sireum.U32._
      val o = message.DocInfo(None(), ISZ(u32"0", u32"10", u32"40"))
      check(pooling, o, (w) => w.writeDocInfo(o), (r) => r.readDocInfo())
    }

    {
      val o = message.Message(message.Level.Info, None(), "test", "test info")
      check(pooling, o, (w) => w.writeMessage(o), (r) => r.readMessage())
    }
  }

  def check[T](pooling: B, n: T, f: MessagePack.Writer => Unit, g: MessagePack.Reader => T)(
    implicit pos: Position
  ): Unit = {
    val name = n.getClass.getName
    *(s"${name.substring(name.lastIndexOf(".") + 1)} $n") {
      val w = MessagePack.writer(pooling)
      f(w)
      val buf = w.result
      val r = MessagePack.reader(buf)
      r.init()
      val m = g(r)
      assert(m == n)
      true
    }
  }
}
