package com.chevron.gomica.sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class SQLScriptExecutionException extends RuntimeException {
  public final static int NO_CONN_ERR = -4;
  public final static int NO_USER_ERR = -5;
  public final static int NO_PWD_ERR = -6;
  public final static int NO_QRY_ERR = -7;
  public final static int NO_SCRIPT_ERR = -8;
  public final static int READ_PWD_ERR = -9;
  public final static int RUN_SQL_ERR = -9;
  public final static int BAD_PAR_ERR = -100500;

  final int code;
  private List<Integer> errors = new ArrayList<>();

  public SQLScriptExecutionException(int code, List<Integer> errors, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
    this.errors = errors;
  }

  public SQLScriptExecutionException(int code, List<Integer> errors, String message) {
    this(code, errors, message, null);
  }

  public SQLScriptExecutionException(int code, String message) {
    this(code, null, message, null);
  }

  public SQLScriptExecutionException(int code, String message, Throwable cause) {
    this(code, null, message, cause);
  }

  public int getCode() throws IOException, ClassNotFoundException {
    return code;
  }

  public List<Integer> getErrors() {
    return errors;
  }

  @Override
  public String getMessage() {
    String msg = super.getMessage();
    if(errors != null && errors.size() > 0) {
      msg += ("the nested errors are:" + errors);
    }
    return msg;
  }
}