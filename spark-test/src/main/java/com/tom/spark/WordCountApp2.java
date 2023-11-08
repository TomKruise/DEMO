package com.tom.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WordCountApp2 {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setAppName("WordCountApp2")
                .setMaster("local[*]");//本地模式，使用和CPU内核数相同的线程数进行执行
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        JavaRDD<String> rdd = jsc.textFile("/Users/Tom/Documents/Developer/IdeaProjects/DEMO/spark-test/src/main/resources/data.txt");

        //压扁操作，按照空格分割
        JavaRDD<String> flatMap = rdd.flatMap(s -> {
            String[] strings = s.split(" ");
            return Arrays.asList(strings).iterator();
        });

        //对单词做计数
        JavaPairRDD<Object, Integer> mapToPair = flatMap.mapToPair(s -> new Tuple2<>(s, 1));

        //对相同的单词做相加操作
        JavaPairRDD<Object, Integer> reduceByKey = mapToPair.reduceByKey((n1, n2) -> n1+n2);

        //执行计算
        List<Tuple2<Object, Integer>> collect = reduceByKey.collect();

        collect.forEach(tuple2 -> System.out.println(tuple2._1() + ": " + tuple2._2()));

        jsc.stop();
    }
}
