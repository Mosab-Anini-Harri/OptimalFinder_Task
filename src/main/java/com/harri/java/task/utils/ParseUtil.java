package com.harri.java.task.utils;

import com.harri.java.task.models.Brand;
import com.harri.java.task.models.Repository;

public class ParseUtil {
    public static Brand parseBrand(String line) {
        String[] props = line.split(",");
        Brand b = new Brand();

        b.setId(Integer.valueOf(props[0]));
        b.setName(props[1]);
        b.setMaxUtilization(Integer.valueOf(props[2]));
        return b;
    }

    public static Repository parseRepo(String line) {
        String[] props = line.split(",");
        Repository r = new Repository();
        r.setBrandId(Integer.valueOf(props[0]));
        r.setSource(parseSourceDestination(props[1]));
        r.setId(Integer.valueOf(props[2]));
        r.setPayload(Integer.valueOf(props[3]));
        return r;
    }

    private static Boolean parseSourceDestination(String val) {
        return "s".equalsIgnoreCase(val);
    }
}
