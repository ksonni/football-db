package com.ksonni.footballdb.files.services;

import com.ksonni.footballdb.config.MapStructConfig;
import com.ksonni.footballdb.files.domain.FileRegistration;
import com.ksonni.footballdb.files.dto.FileRegistrationResponse;
import com.ksonni.footballdb.generated.ql.QLFileRegistrationPage;
import com.ksonni.footballdb.queryparser.PageResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface FilesMapper {

    /**
     * Generated FilesMapper instance.
     */
    FilesMapper INSTANCE = Mappers.getMapper(FilesMapper.class);

    /**
     * Maps a File to FileResponse.
     *
     * @param fileRegistration File
     * @return mapped response
     */
    FileRegistrationResponse toFileRegistrationResponse(FileRegistration fileRegistration);

    /**
     * Maps a PageResult to QLFileRegistrationPage DTO.
     *
     * @param page PageResult
     * @return QLFileRegistrationPage
     */
    QLFileRegistrationPage toQLPage(PageResult<FileRegistration> page);
}
