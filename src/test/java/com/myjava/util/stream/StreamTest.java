package com.myjava.util.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StreamTest {

    public static void main(String[] args) throws Exception {
        List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8));

        list.parallelStream().forEach(e -> System.out.println(e + ""));
        System.out.println("pause for 5 min...");
        Thread.sleep(5000);
        list.stream().parallel().forEach(e -> System.out.println(e + ""));
    }

}
