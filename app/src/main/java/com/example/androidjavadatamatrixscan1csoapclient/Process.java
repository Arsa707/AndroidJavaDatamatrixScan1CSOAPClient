package com.example.androidjavadatamatrixscan1csoapclient;

public class Process {
    private String number;
    private String date;
    private String fullDate;
    private String block;
    private int position;
    private String processID;
    private String organization;
    private String contragent;
    private String docDate;
    private String docNumber;

    public String getFullDate() {
        return fullDate;
    }
    public String getDate() {
        return date;
    }
    public void setBlock(boolean block) {
        this.block = block == true ? "✔" : "";
    }

    public String getProcessID() {
        return processID;
    }

    public String getOrganization() {
        return organization;
    }

    public String getContragent() {
        return contragent;
    }

    public String getNumber() {

        return number;
    }


    public String getBlock() {
        return block;
    }

    public String getDocDate() {
        return docDate;
    }

    public String getDocNumber() {
        return docNumber;
    }

    Process(String number, String date, String block, int position, String organization,
            String contragent, String docDate, String docNumber, String processID) {
        this.number = number;
        this.date = date.length() > 10 ? date.substring(0,10) : date;
        this.fullDate = date;
        this.block = block.equals("true") || block.equals("✔") ? "✔" : "";
        this.position = position;
        this.organization = organization;
        this.contragent = contragent;
        this.docDate = docDate.length() > 10 ? docDate.substring(0,10) : docDate;
        this.docNumber = docNumber;
        this.processID = processID;
    }
}