package com.library.management.core.filters;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserFilters {
    private String search;
    private String role;
}