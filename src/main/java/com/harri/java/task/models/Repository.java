package com.harri.java.task.models;

public class Repository implements Comparable<Repository> {

    private Integer id;
    private Integer brandId;
    private Boolean isSource;
    private Integer payload;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Boolean isSource() {
        return isSource;
    }

    public void setSource(Boolean source) {
        isSource = source;
    }

    public Integer getPayload() {
        return payload;
    }

    public void setPayload(Integer payload) {
        this.payload = payload;
    }

    @Override
    public int compareTo(Repository repository) {
        return this.payload - repository.payload;
    }
}
