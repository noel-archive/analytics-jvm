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

package org.noelware.analytics.server.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.noelware.analytics.server.plugins.Plugin;
import org.noelware.analytics.server.plugins.PluginRegistry;

public class PluginRegistryImpl implements PluginRegistry {
  private final HashMap<Class<? extends Plugin>, Plugin> plugins = new HashMap<>();

  @NotNull
  @Override
  public List<Plugin> getPlugins() {
    return plugins.values().stream().toList();
  }

  @Override
  public @Nullable <T extends Plugin> T getPluginByClass(Class<Plugin> cls) throws Exception {
    try {
      var plugin =
          plugins.entrySet().stream()
              .filter(entry -> entry.getKey() == cls)
              .findAny()
              .orElseThrow()
              .getValue();

      return (T) plugin;
    } catch (NoSuchElementException e) {
      return null;
    } catch (Exception e) {
      throw new Exception("Unable to find plugin with class", e);
    }
  }

  @Override
  public void onEachPlugin(Function<Plugin, Void> lambda) {
    for (Plugin plugin : getPlugins()) {
      lambda.apply(plugin);
    }
  }

  @Override
  public void addPlugin(Class<Plugin> pluginClass) throws Exception {
    try {
      var plugin = pluginClass.getConstructor().newInstance();

      plugin.onInit(this);
      plugins.putIfAbsent(pluginClass, plugin);
    } catch (InvocationTargetException | InstantiationException | IllegalStateException e) {
      throw new Exception("Unable to create new plugin instance", e);
    } catch (NoSuchElementException e) {
      throw new Exception("Unable to find primary constructor with 0 arguments", e);
    }
  }

  @Override
  public void addPlugin(Plugin plugin) {
    plugin.onInit(this);
    plugins.putIfAbsent(plugin.getClass(), plugin);
  }

  @Override
  public void close() throws Exception {
    onEachPlugin(
        plugin -> {
          plugin.onDestroy(this);
          return null;
        });

    plugins.clear();
  }
}
