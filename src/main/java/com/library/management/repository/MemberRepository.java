package com.library.management.repository;

import com.library.management.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Page<Member> findAll(Pageable pageable);
    Page<Member> findByDeletedFalse(Pageable pageable);

    Optional<Member> findByUuid(UUID uuid);
    Optional<Member> findByUuidAndDeletedFalse(UUID uuid);
    Optional<Member> findByEmail(String email);           // ← πρόσθεσε!
    Optional<Member> findByPhoneNumber(String phoneNumber); // ← πρόσθεσε!

    boolean existsByUuid(UUID uuid);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
