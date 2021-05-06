package com.chevron.edap.gomica.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;

public class EncryptedPropertiesReader {

    private Path keyStoreLocation;

    protected EncryptedPropertiesReader(Path keyStoreLocation) {
        this.keyStoreLocation = keyStoreLocation;
    }

    public String readProperty(final String alias, final String keyStorePass) {
        return readProperty(alias, keyStorePass, keyStorePass);
    }

    public  String readProperty(final String alias, final String keystorePass, final String keyPass) {
        try  (InputStream keystoreStream = new FileInputStream(keyStoreLocation.toFile())){
                final KeyStore keyStore = KeyStore.getInstance("JCEKS");
                keyStore.load(keystoreStream, keystorePass.toCharArray());
                if (keyStore.containsAlias(alias)) {
                    final Key key = keyStore.getKey(alias, keyPass.toCharArray());
                    return new String(key.getEncoded());
                } else {
                    throw new IllegalArgumentException("The store doesn't contain the key");
                }
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException e) {
            throw new IllegalStateException("Something went wrong", e);
        }
    }

    public static EncryptedPropertiesReader newInstance(Path keyStoreLocation) {
        return new EncryptedPropertiesReader(keyStoreLocation);
    }

}
