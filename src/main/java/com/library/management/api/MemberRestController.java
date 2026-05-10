package com.library.management.api;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberReadOnlyDTO;
import com.library.management.dto.MemberUpdateDTO;
import com.library.management.service.IMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberRestController {

    private final IMemberService memberService;

    @PostMapping
    public ResponseEntity<MemberReadOnlyDTO> saveMember(
            @Valid @RequestBody MemberInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {

        MemberReadOnlyDTO savedMember = memberService.saveMember(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(savedMember.id())
                .toUri();

        return ResponseEntity.created(location).body(savedMember);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<MemberReadOnlyDTO> updateMember(
            @PathVariable UUID uuid,
            @Valid @RequestBody MemberUpdateDTO dto)
            throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidArgumentException {
        return ResponseEntity.ok(memberService.updateMember(uuid, dto));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        memberService.deleteMemberByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<MemberReadOnlyDTO> getMember(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        return ResponseEntity.ok(memberService.getMemberByUUIDDeletedFalse(uuid));
    }

    @GetMapping
    public ResponseEntity<Page<MemberReadOnlyDTO>> getMembers(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(memberService.getMembersPaginatedAndDeletedFalse(pageable));
    }
}