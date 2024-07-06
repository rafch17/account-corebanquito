package com.banquito.core.account.util.mapper;

import com.banquito.core.account.dto.DebitCardDTO;
import com.banquito.core.account.model.DebitCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DebitCardMapper {
    @Mapping(source = "account.id", target = "accountId")
    DebitCardDTO toDTO(DebitCard debitCard);

    @Mapping(source = "accountId", target = "account.id")
    DebitCard toPersistence(DebitCardDTO dto);
}