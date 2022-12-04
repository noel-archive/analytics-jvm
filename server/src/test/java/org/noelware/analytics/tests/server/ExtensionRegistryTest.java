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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noelware.analytics.jvm.server.extensions.ExtensionRegistry;
import org.noelware.analytics.jvm.server.extensions.internal.DefaultExtensionRegistry;
import org.noelware.analytics.jvm.server.extensions.jvm.JvmMemoryPoolsExtension;
import org.noelware.analytics.jvm.server.extensions.jvm.JvmThreadsExtension;
import org.noelware.analytics.jvm.server.extensions.jvm.JvmVersionInfoExtension;

public class ExtensionRegistryTest {
    private static final ExtensionRegistry registry = new DefaultExtensionRegistry();

    @BeforeAll
    public static void beforeRun() {
        registry.registerAll(new JvmThreadsExtension(), new JvmMemoryPoolsExtension(), new JvmVersionInfoExtension());
    }

    @Test
    public void test_findByClass() {
        assertNull(registry.findByClass(JvmThreadsExtension.JvmThreadsData.class));
        assertNotNull(registry.findByClass(JvmThreadsExtension.class));
    }
}
