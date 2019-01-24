package com.harri.java.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.harri.java.task.models.Brand;
import com.harri.java.task.models.OptimalUtilization;
import com.harri.java.task.models.Repository;
import com.harri.java.task.utils.ParseUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class OptimalFinder {

    private static String BRAND_INPUT_FILE = null;
    private static String REPOS_INPUT_FILE = null;
    private static String RESULT_FILE = null;

    private static Map<Integer, Brand> brandsMap = new HashMap<Integer, Brand>();
    private static ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) {

        // Validate argument
        if (args == null || args.length != 3) {
            System.out.println("Please Enter a valid args");
            System.out.println("a valid args will be like: brand_path repo_path output_path");
            return;
        } else {
            BRAND_INPUT_FILE = args[0];
            REPOS_INPUT_FILE = args[1];
            RESULT_FILE = args[2];

            if (Files.notExists(Paths.get(BRAND_INPUT_FILE))) {
                System.out.println("Please Enter a valid path for brand file");
                return;
            }
            if (Files.notExists(Paths.get(REPOS_INPUT_FILE))) {
                System.out.println("Please Enter a valid path for repository file");
                return;
            }
        }

        // Read the two files
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(BRAND_INPUT_FILE));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!"".equals(line.trim())) {
                    Brand brand = ParseUtil.parseBrand(line);

                    brandsMap.put(brand.getId(), brand);
                }
            }

            reader = new BufferedReader(new FileReader(REPOS_INPUT_FILE));
            while ((line = reader.readLine()) != null) {
                if (!"".equals(line.trim())) {

                    Repository repository = ParseUtil.parseRepo(line);

                    Brand brand = brandsMap.get(repository.getBrandId());

                    if (brand != null) {
                        if (repository.isSource()) {
                            if (brand.getSourceRepos() == null) {
                                List<Repository> repos = new ArrayList<Repository>();
                                repos.add(repository);
                                brand.setSourceRepos(repos);
                            } else {
                                brand.getSourceRepos().add(repository);
                            }
                        } else {
                            if (brand.getDestRepos() == null) {
                                List<Repository> repos = new ArrayList<Repository>();
                                repos.add(repository);
                                brand.setDestRepos(repos);
                            } else {
                                brand.getDestRepos().add(repository);
                            }
                        }

                    /*if (brand.getRepositories() == null) {
                        List<Repository> repos = new ArrayList<Repository>();
                        repos.add(repository);
                        brand.setRepositories(repos);
                    } else {
                        brand.getRepositories().add(repository);
                    }*/

                    }
                }
            }

            List<OptimalUtilization> result = new ArrayList<OptimalUtilization>();

            for (Brand brand :
                    brandsMap.values()) {
                result.addAll(getOptimalUtilization_OptimalSolution(brand));
            }

            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            System.out.println(mapper.writeValueAsString(result));

            printResult(result, RESULT_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static List<OptimalUtilization> getOptimalUtilization(Brand brand) {
        List<Repository> sources = brand.getSourceRepos();
        List<Repository> destinations = brand.getDestRepos();

        Integer maxUtilization = brand.getMaxUtilization();
        Integer optimal = 0;
        List<OptimalUtilization> result = new ArrayList<OptimalUtilization>();

        for (Repository source : sources) {
            for (Repository destination : destinations) {
                Integer totalPayload = source.getPayload() + destination.getPayload();
                if (totalPayload <= maxUtilization) {
                    if (totalPayload > optimal) {
                        optimal = totalPayload;
                        result.clear();
                        addToOptimal(brand, result, source, destination);
                    } else if (totalPayload.equals(optimal)) {
                        addToOptimal(brand, result, source, destination);
                    }
                }
            }
        }

        return result;
    }

    private static void addToOptimal(Brand b, List<OptimalUtilization> result, Repository source, Repository dest) {
        OptimalUtilization ou = new OptimalUtilization();
        ou.setBrandName(b.getName());
        ou.setSourceRepoId(source.getId());
        ou.setDestRepoId(dest.getId());
        result.add(ou);
    }


    private static void printResult(List<OptimalUtilization> result, String outputFilePath) {
        try (PrintWriter writer = new PrintWriter(outputFilePath)) {
            for (OptimalUtilization ou :
                    result) {
                writer.printf("%s,%s,%s\n", ou.getBrandName(), ou.getSourceRepoId(), ou.getDestRepoId());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static List<OptimalUtilization> getOptimalUtilization_OptimalSolution(Brand brand) throws JsonProcessingException {
        Collections.sort(brand.getSourceRepos());
        List<Repository> sources = brand.getSourceRepos();
        Collections.sort(brand.getDestRepos());
        List<Repository> destinations = brand.getDestRepos();

        Integer maxUtilization = brand.getMaxUtilization();
        Integer optimal = 0;
        List<OptimalUtilization> result = new ArrayList<OptimalUtilization>();

        int l = 0;
        int r = destinations.size() - 1;

        while (l != sources.size() && r != -1) {
            int totalLoad = sources.get(l).getPayload() + destinations.get(r).getPayload();
            if (totalLoad <= maxUtilization) {
                if (totalLoad > optimal) {
                    optimal = totalLoad;
                    result.clear();
                    addToOptimal(brand, result, sources.get(l), destinations.get(r));
                } else if (totalLoad == optimal) {
                    addToOptimal(brand, result, sources.get(l), destinations.get(r));
                }
            }

            if (totalLoad > maxUtilization)
                r--;
            else // Move to larger values
                l++;
        }

        System.out.println(mapper.writeValueAsString(result));

        System.out.println(l + ":" + r);

        return result;
    }


}
