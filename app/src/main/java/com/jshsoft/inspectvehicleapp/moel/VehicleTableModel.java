package com.jshsoft.inspectvehicleapp.moel;

public class VehicleTableModel {
    private String id;
    private String name;
    private String value;
    private String remarks;
    private Boolean operation;//用于记录是否被选中的状态

    public VehicleTableModel(String id, String name, String value, String remarks, Boolean operation) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.remarks = remarks;
        this.operation = operation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "VehicleTableModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", remarks='" + remarks + '\'' +
                ", operation=" + operation +
                '}';
    }
}
