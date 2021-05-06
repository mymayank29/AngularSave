package com.chevron.gomica.sql;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * Created by ewmw on 5/22/2014.
 * Utilities...
 */

public class ConnectionUtils {

    public static final String OOZIE_ACTION_CONF_XML_PROPERTY = "oozie.action.conf.xml";

    private static final String KEYSTORE_PASS = "none";

    private static final String KEY_PASS = "none";

    private static final DateFormat[] dfs = new SimpleDateFormat[]{
            new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a"),
            new SimpleDateFormat("MM/dd/yyyy hh:mm a"),
            new SimpleDateFormat("MM/dd/yyyy"),
            new SimpleDateFormat("yyyy-MM-dd")};
    static {
    	for (DateFormat df : dfs) {
    		df.setTimeZone(TimeZone.getTimeZone("UTC"));
    	}
    }



    public static String readPasswordFromKeystore(final String keystoreLocation, final String alias) {
        return readProperty(keystoreLocation, KEYSTORE_PASS, alias);
    }

    public static String readProperty(final String keyStoreLocation, final String keystorePass, final String alias) {
        InputStream keystoreStream = null;
        try  {
            try{
                keystoreStream = new FileInputStream(keyStoreLocation);
                final KeyStore keyStore = KeyStore.getInstance("JCEKS");
                keyStore.load(keystoreStream, keystorePass.toCharArray());
                if (keyStore.containsAlias(alias)) {
                    final Key key = keyStore.getKey(alias, KEY_PASS.toCharArray());
                    return new String(key.getEncoded());
                } else {
                    throw new IllegalArgumentException("The store doesn't contain the key");
                }
            } finally {
                    keystoreStream.close();
            }
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
            throw new IllegalStateException("Something went wrong", e);
        }

    }
    public static String readPasswordFromKeystore(final String keystoreLocation, final String keystorePass, final String alias) {
        try {
            InputStream keystoreStream = null;
            Key key = null;
            String pass = null;
            try {

                keystoreStream = new FileInputStream(keystoreLocation);
                KeyStore keyStore = KeyStore.getInstance("JCEKS");
                keyStore.load(keystoreStream, keystorePass.toCharArray());
                if (!keyStore.containsAlias(alias)) {
                    throw new IllegalArgumentException("The store doesn't contain the alias for " + alias);
                }
                key = keyStore.getKey(alias, KEY_PASS.toCharArray());
                pass = new String(key.getEncoded());
            } finally {
                if(keystoreStream != null) {
                    keystoreStream.close();
                }
            }
            return pass;
        } catch (Exception e) {
            throw new IllegalStateException("Something goes wrong with your keystore", e);
        }
    }

    /**
     * @deprecated
     */
    public static char[] getPasswordFromSecurityProvider(final String alias) throws Exception {
        Configuration configuration = new Configuration();
        // Make configuration available inside java action
        String configurationLocation = System.getProperty(OOZIE_ACTION_CONF_XML_PROPERTY);
        Path localConfigurationPath = new Path(configurationLocation);
        configuration.addResource(localConfigurationPath);
        char[] aliasVal = null;
        try {
            // All security in place, using the simplest method possible
            aliasVal = configuration.getPassword(alias);
            if (aliasVal == null) {
                // throw new Exception("Unable to read value for alias " + alias);
                System.out.println("Nothing found in store for " + alias);
                return null;
            } else {
                System.out.println("Non-empty value got from store for " + alias);
                return aliasVal;
            }
        } catch (IOException e) {
            throw new Exception("Problem occurred during reading credential storage", e);
        } finally {
            if(aliasVal != null) {
                Arrays.fill(aliasVal, ' ');
            }
        }
    }
}
