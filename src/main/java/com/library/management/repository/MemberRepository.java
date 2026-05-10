package com.library.management.repository;

import com.library.management.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Page<Member> findAll(Pageable pageable);
    Page<Member> findByDeletedFalse(Pageable pageable);

    Optional<Member> findById(UUID uuid);
    Optional<Member> findByIdAndDeletedFalse(UUID uuid);
    Optional<Member> findByEmail(String email);           // ← πρόσθεσε!
    Optional<Member> findByPhoneNumber(String phoneNumber); // ← πρόσθεσε!

    boolean existsById(UUID uuid);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmailAndIdNot(String email, UUID uuid);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, UUID uuid);
}
