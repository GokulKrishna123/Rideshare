package com.sourcey.materiallogindemo;

public class GoingModel {


    private String personId;
    private String personName;
    private String personFrom;
    private String personTo;
    private String personTime;
    private String personVia;
    private String personType;
    private String phone;

    public GoingModel() {

    }

    public GoingModel(String personId, String personName, String personFrom, String personTo, String personTime, String personVia, String personType,String Phone) {
        this.personId = personId;
        this.personName = personName;
        this.personFrom = personFrom;
        this.personTo = personTo;
        this.personTime = personTime;
        this.personVia = personVia;
        this.personType = personType;
        this.phone = Phone;
    }

    public String getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getPersonFrom() {
        return personFrom;
    }

    public String getPersonTo() {
        return personTo;
    }

    public String getPersonTime() {
        return personTime;
    }

    public String getPersonVia() {
        return personVia;
    }

    public String getPersonType() {
        return personType;
    }
    public String getPhone() {
        return phone;
    }
}