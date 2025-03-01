package com.ksonni.footballdb.files.services;

import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.queryparser.QueryableRepository;

public interface FilesRepository extends QueryableRepository<FileRegistration, String> {
}
