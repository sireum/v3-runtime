/*
 Copyright (c) 2017 Andriy Plokhotnyuk, and respective contributors

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
// From: https://github.com/plokhotnyuk/jsoniter-scala/blob/e089f06c2d8b4bdb87a6874e17bf716e8608b117/jsoniter-scala-examples/src/main/scala-2.13/com/github/plokhotnyuk/jsoniter_scala/examples
package org.sireum.$internal;

public class UnsafeUtils {
  static final sun.misc.Unsafe UNSAFE;
  static {
    try {
      java.lang.reflect.Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      UNSAFE = (sun.misc.Unsafe) field.get(null);
      field.setAccessible(false);
    } catch (Throwable ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }
  public static void releaseFence() {
    UNSAFE.storeFence();
  }
}
