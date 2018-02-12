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

package org.sireum

import org.sireum.test._

class BagTest extends SireumRuntimeSpec {

  *(Bag.empty[String].size =~= z"0")

  *(!Bag.empty[String].contains("a"))

  *(Bag.empty[String].add("a").contains("a"))

  *(Bag.empty[String].add("a").add("a").count("a") == 2)

  *(Bag.empty[String].addN("a", 4).add("a").count("a") == 5)

  *(Bag.empty[String].addN("a", 4).remove("a").count("a") == 3)

  *(Bag.empty[String].addN("a", 4).removeN("a", 10).count("a") == 0)

  *(!Bag.empty[String].add("a").contains("A"))

  *(Bag.empty[String].add("a").add("b").contains("a"))

  *(Bag.empty[String].add("a").add("b").contains("b"))

  *(Bag.empty[String].addAll(ISZ("a","b")).remove("a").remove("b").isEmpty)

  *(Bag.empty[String].addAll(ISZ("a","b")) =~= Bag.empty[String].add("b").add("a"))

  *(Bag.empty[String].union(Bag.empty[String].add("A")) =~= Bag.empty[String].add("A"))

  *(Bag.empty[String].intersect(Bag.empty[String].add("A")) =~= Bag.empty[String])

  *(Bag.empty[String].add("a").intersect(Bag.empty[String].add("A")) =~= Bag.empty[String])

}