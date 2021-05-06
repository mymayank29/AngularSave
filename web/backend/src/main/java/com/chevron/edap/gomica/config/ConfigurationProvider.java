package com.chevron.edap.gomica.config;

import com.chevron.edap.gomica.security.Role;
import com.chevron.edap.gomica.security.RoleType;
import org.springframework.core.env.Environment;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.io.FilenameUtils.normalize;


public class ConfigurationProvider {

    Environment env;
    private EncryptedPropertiesReader encryptedPropertiesReader;

    public ConfigurationProvider(Environment env) {
        this.env = env;
        this.setup();
    }

    private String envSettingsPath;
    private String keyStorePath;
    private String keyStorePasswordPath;
    private String driverClassName;
    private String userNamePropKey;
    private String credAliasPropKey;
    private String jdbcUrlPropKey;
    private Properties envSettings;
    private String dbUserName;
    private String jdbcUrl;
    private String passwordAlias;
    private String keyStorePassword;
    private String showSql;
    private String hibernateDialect;
    private int dbPoolInitialSize;
    private int dbPoolMaxTotal;
    private int dbPoolMaxIdle;
    private int dbPoolMinIdle;

    /*
  LDAP
   */
    private String ldapUserDn = "";
    private String ldapPasswordAlias;

    private String ldapContextBase;

    private String ldapProviderUrl;
    private String ldapProviderUrl1;
    private String ldapProviderUrl2;
    private String ldapProviderUrl3;
    private String ldapBaseDn;
    private String ldapUserSearchFilter;
    private String ldapGroupSearchBase;
    private String ldapGroupSearchFilter;

    /*
    Auth
     */

    private String localRoles;
    private String localUser;
    private String kerberosPrincipal;
    private String kerberosKeyTabLocation;
    private String krb5ConfLocation;
    private boolean isSpnegoDebugEnabled;
    private boolean isLdapAuthEnabled;
    private Set<Role> enabledEnvSpecificRoles;

    private void setup() {
        this.envSettingsPath = normalizePath(env.getProperty("gomica.env.settings.cnf"));
        this.userNamePropKey = env.getProperty("gomica.db.userNamePropKey");
        this.credAliasPropKey = env.getProperty("gomica.db.credAliasPropKey");
        this.jdbcUrlPropKey = env.getProperty("gomica.db.jdbcUrlPropKey");
        extractEnvSettings();

        this.keyStorePath = normalizePath(env.getProperty("gomica.keystore.file"));
        setupKeyStore();

        this.keyStorePasswordPath = normalizePath(env.getProperty("gomica.keystore.key.file"));
        setupKeyPassword();
        this.isLdapAuthEnabled = Boolean.valueOf(env.getProperty("ldap.auth.enabled"));

        this.driverClassName = env.getProperty("spring.datasource.driverClassName");
        this.showSql = env.getProperty("spring.jpa.show-sql");
        this.hibernateDialect = env.getProperty("spring.jpa.properties.hibernate.dialect");
        this.dbPoolInitialSize = env.getProperty("spring.datasource.initialSize", java.lang.Integer.class, 5);
        this.dbPoolMaxTotal = env.getProperty("spring.datasource.maxTotal", java.lang.Integer.class, 10);
        this.dbPoolMaxIdle = env.getProperty("spring.datasource.maxIdle", java.lang.Integer.class, 10);
        this.dbPoolMinIdle = env.getProperty("spring.datasource.minIdle", java.lang.Integer.class, 5);

        extractAuthSettings();
        extractLdapSettings();
    }

