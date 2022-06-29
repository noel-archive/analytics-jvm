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

package org.noelware.analytics.server.plugins;

import com.google.protobuf.Value;
import java.util.Map;
import org.noelware.analytics.server.Pair;

/**
 * Represents a plugin, to extend the statistics that the Analytics server can share and visualise,
 * and optionally store (if you want to)!
 *
 * <p>The plugin has lifecycle hooks, {@link
 * #onInit(org.noelware.analytics.server.plugins.PluginRegistry)} and {@link
 * #onDestroy(org.noelware.analytics.server.plugins.PluginRegistry)}.
 */
public interface Plugin {
  /** Called when the plugin is being initialized. */
  default void onInit(PluginRegistry registry) {}

  /** Called when the plugin is being destroyed. */
  default void onDestroy(PluginRegistry registry) {}

  /** Extra statistics to push to the analytics server. */
  default Pair<String, Map<String, Value>> getStatistics() {
    return null;
  }
}
