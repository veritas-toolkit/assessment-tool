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

package org.veritas.assessment.biz.converter;

import org.veritas.assessment.common.metadata.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Converter<T, S> {

    /**
     * Convert {@link T} object to {@link S} object
     *
     * @param source source object
     * @return Target object
     */
    T convertFrom(S source);

    default T convertFrom(S source, Consumer<T> postAction) {
        T dto = convertFrom(source);
        postAction.accept(dto);
        return dto;
    }

    default T convertFrom(S source, BiConsumer<T, S> postAction) {
        T dto = convertFrom(source);
        postAction.accept(dto, source);
        return dto;
    }

    default List<T> convertFrom(List<S> sourceList) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> dtoList = new ArrayList<>(sourceList.size());
        sourceList.forEach(source -> {
            dtoList.add(convertFrom(source));
        });
        return dtoList;
    }

    default List<T> convertFrom(List<S> sourceList, Consumer<T> postAction) {
        List<T> dtoList = convertFrom(sourceList);
        if (postAction != null) {
            dtoList.forEach(postAction);
        }
        return dtoList;
    }

    default List<T> convertFrom(List<S> sourceList, BiConsumer<T, S> postAction) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> dtoList = new ArrayList<>(sourceList.size());
        sourceList.forEach(source -> {
            dtoList.add(convertFrom(source, postAction));
        });
        return dtoList;
    }


    default Pageable<T> convertFrom(Pageable<S> sourcePageable) {
        Objects.requireNonNull(sourcePageable, "The arg[sourcePageable] cannot be null.");
        Pageable<T> targetPageable = new Pageable<>();
        List<T> records = convertFrom(sourcePageable.getRecords());
        targetPageable.setRecords(records);
        targetPageable.setPage(sourcePageable.getPage());
        targetPageable.setPageCount(sourcePageable.getPageCount());
        targetPageable.setPageSize(sourcePageable.getPageSize());
        targetPageable.setTotal(sourcePageable.getTotal());
        return targetPageable;
    }

    default Pageable<T> convertFrom(Pageable<S> sourcePageable, Consumer<T> postAction) {
        Objects.requireNonNull(sourcePageable, "The arg[sourcePageable] cannot be null.");
        Pageable<T> targetPageable = new Pageable<>();
        List<T> records = convertFrom(sourcePageable.getRecords(), postAction);
        targetPageable.setRecords(records);
        targetPageable.setPage(sourcePageable.getPage());
        targetPageable.setPageCount(sourcePageable.getPageCount());
        targetPageable.setPageSize(sourcePageable.getPageSize());
        targetPageable.setTotal(sourcePageable.getTotal());
        return targetPageable;
    }
    default Pageable<T> convertFrom(Pageable<S> sourcePageable, BiConsumer<T, S> postAction) {
        Objects.requireNonNull(sourcePageable, "The arg[sourcePageable] cannot be null.");
        Pageable<T> targetPageable = new Pageable<>();
        List<T> records = convertFrom(sourcePageable.getRecords(), postAction);
        targetPageable.setRecords(records);
        targetPageable.setPage(sourcePageable.getPage());
        targetPageable.setPageCount(sourcePageable.getPageCount());
        targetPageable.setPageSize(sourcePageable.getPageSize());
        targetPageable.setTotal(sourcePageable.getTotal());
        return targetPageable;
    }
}
