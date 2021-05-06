package com.chevron.gomica.sql;


import com.chevron.edap.common.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordProvider {
  private static final Logger log = LoggerFactory.getLogger(PasswordProvider.class);

  protected String fromKeyStore(String keystore, String alias, String envVar) throws SQLScriptExecutionException {
    try {
      return ConnectionUtils.readPasswordFromKeystore(keystore, alias);
    } catch(Exception e) {
      log.warn("Can't get password from keystore:" + keystore
                       + " using alias:" + alias
                       + " and default password."
                       + " falling back to Common SecurityUtils."
                       + " reading password from:" + envVar, e);

      return SecurityUtils.readPasswordFromKeystore(keystore, envVar, alias);
    }
  }

//  protected char[] fromHadoop(String alias) throws SQLScriptExecutionException {
//    try {
//        return ConnectionUtils.getPasswordFromSecurityProvider(alias);
//    } catch (Exception e) {
//        log.warn("Can't get password from Hadoop Security Provider"
//          + " using alias:" + alias
//          + " - falling back to Common SecurityUtils");
//        try {
//          return SecurityUtils.getPasswordFromSecurityProvider(alias).toCharArray();
//        } catch (Exception e1) {
//          throw new SQLScriptExecutionException(SQLScriptExecutionException.READ_PWD_ERR, "Unable read password from Hadoop Security Provider", e);
//        }
//    }
//  }

  public String getPassword(String keystore, String alias, String envVar) throws SQLScriptExecutionException {
//    if (null != keystore) {
      return fromKeyStore(keystore, alias, envVar);
//    } else {
//      return fromHadoop(alias);
//    }
  }
}