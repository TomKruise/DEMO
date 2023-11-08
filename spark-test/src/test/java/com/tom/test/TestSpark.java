package com.tom.test;


import io.github.tomkruise.util.GsonUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.junit.Test;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSpark {
    @Test
    public void testMap(){
        JavaSparkContext jsc = getJavaSparkContext();
        JavaRDD<Integer> rdd1 = jsc.parallelize(Arrays.asList(1,2,3,4,5));

        JavaRDD<Integer> rdd2 = rdd1.map((Function<Integer, Integer>) integer -> integer * 3);

        List<Integer> list = rdd2.collect();
        list.stream().forEach(System.out::println);

        jsc.stop();
    }

    @Test
    public void testFilter(){
        JavaSparkContext jsc = getJavaSparkContext();
        JavaRDD<Integer> rdd1 = jsc.parallelize(Arrays.asList(1,2,3,4,5));

        JavaRDD<Integer> rdd2 = rdd1.filter(v1 -> v1 > 2);

        List<Integer> list = rdd2.collect();
        list.stream().forEach(System.out::println);
        jsc.stop();
    }

    @Test
    public void testFlatMap(){
        JavaSparkContext jsc = getJavaSparkContext();
        JavaRDD<String> rdd1 = jsc.parallelize(Arrays.asList("Hello world","Hello spark","Tom and Jerry"));

        JavaRDD<String> rdd2 = rdd1.flatMap(s -> Arrays.asList(s.split(" ")).iterator());

        List<String> list = rdd2.collect();
        list.stream().forEach(System.out::println);
        jsc.stop();
    }

    @Test
    public void testMapPartitions(){
        JavaSparkContext jsc = getJavaSparkContext();
        JavaRDD<Integer> rdd1 = jsc.parallelize(Arrays.asList(1,2,3,4,5,6,7,8,9,10),3);

        JavaRDD<Integer> rdd2 = rdd1.mapPartitions(integerIterator -> {
            List<Integer> result = new ArrayList<>();
            while (integerIterator.hasNext()) {
                Integer i = integerIterator.next();
                result.add(i);
            }
            System.out.println("Partitions: " + result);
            return result.iterator();
        });

        List<Integer> list = rdd2.collect();
        list.stream().forEach(System.out::println);
        jsc.stop();
    }

    @Test
    public void testReduceByKey(){
        JavaSparkContext jsc = getJavaSparkContext();
        JavaRDD<String> rdd1 = jsc.parallelize(Arrays.asList("Hello world","Hello spark","Tom and Jerry"));

        //按照空格分割
        JavaRDD<String> rdd2 = rdd1.flatMap(s -> Arrays.asList(s.split(" ")).iterator());
        //把每个单词的出现数量标记为1
        JavaPairRDD<String, Integer> pairRDD = rdd2.mapToPair(s -> new Tuple2<>(s, 1));
        //按照key相同的数进行相加操作
        JavaPairRDD<String, Integer> pairRDD1 = pairRDD.reduceByKey((v1, v2) -> v1 + v2);

        List<Tuple2<String, Integer>> list = pairRDD1.collect();
        list.stream().forEach(System.out::println);
        jsc.stop();
    }

    @Test
    public void testCoalesce(){
        JavaSparkContext jsc = getJavaSparkContext();
        JavaRDD<Integer> rdd1 = jsc.parallelize(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        System.out.println(rdd1.getNumPartitions());

        //重新分区，shuffle：是否重新分配存储，如果分区数大于原有分区数，就需要设置为true
        JavaRDD<Integer> rdd2 = rdd1.coalesce(2,false);
        System.out.println(rdd2.getNumPartitions());
        List<Integer> list = rdd2.collect();
        list.stream().forEach(System.out::println);

        jsc.stop();
    }

    private JavaSparkContext getJavaSparkContext() {
        SparkConf sparkConf = new SparkConf()
                .setAppName("WordCountApp")
                .setMaster("local[*]");//本地模式，使用和CPU内核数相同的线程数进行执行
        return new JavaSparkContext(sparkConf);
    }
}
