package com.jshsoft.inspectvehicleapp.moel;

/**
 * SmartTable 专用类，标签眉
 */
public class Item {
    private String id;
    private String project;
    private String type;
    private String particulars;
    private Boolean operation;//用于记录是否被选中的状态

    public Item() {
    }

    public Item(String id, String project, String type, String particulars, Boolean operation) {
        this.id = id;
        this.project = project;
        this.type = type;
        this.particulars = particulars;
        this.operation = operation;
    }

    public Boolean getOperation() {
        return operation;
    }

    public void setOperation(Boolean operation) {
        this.operation = operation;
    }





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", project='" + project + '\'' +
                ", type='" + type + '\'' +
                ", particulars='" + particulars + '\'' +
                ", operation=" + operation +
                '}';
    }
}
