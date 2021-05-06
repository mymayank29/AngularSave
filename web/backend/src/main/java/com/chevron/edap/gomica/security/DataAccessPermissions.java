package com.chevron.edap.gomica.security;



import java.io.Serializable;

public class DataAccessPermissions implements Serializable{



    private Operation operation;

    public DataAccessPermissions() {
    }

    public DataAccessPermissions(Operation operation) {

        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = (operation==null)?Operation.READ:operation;
    }




}
