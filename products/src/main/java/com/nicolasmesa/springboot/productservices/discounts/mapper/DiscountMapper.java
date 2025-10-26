package com.nicolasmesa.springboot.productservices.discounts.mapper;

import com.nicolasmesa.springboot.productservices.discounts.dto.DiscountDto;
import com.nicolasmesa.springboot.productservices.discounts.entity.Discount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    Discount toEntity(DiscountDto dto);

    @Mapping(target = "startDateTime", source = "startDateTime", qualifiedByName = "truncateToSeconds")
    @Mapping(target = "endDateTime", source = "endDateTime", qualifiedByName = "truncateToSeconds")
    DiscountDto toDto(Discount entity);

    @Mapping(target = "startDateTime", source = "startDateTime", qualifiedByName = "truncateToSeconds")
    @Mapping(target = "endDateTime", source = "endDateTime", qualifiedByName = "truncateToSeconds")
    List<DiscountDto> toDto(List<Discount> entity);

    @Named("truncateToSeconds")
    default LocalDateTime truncateToSeconds(LocalDateTime dt) {
        return dt == null ? null : dt.truncatedTo(ChronoUnit.SECONDS);
    }
}
