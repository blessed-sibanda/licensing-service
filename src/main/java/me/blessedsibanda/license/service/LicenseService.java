package me.blessedsibanda.license.service;

import java.util.UUID;

import me.blessedsibanda.license.model.Organization;
import me.blessedsibanda.license.service.client.OrganizationDiscoveryClient;
import me.blessedsibanda.license.service.client.OrganizationRestTemplateClient;
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

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestTemplateClient;

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

    public License getLicense(String licenseId, String organizationId, String clientType) {
        License license =
                licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId)
;
        if (license == null) {
            throw new IllegalArgumentException(String.format(
                    messages.getMessage("license.search.error.message", null, null),
                    licenseId, organizationId
            ));
        }
        Organization organization = retrieveOrganizationInfo(organizationId, clientType);

        if (organization != null) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }
        return license.withComment(config.getProperty());
    }

    private Organization retrieveOrganizationInfo(
            String organizationId,
            String clientType
    ) {
        Organization organization = null;
        switch (clientType) {
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestTemplateClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
        }
        return organization;
    }
}
