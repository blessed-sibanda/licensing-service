package me.blessedsibanda.license.controller;

import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.blessedsibanda.license.model.License;
import me.blessedsibanda.license.service.LicenseService;

@RestController
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenseController {
        @Autowired
        private LicenseService licenseService;

        @GetMapping("/{licenseId}")
        public ResponseEntity<License> getLicense(
                        @PathVariable("organizationId") String organizationId,
                        @PathVariable("licenseId") String licenseId) {
                License license = licenseService.getLicense(licenseId, organizationId, "");
                license.add(
                                linkTo(methodOn(LicenseController.class).getLicense(organizationId,
                                                license.getLicenseId()))
                                                .withSelfRel(),
                                linkTo(methodOn(LicenseController.class).createLicense(organizationId, license, null))
                                                .withRel("createLicense"),
                                linkTo(methodOn(LicenseController.class).updateLicense(organizationId, license))
                                                .withRel("updateLicense"),
                                linkTo(methodOn(LicenseController.class).deleteLicense(organizationId,
                                                license.getLicenseId()))
                                                .withRel("deleteLicense"));
                return ResponseEntity.ok(license);
        }

        @PutMapping
        public ResponseEntity<License> updateLicense(
                        @PathVariable("organizationId") String organizationId,
                        @RequestBody License license) {
                return ResponseEntity.ok(licenseService.updateLicense(
                                license, organizationId));
        }

        @PostMapping
        public ResponseEntity<License> createLicense(
                        @PathVariable("organizationId") String organizationId,
                        @RequestBody License license,
                        @RequestHeader(value = "Accept-Language", required = false) Locale locale) {
                return ResponseEntity.ok(licenseService.createLicense(
                                license, organizationId));
        }

        @DeleteMapping("/{licenseId}")
        public ResponseEntity<String> deleteLicense(
                        @PathVariable("organizationId") String organizationId,
                        @PathVariable("licenseId") String licenseId) {
                return ResponseEntity.ok(licenseService.deleteLicense(licenseId));
        }

        @GetMapping("/{licenseId}/{clientType}")
        public License getLicensesWithClient(
                @PathVariable("organizationId") String organizationId,
                @PathVariable("licenseId") String licenseId,
                @PathVariable("clientType") String clientType
        ) {
                return licenseService.getLicense(licenseId, organizationId, clientType);
        }
}
