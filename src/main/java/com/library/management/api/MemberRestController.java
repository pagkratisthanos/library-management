package com.library.management.api;

import com.library.management.core.exceptions.EntityAlreadyExistsException;
import com.library.management.core.exceptions.EntityInvalidArgumentException;
import com.library.management.core.exceptions.EntityNotFoundException;
import com.library.management.dto.MemberInsertDTO;
import com.library.management.dto.MemberReadOnlyDTO;
import com.library.management.dto.MemberUpdateDTO;
import com.library.management.mapper.MemberMapper;
import com.library.management.model.Member;
import com.library.management.service.IMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    private final MemberMapper memberMapper;

    @Operation(summary = "Save a member")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<MemberReadOnlyDTO> saveMember(
            @Valid @RequestBody MemberInsertDTO dto)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {
        Member savedMember = memberService.saveMember(dto);
        MemberReadOnlyDTO responseDTO = memberMapper.mapToMemberReadOnlyDTO(savedMember);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(responseDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @Operation(summary = "Update a member")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{uuid}")
    public ResponseEntity<MemberReadOnlyDTO> updateMember(
            @PathVariable UUID uuid,
            @Valid @RequestBody MemberUpdateDTO dto)
            throws EntityNotFoundException, EntityAlreadyExistsException, EntityInvalidArgumentException {
        Member updatedMember = memberService.updateMember(uuid, dto);
        return ResponseEntity.ok(memberMapper.mapToMemberReadOnlyDTO(updatedMember));
    }

    @Operation(summary = "Delete a member")
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID uuid)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        memberService.deleteMemberByUuid(uuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a member by uuid")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{uuid}")
    public ResponseEntity<MemberReadOnlyDTO> getMember(@PathVariable UUID uuid)
            throws EntityNotFoundException {
        Member member = memberService.getMemberByUUIDDeletedFalse(uuid);
        return ResponseEntity.ok(memberMapper.mapToMemberReadOnlyDTO(member));
    }

    @Operation(summary = "Get all members paginated")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<Page<MemberReadOnlyDTO>> getMembers(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<Member> members = memberService.getMembersPaginatedAndDeletedFalse(pageable);
        return ResponseEntity.ok(members.map(memberMapper::mapToMemberReadOnlyDTO));
    }
}