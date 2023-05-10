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

package org.veritas.assessment.common.metadata;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class Pageable<T> implements Serializable {
    /**
     * All element count including other pages.
     */
    private int total = 0;
    /**
     * page size
     */
    private int pageSize = 10;
    /**
     * current page number
     */
    private int page = 1;
    /**
     * page count
     */
    private int pageCount;

    /**
     * records of current page.
     */
    private List<T> records = Collections.emptyList();

    public static <T> Pageable<T> convert(Page<T> mybatisPlusPage) {
        Pageable<T> pageable = new Pageable<>();
        pageable.setPageCount((int) mybatisPlusPage.getPages());
        pageable.setRecords(mybatisPlusPage.getRecords());
        pageable.setPage((int) mybatisPlusPage.getCurrent());
        pageable.setPageSize((int) mybatisPlusPage.getSize());
        pageable.setTotal((int) mybatisPlusPage.getTotal());
        return pageable;
    }

    public static <T> Pageable<T> noRecord(int page, int pageSize) {
        Pageable<T> pageable = new Pageable<>();
        pageable.setRecords(Collections.emptyList());
        pageable.setPageCount(1);
        pageable.setPage(page);
        pageable.setPageSize(pageSize);
        pageable.setTotal(0);
        return pageable;
    }

    public List<T> getRecords() {
        return Collections.unmodifiableList(records);
    }
}