    private void extractEnvSettings() {
        requireNonNull(this.envSettingsPath);
        assert Files.exists(Paths.get(this.envSettingsPath));
        this.envSettings = new Properties();
        try (final Reader reader = new FileReader(this.envSettingsPath)) {
            this.envSettings.load(reader);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        this.dbUserName = envSettings.getProperty(userNamePropKey);
        this.jdbcUrl = envSettings.getProperty(jdbcUrlPropKey);
        this.passwordAlias = envSettings.getProperty(credAliasPropKey);
    }

    private void extractAuthSettings() {
        this.localRoles = envSettings.getProperty("esra.local.roles");
        this.enabledEnvSpecificRoles = extractEnvSpecificRoles(env.getProperty("gomica.enabled.env.specific.roles"));
        this.localUser = envSettings.getProperty("esra.local.user");
        this.kerberosPrincipal = System.getProperty("kerberos.http.principal", env.getProperty("kerberos.http.principal"));
        this.kerberosKeyTabLocation = System.getProperty("kerberos.http.key.tab.location", envSettings.getProperty("kerberos.http.key.tab.location"));
        this.krb5ConfLocation = envSettings.getProperty("kerberosKrb5conf");
        this.isSpnegoDebugEnabled = Boolean.valueOf(env.getProperty("spnego.debug", "false"));
    }

    private Set<Role> extractEnvSpecificRoles(String envSpecificRoles){
        System.out.println("+++++ env roles " + envSpecificRoles);
        if (org.apache.commons.lang3.StringUtils.isEmpty(envSpecificRoles)) {
            return Collections.emptySet();
        }
        return  Arrays.stream(envSpecificRoles.split(",")).map(Role::getRoleFromBusinessName).filter(Objects::nonNull).filter(role -> RoleType.ENV_SPECIFIC == role.getRoleType() ).collect(Collectors.toSet());

    }
    private void extractLdapSettings() {
        this.ldapUserDn = envSettings.getProperty("LdapAccount");
        this.ldapPasswordAlias = envSettings.getProperty("LdapCredentialAlias");
        this.ldapContextBase = env.getProperty("ldapContextBase");

        this.ldapProviderUrl = System.getProperty("ldapProviderUrl", envSettings.getProperty("ldapProviderUrl"));
        this.ldapProviderUrl1 = System.getProperty("ldapProviderUrl1", envSettings.getProperty("ldapProviderUrl1"));
        this.ldapProviderUrl2 = System.getProperty("ldapProviderUrl2", envSettings.getProperty("ldapProviderUrl2"));
        this.ldapProviderUrl3 = System.getProperty("ldapProviderUrl3", envSettings.getProperty("ldapProviderUrl3"));
        this.ldapBaseDn = env.getProperty("ldapBaseDn");
        this.ldapUserSearchFilter = env.getProperty("ldapUserSearchFilter");
        this.ldapGroupSearchBase = env.getProperty("ldapGroupSearchBase");
        this.ldapGroupSearchFilter = env.getProperty("ldapGroupSearchFilter");
    }

    private String normalizePath(String path) {
        if(!path.startsWith("/home") || !path.contains("/conf/")) throw new IllegalArgumentException("Invalid property path");
        return normalize(path);
    }

    private void setupKeyStore() {
        requireNonNull(this.keyStorePath);
        this.encryptedPropertiesReader = EncryptedPropertiesReader.newInstance(Paths.get(this.keyStorePath));
    }

    private void setupKeyPassword() {
        requireNonNull(this.keyStorePasswordPath);
        assert Files.exists(Paths.get(this.keyStorePasswordPath));
        try {
            this.keyStorePassword = new String(Files.readAllBytes(Paths.get(this.keyStorePasswordPath))).trim();
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getDbUserName() {
        return dbUserName;
    }

    public String getDbPassword() {
        return encryptedPropertiesReader.readProperty(this.passwordAlias, this.keyStorePassword);
    }

    public int getDbPoolInitialSize() {
        return dbPoolInitialSize;
    }

    public int getDbPoolMaxTotal() {
        return dbPoolMaxTotal;
    }

    public int getDbPoolMaxIdle() {
        return dbPoolMaxIdle;
    }

    public int getDbPoolMinIdle() {
        return dbPoolMinIdle;
    }

    public String getLdapUserDn() {
        return ldapUserDn;
    }

    public String getLdapPassword() {
        return (localUser == null) ? encryptedPropertiesReader.readProperty(this.ldapPasswordAlias, this.keyStorePassword) : "";
    }

    public String getLdapContextBase() {
        return ldapContextBase;
    }

    public String getLdapProviderUrl() {
        return ldapProviderUrl;
    }

    public String getLdapProviderUrl1() {
        return ldapProviderUrl1;
    }

    public String getLdapProviderUrl2() {
        return ldapProviderUrl2;
    }

    public String getLdapProviderUrl3() {
        return ldapProviderUrl3;
    }

    public String getLdapBaseDn() {
        return ldapBaseDn;
    }

    public String getLdapUserSearchFilter() {
        return ldapUserSearchFilter;
    }

    public String getLdapGroupSearchBase() {
        return ldapGroupSearchBase;
    }

    public String getLdapGroupSearchFilter() {
        return ldapGroupSearchFilter;
    }

    public String getLocalRoles() {
        return localRoles;
    }

    public String getLocalUser() {
        return localUser;
    }

    public String getKerberosPrincipal() {
        return kerberosPrincipal;
    }

    public String getKerberosKeyTabLocation() {
        return kerberosKeyTabLocation;
    }

    public String getKrb5ConfLocation() {
        return krb5ConfLocation;
    }
    public boolean isSpnegoDebugEnabled() {
        return isSpnegoDebugEnabled;
    }

    public boolean isLdapAuthEnabled() {
        return isLdapAuthEnabled;
    }

    public Set<Role> getEnabledEnvSpecificRoles() {
        return enabledEnvSpecificRoles;
    }

    public Environment getEnv() {
        return env;
    }

    public String getEnvSettingsPath() {
        return envSettingsPath;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public String getKeyStorePasswordPath() {
        return keyStorePasswordPath;
    }

    public String getUserNamePropKey() {
        return userNamePropKey;
    }

    public String getCredAliasPropKey() {
        return credAliasPropKey;
    }

    public String getJdbcUrlPropKey() {
        return jdbcUrlPropKey;
    }

    public Properties getEnvSettings() {
        return envSettings;
    }

    public String getPasswordAlias() {
        return passwordAlias;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getShowSql() {
        return showSql;
    }

    public String getHibernateDialect() {
        return hibernateDialect;
    }
}




