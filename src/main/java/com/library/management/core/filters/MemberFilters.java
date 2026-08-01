package com.library.management.core.filters;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MemberFilters {
    private String search;
    private String firstname;
    private String lastname;
    private String email;
    private String phoneNumber;
}