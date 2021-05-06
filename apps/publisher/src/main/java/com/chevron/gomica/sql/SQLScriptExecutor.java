// package com.chevron.gomica.sql;


// import org.apache.commons.cli.*;
// import org.apache.hadoop.conf.Configuration;
// import org.apache.hadoop.fs.FileSystem;
// import org.apache.hadoop.fs.Path;
// import org.apache.hadoop.fs.Path;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import java.io.*;
// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Properties;

// public class SQLScriptExecutor {
//     private static final Logger log = LoggerFactory.getLogger(SQLScriptExecutor.class);

//     private final static boolean debug = Boolean.getBoolean("sql.script.executor.debug");
//     private PasswordProvider pwdProvider;
//     private String connectionUrl;
//     private String user = null;
//     private String pwd = null;
//     private String pwda = null;
//     private String pwde = null;
//     private String keystore = null;
//     private String scriptFilePath = null;
//     private String query = null;

//     public SQLScriptExecutor(PasswordProvider pwdProvider) {
//       super();
//       this.pwdProvider = pwdProvider;
//     }

//     public String getConnectionUrl() {
//       return connectionUrl;
//     }

//     public void setConnectionUrl(String connectionUrl) {
//       this.connectionUrl = connectionUrl;
//     }

//     public String getUser() {
//       return user;
//     }

//     public void setUser(String user) {
//       this.user = user;
//     }

//     public String getPassword() {
//       return pwd;
//     }

//     public void setPassword(String pwd) {
//       this.pwd = pwd;
//     }

//     public String getAlias() {
//       return pwda;
//     }

//     public void setAlias(String pwda) {
//       this.pwda = pwda;
//     }

//     public String getEnvVar() {
//       return pwde;
//     }

//     public void setEnvVar(String pwde) {
//       this.pwde = pwde;
//     }

//     public String getKeystore() {
//       return keystore;
//     }

//     public void setKeystore(String keystore) {
//       this.keystore = keystore;
//     }

//     public String getScriptFilePath() {
//       return scriptFilePath;
//     }

//     public void setScriptFilePath(String scriptFilePath) {
//       this.scriptFilePath = scriptFilePath;
//     }

//     public String getQuery() {
//       return query;
//     }

//     public void setQuery(String query) {
//       this.query = query;
//     }

//     public static boolean isDebug() {
//       return debug;
//     }

//     protected void validateArgs() throws SQLScriptExecutionException {
//       List<Integer> errors = new ArrayList<>(5);

//       if(null == connectionUrl) {
//         System.err.println("Connect URL is required: -c connectionUrl");
//         errors.add(SQLScriptExecutionException.NO_CONN_ERR);
//       }

//       if (null == user) {
//           System.err.println("User name is required: -u username");
//           errors.add(SQLScriptExecutionException.NO_USER_ERR);
//       }

//       if (null == pwd && null == pwda) {
//           System.err.println("Password or password alias is required: -p for password, -pa for password alias, -k for keystore");
//           errors.add(SQLScriptExecutionException.NO_PWD_ERR);
//       }

//       if (null == scriptFilePath && null == query) {
//           System.err.println("SQL Script file path or query is required: -s scriptFileName or -q SQLQuery");
//           errors.add(SQLScriptExecutionException.NO_QRY_ERR);
//       }

//       if (null == user || (null == pwd && null == pwda) || (null == scriptFilePath && null == query) || connectionUrl == null) {
//           throw new SQLScriptExecutionException(SQLScriptExecutionException.BAD_PAR_ERR, errors, "");
//       }
//     }

//     protected void parseArgs(String[] args) {
//       log.info("Parsing arguments");

//       Options ops = new Options();
//       ops.addOption("c", "connectstring", true, "Connect string");
//       ops.addOption("u", "user", true, "User name");
//       ops.addOption("p", "password", true, "Password");
//       ops.addOption("pa", "passwordAlias", true, "password alias");
//       ops.addOption("pe", "passwordEnvVar", true, "env var with keystore password");
//       ops.addOption("k", "keystore", true, "keystore location");
//       ops.addOption("sch", "schema", true, "SQL sequrity schema");
//       ops.addOption("s", "scriptFilePath", true, "SQL Script file path");
//       ops.addOption("q", "query", true, "SQL query");

