/*
 * 🌂 analytics-jvm: Client and server implementation of Noelware Analytics in Java, supported for both Java and Kotlin
 * Copyright (c) 2022 Noelware
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.noelware.analytics.client.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a generic response object
 *
 * @since 28.06.22
 * @author Noel <cutie@floofy.dev>
 */
public class Response<T> {
  private final Exception exception;
  private final boolean success;
  private final T data;

  public Response(@NotNull T data) {
    this(data, true, null);
  }

  public Response(@NotNull Exception exception) {
    this(null, false, exception);
  }

  protected Response(@Nullable T data, boolean success, @Nullable Exception exception) {
    this.exception = exception;
    this.success = success;
    this.data = data;
  }

  /** Returns the data response type, if the response was a success. */
  @Nullable
  public T getData() {
    return data;
  }

  /** Returns if the RPC call was a success. */
  public boolean getSuccess() {
    return success;
  }

  /** Returns the Exception object if the RPC call was not a success. */
  @Nullable
  public Exception getException() {
    return exception;
  }

  /**
   * Returns the underlying data if the RPC call was a success, or the exception is thrown.
   *
   * @throws Exception If the RPC call was not a success, this will throw the exception object.
   */
  @NotNull
  public T getOrThrow() throws Exception {
    if (this.data == null) {
      assert exception != null : "Data was null but no exception(?)";
      throw exception;
    }

    return this.data;
  }
}
