/*
 * 🐻‍❄️🌂 analytics-jvm: Client and server implementation of Noelware Analytics in Java, supported for both Java and Kotlin
 * Copyright (c) 2022 Noelware <team@noelware.org>
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

package org.noelware.analytics.tests.server;

import static org.junit.jupiter.api.Assertions.*;

import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.noelware.analytics.jvm.server.extensions.jvm.JvmThreadsExtension;
import org.noelware.analytics.jvm.server.util.GrpcValueUtil;

public class GrpcValueUtilTests {
    @Test
    public void test_ifValuesAreAcceptable() {
        assertDoesNotThrow(() -> GrpcValueUtil.toValue("a string owo"));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(1));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(1L));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(1f));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(1.0));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(true));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(1L));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(new JvmThreadsExtension().supply()));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(List.of("a", "b", "c")));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(ListValue.newBuilder().build()));
        assertDoesNotThrow(() -> GrpcValueUtil.toValue(Struct.newBuilder().build()));

        assertThrows(IllegalStateException.class, () -> {
            GrpcValueUtil.toValue(new Object());
        });

        assertThrows(IllegalStateException.class, () -> {
            GrpcValueUtil.toValue(Struct.newBuilder());
        });
    }
}
