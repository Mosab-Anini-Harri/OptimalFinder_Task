package com.harri.java.task.models;

import java.util.List;

public class Brand {

    private Integer id;
    private String name;
    private Integer maxUtilization;

    private List<Repository> repositories;

    private List<Repository> sourceRepos;
    private List<Repository> destRepos;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxUtilization() {
        return maxUtilization;
    }

    public void setMaxUtilization(Integer maxUtilization) {
        this.maxUtilization = maxUtilization;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }

    public List<Repository> getSourceRepos() {
        return sourceRepos;
    }

    public void setSourceRepos(List<Repository> sourceRepos) {
        this.sourceRepos = sourceRepos;
    }

    public List<Repository> getDestRepos() {
        return destRepos;
    }

    public void setDestRepos(List<Repository> destRepos) {
        this.destRepos = destRepos;
    }
}
