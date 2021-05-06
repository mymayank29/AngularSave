package com.chevron.gomica.sql;

import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SQLScriptRunner {

    private PasswordProvider pwdProvider;
    private String connectionUrl;
    private String user = null;
    private String pwd = null;
    private String pwda = null;
    private String pwde = null;
    private String keystore = null;
    private String scriptFilePath = null;
    private String DELIMITER = ";";

    public SQLScriptRunner(PasswordProvider pwdProvider) {
        super();
        this.pwdProvider = pwdProvider;
    }

    public static void main(String[] args) {
        try {
            SQLScriptRunner executor = new SQLScriptRunner(new PasswordProvider());
            executor.parseArgs(args);
            executor.validateArgs();
            executor.execute();
        } catch(SQLScriptExecutionException e) {
                e.printStackTrace();
                System.exit(e.code);
        }
    }

    public void execute() throws SQLScriptExecutionException {

        Properties connProps = fillProperties();
        BufferedReader reader = null;
        FileSystem fs = null;
        Connection connection = null;

        try {
            try {
                File localfile = new File(scriptFilePath);
                if (localfile.isFile()) {
                    reader = new BufferedReader(new FileReader(scriptFilePath));
                } else {
                    Configuration conf = new Configuration();
                    conf.setBoolean("fs.hdfs.impl.disable.cache", true);
                    fs = FileSystem.get(conf);
                    Path inputPath = new Path(scriptFilePath);
                    if (fs.exists(inputPath)) {
                        reader = new BufferedReader(new InputStreamReader(fs.open(inputPath)));
                    } else {
//                        System.err.println(String.format("Input file '%s' doesn't exist", scriptFilePath));
                        throw new IllegalArgumentException("Input file" + scriptFilePath + " doesn't exist");
                    }
                }

                connection = DriverManager.getConnection(connectionUrl, connProps);
                StringBuilder command = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    if (command == null) command = new StringBuilder();
                    command.append(line.trim());
                    command.append("\n");
                    if (line.trim().endsWith(DELIMITER)) {
                        if (command.length() > 0) {
                            PreparedStatement statement = connection.prepareStatement(escapeSQLString(command.toString()));
                            try {
                                statement.execute();
                            } catch (SQLException sqle) {
                                sqle.printStackTrace();
                            } finally {
                                statement.close();
                            }
                        }
                        command = null;
                    }
                }
                connection.commit();
            } catch (Exception e) {
                if(connection != null) {
                    connection.rollback();
                }
            } finally {
                if(connection != null) {
                    connection.close();
                }
                if(reader != null) {
                    reader.close();
                }
                if (fs != null) {
                    fs.close();
                }
            }
        } catch (IOException | SQLException e) {
            throw new SQLScriptExecutionException(SQLScriptExecutionException.RUN_SQL_ERR, "Can't execute the SQL", e);
        }
    }

    protected Properties fillProperties() throws SQLScriptExecutionException {
        Properties connectionProperties = new Properties();
        if (null != user) {
            connectionProperties.put("user", user);
        }
        if (null != pwd) {
            connectionProperties.put("password", pwd);
        } else {
            connectionProperties.put("password", pwdProvider.getPassword(keystore, pwda, pwde));
        }
        return connectionProperties;
    }

    protected void validateArgs() throws SQLScriptExecutionException {
        List<Integer> errors = new ArrayList<>(5);

        if(null == connectionUrl) {
            System.err.println("Connect URL is required: -c connectionUrl");
            errors.add(SQLScriptExecutionException.NO_CONN_ERR);
        }

        if (null == user) {
            System.err.println("User name is required: -u username");
            errors.add(SQLScriptExecutionException.NO_USER_ERR);
        }

        if (null == pwd && null == pwda) {
            System.err.println("Password or password alias is required: -p for password, -pa for password alias, -k for keystore");
            errors.add(SQLScriptExecutionException.NO_PWD_ERR);
        }

        if (null == scriptFilePath) {
            System.err.println("SQL Script file path or query is required: -s scriptFileName or -q SQLQuery");
            errors.add(SQLScriptExecutionException.NO_QRY_ERR);
        }

        if (null == user || (null == pwd && null == pwda) || null == scriptFilePath || connectionUrl == null) {
            throw new SQLScriptExecutionException(SQLScriptExecutionException.BAD_PAR_ERR, errors, "");
        }
    }

    protected void parseArgs(String[] args) {

        Options ops = new Options();
        ops.addOption("c", "connectstring", true, "Connect string");
        ops.addOption("u", "user", true, "User name");
        ops.addOption("p", "password", true, "Password");
        ops.addOption("pa", "passwordAlias", true, "password alias");
        ops.addOption("pe", "passwordEnvVar", true, "env var with keystore password");
        ops.addOption("k", "keystore", true, "keystore location");
        ops.addOption("sch", "schema", true, "SQL sequrity schema");
        ops.addOption("s", "scriptFilePath", true, "SQL Script file path");
        ops.addOption("q", "query", true, "SQL query");

        CommandLineParser parser = new GnuParser();

        CommandLine commandLine;
        try {
            commandLine = parser.parse(ops, args);
        } catch (ParseException e) {
            throw new SQLScriptExecutionException(SQLScriptExecutionException.BAD_PAR_ERR, "Can't parse command line", e);
        }

        connectionUrl = commandLine.getOptionValue('c');
        user = commandLine.getOptionValue('u');
        pwd = commandLine.getOptionValue('p');
        pwda = commandLine.getOptionValue("pa");
        pwde = commandLine.getOptionValue("pe");
        keystore = commandLine.getOptionValue("k");
        scriptFilePath = commandLine.getOptionValue('s');
    }

    private String escapeSQLString(String s) {
        return s.replaceAll("%", "\\%")
                .replaceAll("'", "\\'")
                .replaceAll("like", "")
                .replaceAll("LIKE", "");
    }

//    private String escapeWildcardsForMySQL(String s) {
//        return escapeStringForMySQL(s)
//                .replaceAll("%", "\\%")
//                .replaceAll("_","\\_");
//    }

}
