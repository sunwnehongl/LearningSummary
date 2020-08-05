package com.sun.swh.collection;

import org.junit.Test;

import javax.security.auth.Policy;
import java.util.*;


/**
 * @Auther: swh
 * @Date: 2020/4/17 22:51
 * @Description:
 */
public class SetTest {

    public void SetTest() {


    }

    public void SetTest(String a) {

    }

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

    @Test
    public void test() {
        Integer b = 15000;
        System.out.println(b == 15000);

    }

    private <T> void print(T t) {
        System.out.println(t);
    }
}
