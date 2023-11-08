package com.tom.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;

public class MyRecommend {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("MyRecommend").setMaster("local[*]");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        jsc.setLogLevel("WARN");

        JavaRDD<String> sourceJavaRDD = jsc.textFile("spark-test/ml-100k/u.data");
        JavaRDD<String[]> lineJavaRDD = sourceJavaRDD.map(v -> v.split("\t"));
        // 转化得到Rating对象集
        JavaRDD<Rating> ratingJavaRDD = lineJavaRDD.map(v -> new Rating(Integer.valueOf(v[0]), Integer.valueOf(v[1]), Double.valueOf(v[2])));

        // 训练模型
        MatrixFactorizationModel model = ALS.train(ratingJavaRDD.rdd(), 8, 10);

        //商品推荐
        Rating[] recommendProducts = model.recommendProducts(301, 10);

        for (Rating product : recommendProducts) {
            System.out.println("userId = "+ product.user() +", product = " + product.product());
        }

        jsc.stop();
    }
}
