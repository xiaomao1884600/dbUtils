package com.doubeye.commons.utils.ldap;

import junit.framework.TestCase;
import org.junit.Assert;

public class LDAPUtilsTest extends TestCase{
    private static final String LDAP_URL = "ldap://hxsddc.hxsd.local";
    public void testCorrectVerification() {
        String userName = "adread";
        String passpord = "Hxsdp@ssword";
        LDAPConfiguration configuration = new LDAPConfiguration();
        configuration.setUrl(LDAP_URL);
        configuration.setSecurityAuthentication(LDAPConfiguration.SECURITY_AUTHENTICATION_SIMPLE);
        LDAPVerificationResult result = LDAPUtils.identityVerification(configuration.getLDAPEnvironment(), userName, passpord);
        System.out.println("isWrongUserNameOrPassword=" + result.isWrongUserNameOrPassword());
        Assert.assertEquals("should success ", result.isSuccess(), true);
    }

    public void testWrongPassword() {
        String userName = "adread";
        String passpord = "wrongPassword";
        LDAPConfiguration configuration = new LDAPConfiguration();
        configuration.setUrl(LDAP_URL);
        configuration.setSecurityAuthentication(LDAPConfiguration.SECURITY_AUTHENTICATION_SIMPLE);
        LDAPVerificationResult result = LDAPUtils.identityVerification(configuration.getLDAPEnvironment(), userName, passpord);
        Assert.assertTrue("wrong username or password", result.isWrongUserNameOrPassword());
    }

    public void testUserNotExists() {
        String userName = "adread_not_exists";
        String passpord = "H1xsdp@ssword";
        LDAPConfiguration configuration = new LDAPConfiguration();
        configuration.setUrl(LDAP_URL);
        configuration.setSecurityAuthentication(LDAPConfiguration.SECURITY_AUTHENTICATION_SIMPLE);
        LDAPVerificationResult result = LDAPUtils.identityVerification(configuration.getLDAPEnvironment(), userName, passpord);
        Assert.assertTrue("wrong username or password ", result.isWrongUserNameOrPassword());
    }
}
