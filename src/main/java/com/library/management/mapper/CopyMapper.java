package com.library.management.mapper;

import com.library.management.dto.CopyInsertDTO;
import com.library.management.dto.CopyReadOnlyDTO;
import com.library.management.model.Copy;
import org.springframework.stereotype.Component;

@Component
public class CopyMapper {

    public Copy mapToCopyEntity(CopyInsertDTO copyInsertDTO) {

        Copy copy = new Copy();

        copy.setAvailable(copyInsertDTO.available());
        copy.setCondition(copyInsertDTO.condition());

        return copy;
    }

    public CopyReadOnlyDTO mapToCopyReadOnlyDTO(Copy copy) {

        return new CopyReadOnlyDTO(
                copy.getId(),
                copy.getBook().getId(),
                copy.getBook().getTitle(),
                copy.getAvailable(),
                copy.getCondition()
        );
    }
}
