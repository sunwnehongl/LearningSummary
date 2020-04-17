package com.sun.swh.collection;

import org.junit.Test;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


/**
 * @Auther: swh
 * @Date: 2020/4/17 22:51
 * @Description:
 */
public class SetTest {

    @Test
    public void testTreeSet() {

        Random random = new Random(47);
        Set<Integer> set = new TreeSet<>();
        for (int i = 0; i < 100; i++) {
            set.add(random.nextInt(30));
        }
        System.out.println(set);
    }

    @Test
    public void testTreeSetA() {
        Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        Collections.addAll(set,"aepidemic prevention and control lost".split(" "));
        System.out.println(set);
    }
}
