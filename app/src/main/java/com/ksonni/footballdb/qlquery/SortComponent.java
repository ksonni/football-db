package com.ksonni.footballdb.qlquery;

import org.springframework.data.domain.Sort;

record SortComponent(String key, Integer order, boolean desc) {
    Sort build() {
        return desc ? Sort.by(key).descending() : Sort.by(key);
    }
}
