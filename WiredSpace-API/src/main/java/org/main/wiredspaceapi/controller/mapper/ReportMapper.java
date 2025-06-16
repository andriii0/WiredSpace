package org.main.wiredspaceapi.controller.mapper;

import org.main.wiredspaceapi.controller.dto.post.ReportDTO;
import org.main.wiredspaceapi.domain.Report;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    ReportDTO toDto(Report report);

    Report toEntity(ReportDTO dto);
}