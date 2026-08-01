package com.library.management.core.filters;

import com.library.management.model.CopyCondition;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CopyFilters {
    private String bookTitle;
    private Boolean available;
    private CopyCondition condition;
}