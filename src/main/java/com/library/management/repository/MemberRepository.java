package com.library.management.repository;

import com.library.management.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findAll(Pageable pageable);

    Page<Member> findByDeletedFalse(Pageable pageable);
}
