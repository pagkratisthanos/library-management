package com.library.management.core.filters;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BookFilters {
    private String title;
    private String isbn;
    private String language;
    private String description;
}