package com.library.management.core.filters;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AuthorFilters {
    private String firstname;
    private String lastname;
    private String birthPlace;
}