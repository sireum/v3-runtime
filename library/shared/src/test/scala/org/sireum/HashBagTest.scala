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

class HashBagTest extends SireumRuntimeSpec {

  *(HashBag.empty[String].size =~= z"0")

  *(!HashBag.empty[String].contains("a"))

  *(HashBag.empty[String].add("a").contains("a"))

  *(HashBag.empty[String].add("a").add("a").count("a") == 2)

  *(HashBag.empty[String].addN("a", 4).add("a").count("a") == 5)

  *(HashBag.empty[String].addN("a", 4).remove("a").count("a") == 3)

  *(HashBag.empty[String].addN("a", 4).removeN("a", 10).count("a") == 0)

  *(!HashBag.empty[String].add("a").contains("A"))

  *(HashBag.empty[String].add("a").add("b").contains("a"))

  *(HashBag.empty[String].add("a").add("b").contains("b"))

  *(HashBag.empty[String].addAll(ISZ("a","b")).remove("a").remove("b").isEmpty)

  *(HashBag.empty[String].addAll(ISZ("a","b")) =~= HashBag.empty[String].add("b").add("a"))

  *(HashBag.empty[String].union(HashBag.empty[String].add("A")) =~= HashBag.empty[String].add("A"))

  *(HashBag.empty[String].intersect(HashBag.empty[String].add("A")) =~= HashBag.empty[String])

  *(HashBag.empty[String].add("a").intersect(HashBag.empty[String].add("A")) =~= HashBag.empty[String])

}