//       CommandLineParser parser = new GnuParser();

//       CommandLine commandLine;
//       try {
//         commandLine = parser.parse(ops, args);
//       } catch (ParseException e) {
//         throw new SQLScriptExecutionException(SQLScriptExecutionException.BAD_PAR_ERR, "Can't parse command line", e);
//       }
//       args = commandLine.getArgs();

//       connectionUrl = commandLine.getOptionValue('c');
//       user = commandLine.getOptionValue('u');
//       pwd = commandLine.getOptionValue('p');
//       pwda = commandLine.getOptionValue("pa");
//       pwde = commandLine.getOptionValue("pe");
//       keystore = commandLine.getOptionValue("k");
//       scriptFilePath = commandLine.getOptionValue('s');
//       query = commandLine.getOptionValue('q');

//       log.info("The following options are passed:");
//       log.info("---------------------------------");
//       for(Option opt: commandLine.getOptions()) {
//         log.info(
//             opt.getOpt()
//           + ":" + opt.getDescription()
//           + ":" + ("p".equals(opt.getOpt())?"******":opt.getValue()));
//       }
//       log.info("---------------------------------");
//     }

//     public void execute() throws SQLScriptExecutionException {
//         log.info("Start Execution");
//         Properties connProps = fillProperties();
//         Reader reader = null;
//         try {
//             if (query == null) {
//                 File localfile = new File(scriptFilePath);
//                 if (localfile.isFile()) {
//                     reader = new BufferedReader(new FileReader(scriptFilePath));
//                 } else {
//                     FileSystem fs = FileSystem.get(new Configuration());
//                     Path inputPath = new Path(scriptFilePath);
//                     if (fs.exists(inputPath)) {
//                         reader = new BufferedReader(new InputStreamReader(fs.open(inputPath)));
//                     } else {
//                         System.err.println(String.format("Input file '%s' doesn't exist", scriptFilePath));
//                     }
//                 }
//             } else {
//                 reader = new BufferedReader(new StringReader(query));
//             }
//             createScriptRunner(connectionUrl, connProps).runScript(reader);
//         } catch (IOException | SQLException e) {
//             throw new SQLScriptExecutionException(SQLScriptExecutionException.RUN_SQL_ERR, "Can't execute the SQL", e);
//         }
//     }

//     protected ScriptRunner createScriptRunner(String connectionUrl, Properties connectionProperties) throws SQLScriptExecutionException {
//       log.info("Create script runner for:" + connectionUrl);
//       try {
//         return new ScriptRunner(connectionUrl, connectionProperties);
//       } catch (SQLException e) {
//         throw new SQLScriptExecutionException(SQLScriptExecutionException.RUN_SQL_ERR, "Can't create Script Runner", e);
//       }
//     }

//     protected Properties fillProperties() throws SQLScriptExecutionException {
//       log.info("Filling connection properties");
//       Properties connectionProperties = new Properties();

//       if (null != user) {
//           connectionProperties.put("user", user);
//       }

//       if (null != pwd) {
//           connectionProperties.put("password", pwd);
//       } else {
//           connectionProperties.put("password", pwdProvider.getPassword(keystore, pwda, pwde));
//       }

//       return connectionProperties;
//     }

//     public static void main(String[] args) {
//       try {
//         SQLScriptExecutor executor = new SQLScriptExecutor(new PasswordProvider());
//         executor.parseArgs(args);
//         executor.validateArgs();
//         executor.execute();
//       } catch(SQLScriptExecutionException e) {
//         if(debug) {
//           throw e;
//         } else {
//           e.printStackTrace();
//           System.exit(e.code);
//         }
//       }
//     }
// }

