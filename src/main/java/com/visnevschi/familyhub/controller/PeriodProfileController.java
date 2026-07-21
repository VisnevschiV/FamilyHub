package com.visnevschi.familyhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dto.PeriodProfile.CreatePeriodProfileRequest;
import com.visnevschi.familyhub.dto.PeriodProfile.FamilyMemberMonthResponse;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodDateRequest;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodMonthResponse;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodProfileResponse;
import com.visnevschi.familyhub.dto.PeriodProfile.UpdatePeriodProfileRequest;
import com.visnevschi.familyhub.service.PeriodProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/personas/me/period-profile")
public class PeriodProfileController {

    private final PeriodProfileService periodProfileService;

    public PeriodProfileController(PeriodProfileService periodProfileService) {
        this.periodProfileService = periodProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PeriodProfileResponse create(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody(required = false) CreatePeriodProfileRequest request) {
        return periodProfileService.createForEmail(jwt.getSubject(), request);
    }

    @GetMapping
    public ResponseEntity<PeriodProfileResponse> get(@AuthenticationPrincipal Jwt jwt) {
        return periodProfileService.getForEmailIfExists(jwt.getSubject())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/family")
    public List<PeriodProfileResponse> getFamily(@AuthenticationPrincipal Jwt jwt) {
        return periodProfileService.getFamilyPeriodsForEmail(jwt.getSubject());
    }

    @PatchMapping
    public PeriodProfileResponse update(@AuthenticationPrincipal Jwt jwt,
                                        @Valid @RequestBody UpdatePeriodProfileRequest request) {
        return periodProfileService.updateForEmail(jwt.getSubject(), request);
    }

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public PeriodProfileResponse start(@AuthenticationPrincipal Jwt jwt,
                                       @Valid @RequestBody PeriodDateRequest request) {
        return periodProfileService.startPeriodForEmail(jwt.getSubject(), request);
    }

    @GetMapping("/records/month")
    public PeriodMonthResponse getMonth(@AuthenticationPrincipal Jwt jwt,
                                        @RequestParam int year,
                                        @RequestParam int month) {
        return periodProfileService.getMonthForEmail(jwt.getSubject(), year, month);
    }

    @GetMapping("/family/records/month")
    public List<FamilyMemberMonthResponse> getFamilyMonth(@AuthenticationPrincipal Jwt jwt,
                                                          @RequestParam int year,
                                                          @RequestParam int month) {
        return periodProfileService.getFamilyMonthForEmail(jwt.getSubject(), year, month);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt) {
        periodProfileService.deleteForEmail(jwt.getSubject());
    }
}
