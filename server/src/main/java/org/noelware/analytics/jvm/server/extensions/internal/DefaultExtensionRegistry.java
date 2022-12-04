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

package org.noelware.analytics.jvm.server.extensions.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;
import org.noelware.analytics.jvm.server.extensions.Extension;
import org.noelware.analytics.jvm.server.extensions.ExtensionRegistry;

public class DefaultExtensionRegistry implements ExtensionRegistry {
    private final ArrayList<Extension<?>> extensions = new ArrayList<>();

    /**
     * Returns all the extensions that were register as an immutable list.
     */
    @Override
    public List<? extends Extension<?>> extensions() {
        return Collections.unmodifiableList(extensions);
    }

    /**
     * Finds a extension by the given {@link Class<T> class}.
     *
     * @param tClass The extension class
     * @return the extension or <code>null</code> if not found.
     */
    @Override
    @SuppressWarnings("unchecked") // it's already checked in the #filter function.
    public @Nullable <T> Extension<T> findByClass(Class<T> tClass) {
        final Optional<Extension<?>> extension = extensions.stream()
                .filter(f -> {
                    try {
                        // Check if `tClass` can be cast to this extension class
                        tClass.cast(f);
                        return true;
                    } catch (ClassCastException e) {
                        // If not, we just return false and try again
                        return false;
                    }
                })
                .findAny();

        return (Extension<T>) extension.orElse(null);
    }

    /**
     * Registers an extension to this registry
     *
     * @param extension extension to register
     */
    @Override
    public <T> void register(Extension<T> extension) {
        extensions.add(extension);
    }
}
