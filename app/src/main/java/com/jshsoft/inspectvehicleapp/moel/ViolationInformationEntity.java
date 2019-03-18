package com.jshsoft.inspectvehicleapp.moel;

public class ViolationInformationEntity {
    /**
     * 违章行为
     */
    private String violation;
    /**
     * 违章地址
     */
    private String violationSite;
    /**
     * 罚款
     */
    private String penalty;
    /**
     * 扣分
     */
    private String point;
    /**
     * 处理状态 0未处理/1已处理未缴款/2已缴款
     */
    private String type;

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public String getViolationSite() {
        return violationSite;
    }

    public void setViolationSite(String violationSite) {
        this.violationSite = violationSite;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ViolationInformationEntity{" +
                "violation='" + violation + '\'' +
                ", violationSite='" + violationSite + '\'' +
                ", penalty='" + penalty + '\'' +
                ", point='" + point + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
