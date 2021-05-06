// package com.chevron.gomica.sql;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import java.io.IOException;
// import java.io.LineNumberReader;
// import java.io.Reader;
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.PreparedStatement;
// import java.sql.SQLException;
// import java.util.Properties;

// public class ScriptRunner {

//     private static final Logger log = LoggerFactory.getLogger(ScriptRunner.class);

//     private String connectionUrl;
//     private Properties connectionProperties;
//     private String delimiter = ";";

//     public ScriptRunner(String connectionUrl, Properties connectionProperties) throws SQLException {
//         this.connectionUrl = connectionUrl;
//         this.connectionProperties = connectionProperties;
//     }

//     /**
//      * Runs an SQL script (read in using the Reader parameter)
//      *
//      * @param reader - the source of the script
//      */
//     public void runScript(Reader reader) throws IOException, SQLException {

//         log.info("Connecting to the database:" + connectionUrl);
//         Connection connection = DriverManager.getConnection(connectionUrl, connectionProperties);

//         log.info("Got connection using driver:"
//             + connection.getMetaData().getDriverName() + ":"
//             + connection.getMetaData().getDriverVersion());

//         try {
//             connection.setAutoCommit(false);
//             runScript(connection, reader);
//         } finally {
//             log.info("Closing connection:" + connectionUrl);
//             connection.close();
//         }
//     }

//     /**
//      * Runs an SQL script (read it using the Reader parameter) using the
//      * connection passed in
//      *
//      * @param connection - the connection to use for the script
//      * @param reader     - the source of the script
//      * @throws SQLException if any SQL errors occur
//      * @throws IOException  if there is an error reading from the Reader
//      */
//     private void runScript(Connection connection, Reader reader) throws IOException, SQLException {
//         StringBuilder command = null;

//         try {
//             LineNumberReader lineReader = new LineNumberReader(reader);
//             String line = null;

//             while ((line = lineReader.readLine()) != null) {
//                 if (command == null) {
//                     command = new StringBuilder();
//                 }

//                 String trimmedLine = line.trim();

//                 if (trimmedLine.startsWith("--")) {
//                     // System.out.println(trimmedLine); <-- WHY THEN YOU NEED THIS BLOCK?
//                 } else if (trimmedLine.length() < 1 || trimmedLine.startsWith("//") || trimmedLine.startsWith("--")) { // <-- trimmedLine.startsWith("--") never happens
//                     // Do nothing if line is comment <-- WHY THEN YOU NEED THIS BLOCK?
//                 } else if (trimmedLine.endsWith(getDelimiter()) || trimmedLine.equals(getDelimiter())) {
//                     command.append(line.substring(0, line.lastIndexOf(getDelimiter())));
//                     command.append("\n");

//                     executeStatement(connection, command.toString());

//                     command = null;
//                 } else {
//                     command.append(line);
//                     command.append("\n");
//                 }
//             }

//             if (null != command && !command.toString().equals("")) {
//               executeStatement(connection, command.toString());
//             }

//             log.info("Commiting the transaction");
//             connection.commit();
//         } catch (SQLException e) {
//             log.info("Rolling back the transaction");
//             connection.rollback();

//             throw new SQLException("Failed to execute statement:" + command, e);
//         }
//     }

//     protected void executeStatement(Connection connection, String command) throws SQLException {
//       log.info("Prepare statement:" + command.toString());
//       PreparedStatement statement = connection.prepareStatement(command);

//       try {
//           log.info("Execute statement:" + command);
//           statement.execute();
//           log.info("" + statement.getUpdateCount() + " rows affected");
//       } finally {
//           statement.close();
//       }
//     }

//     private String getDelimiter() {
//         return delimiter;
//     }

//     public void setDelimiter(String delimiter) {
//         this.delimiter = delimiter;
//     }
// }

