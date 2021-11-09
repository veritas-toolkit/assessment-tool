/*
 * Copyright 2021 MAS Veritas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veritas.assessment.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;
import java.util.function.Supplier;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Duplicate entity.")
public class DuplicateException extends AbstractVeritasException {
    public DuplicateException() {
    }

    public DuplicateException(String message) {
        super(message);
    }

    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new DuplicateException(message);
        }
        return obj;
    }

    public static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
        if (obj == null) {
            throw new DuplicateException(messageSupplier.get());
        }
        return obj;
    }

    public static <T, C extends Collection<T>> C requireNonEmpty(C objCollection, String message) {
        if (objCollection == null || objCollection.isEmpty()) {
            throw new DuplicateException(message);
        }
        return objCollection;
    }
}
