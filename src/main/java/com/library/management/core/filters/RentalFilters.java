package com.library.management.core.filters;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RentalFilters {
    private String search;
    private UUID memberUuid;
    private UUID copyUuid;
    private String active;
}