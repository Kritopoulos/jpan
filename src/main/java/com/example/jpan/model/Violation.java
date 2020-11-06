package com.example.jpan.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class Violation {

    private String priority;
    private String description;
    private String begin_line;
    private String end_line;
    private String begin_column;
    private String end_column;
    private String rule;
    private String rule_set;
    private String eClass;
    private String method;

    @Override
    public String toString() {
        return "Violation{" +
                "priority='" + priority + '\'' +
                ", description='" + description + '\'' +
                ", begin_line='" + begin_line + '\'' +
                ", end_line='" + end_line + '\'' +
                ", begin_column='" + begin_column + '\'' +
                ", end_column='" + end_column + '\'' +
                ", rule='" + rule + '\'' +
                ", rule_set='" + rule_set + '\'' +
                ", eClass='" + eClass + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
//    public Violation(String priority, String description, String begin_line,
//                     String end_line, String begin_column, String end_column, String rule,
//                     String rule_set, String eClass, String method) {
//        this.priority = priority;
//        this.description = description;
//        this.begin_line = begin_line;
//        this.end_line = end_line;
//        this.begin_column = begin_column;
//        this.end_column = end_column;
//        this.rule = rule;
//        this.rule_set = rule_set;
//        this.eClass = eClass;
//        this.method = method;
//    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBegin_line() {
        return begin_line;
    }

    public void setBegin_line(String begin_line) {
        this.begin_line = begin_line;
    }

    public String getEnd_line() {
        return end_line;
    }

    public void setEnd_line(String end_line) {
        this.end_line = end_line;
    }

    public String getBegin_column() {
        return begin_column;
    }

    public void setBegin_column(String begin_column) {
        this.begin_column = begin_column;
    }

    public String getEnd_column() {
        return end_column;
    }

    public void setEnd_column(String end_column) {
        this.end_column = end_column;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRule_set() {
        return rule_set;
    }

    public void setRule_set(String rule_set) {
        this.rule_set = rule_set;
    }

    public String geteClass() {
        return eClass;
    }

    public void seteClass(String eClass) {
        this.eClass = eClass;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setViolation(String priority, String description,
            String begin_line, String end_line, String begin_column,
            String end_column, String rule, String rule_set,
            String eClass, String method){
            this.priority = priority;
            this.description = description;
            this.begin_line = begin_line;
            this.end_line = end_line;
            this.begin_column = begin_column;
            this.end_column = end_column;
            this.rule = rule;
            this.rule_set=rule_set;
            this.eClass = eClass;
            this.method = method;

    }
}
