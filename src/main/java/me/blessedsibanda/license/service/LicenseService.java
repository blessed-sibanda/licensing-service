package me.blessedsibanda.license.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import me.blessedsibanda.license.config.ServiceConfig;
import me.blessedsibanda.license.model.License;
import me.blessedsibanda.license.repository.LicenseRepository;

@Service
public class LicenseService {
    @Autowired
    MessageSource messages;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private ServiceConfig config;

    public License getLicense(String licenseId, String organizationId) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (license == null) {
            throw new IllegalArgumentException(
                    String.format(messages.getMessage("license.search.error.message", null, null), organizationId,
                            licenseId));
        }

        return license.withComment(config.getProperty());
    }

    public License createLicense(License license, String organizationId) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license;
    }

    public License updateLicense(License license, String organizationId) {
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public String deleteLicense(String licenseId) {
        var license = licenseRepository.findById(licenseId);
        String organizationId = null;
        if (license.isPresent()) {
            organizationId = license.get().getOrganizationId();
            licenseRepository.delete(license.get());
        }

        var responseMessage = String.format(messages.getMessage(
                "license.delete.message", null, null),
                licenseId,
                organizationId);
        return responseMessage;
    }
}
