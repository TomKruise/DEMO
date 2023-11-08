package com.tom.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import scala.Tuple2;

import java.util.List;

public class MyRecommend2 {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("MyRecommend").setMaster("local[*]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        jsc.setLogLevel("WARN");

        JavaRDD<String> sourceJavaRDD = jsc.textFile("spark-test/ml-100k/u.data");
        JavaRDD<String[]> lineJavaRDD = sourceJavaRDD.map(v -> v.split("\t"));
        ////装载样本评分数据，其中最后一列Timestamp取除10的余数作为key，Rating为值，即(Int，Rating)
        JavaPairRDD<Integer, Rating> ratings = lineJavaRDD.mapToPair(v -> {
            Rating rating = new Rating(Integer.valueOf(v[0]), Integer.valueOf(v[1]), Double.valueOf(v[2]));
            Integer key = Integer.valueOf(v[3]) % 10;
            return new Tuple2<Integer, Rating>(key, rating);
        });

        //装载电影目录对照表(电影ID->电影标题)
        List<Tuple2> movies = jsc.textFile("spark-test/ml-100k/u.item").map(v -> {
            String[] ss = v.split("\\|");
            return new Tuple2(ss[0], ss[1]);
        }).collect();

        jsc.stop();
    }
}
