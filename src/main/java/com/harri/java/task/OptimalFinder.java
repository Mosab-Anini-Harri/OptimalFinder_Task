package com.harri.java.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.harri.java.task.models.Brand;
import com.harri.java.task.models.OptimalUtilization;
import com.harri.java.task.models.Repository;
import com.harri.java.task.utils.ParseUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptimalFinder {

    private static String BRAND_INPUT_FILE = null;
    private static String REPOS_INPUT_FILE = null;
    private static String RESULT_FILE = null;

    private static Map<Integer, Brand> brandsMap = new HashMap<Integer, Brand>();
    private static ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) {

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

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(BRAND_INPUT_FILE));
            String line = null;
            while ((line = reader.readLine()) != null && !"".equals(line.trim())) {
                Brand b = ParseUtil.parseBrand(line);

                brandsMap.put(b.getId(), b);
            }

            reader = new BufferedReader(new FileReader(REPOS_INPUT_FILE));
            while ((line = reader.readLine()) != null && !"".equals(line.trim())) {
                Repository r = ParseUtil.parseRepo(line);

                Brand b = brandsMap.get(r.getBrandId());

                if (b != null) {
                    if (r.isSource()) {
                        if (b.getSourceRepos() == null) {
                            List<Repository> repos = new ArrayList<Repository>();
                            repos.add(r);
                            b.setSourceRepos(repos);
                        } else {
                            b.getSourceRepos().add(r);
                        }
                    } else {
                        if (b.getDestRepos() == null) {
                            List<Repository> repos = new ArrayList<Repository>();
                            repos.add(r);
                            b.setDestRepos(repos);
                        } else {
                            b.getDestRepos().add(r);
                        }
                    }
                }
            }

            List<OptimalUtilization> result = new ArrayList<OptimalUtilization>();

            for (Brand brand :
                    brandsMap.values()) {
                result.addAll(getOptimalUtilization(brand));
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

    private static List<OptimalUtilization> getOptimalUtilization(Brand b) {
        List<Repository> sources = b.getSourceRepos();
        List<Repository> destinations = b.getDestRepos();

        Integer maxUtilization = b.getMaxUtilization();
        Integer optimal = 0;
        List<OptimalUtilization> result = new ArrayList<OptimalUtilization>();

        for (Repository source :
                sources) {
            for (Repository dest :
                    destinations) {
                Integer totalPayload = source.getPayload() + dest.getPayload();
                if (totalPayload <= maxUtilization) {
                    if (totalPayload > optimal) {
                        optimal = totalPayload;
                        result.clear();
                        addToOptimal(b, result, source, dest);
                    } else if (totalPayload.equals(optimal)) {
                        addToOptimal(b, result, source, dest);
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
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFilePath);
            for (OptimalUtilization ou :
                    result) {
                writer.printf("%s,%s,%s\n", ou.getBrandName(), ou.getSourceRepoId(), ou.getDestRepoId());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }


}
