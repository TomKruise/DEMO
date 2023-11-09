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

        //统计有用户数量和电影数量以及用户对电影的评分数目
        long numUsers = ratings.map(v -> v._2().user()).distinct().count();
        long numRatings = ratings.count();
        long numMovies = ratings.map(v1 -> (v1._2()).product()).distinct().count();

        System.out.println("User: " + numUsers + "; Movie: " + numMovies + "; Rating: " + numRatings);

        //将样本评分表以key值切分成3个部分，分别用于训练（60%，并加入用户评分），校验（20%），and测试（20%）
        //key: 0~9 60% -> <6 20% -> 6<=x<8 20% -> x>=8

        Integer numPartitions = 4;//分区数

        //训练集
        JavaRDD<Rating> training = ratings.filter(v -> v._1() < 6)
                .values()
                .repartition(numPartitions)
                .cache();

        //校验集
        JavaRDD<Rating> validation = ratings.filter(v -> v._1() >= 6 && v._1() < 8)
                .values()
                .repartition(numPartitions)
                .cache();

        //测试集
        JavaRDD<Rating> test = ratings.filter(v -> v._1() >= 8)
                .values()
                .repartition(numPartitions)
                .cache();

        long numTraining = training.count();
        long numValidation = validation.count();
        long numTest = test.count();

        System.out.println("Training: " + numTraining + "; Validation: " + numValidation + "; Test: " + numTest);

        int[] ranks = new int[]{10, 11, 12};
        double[] lambdas = new double[]{0.01, 0.03, 0.1, 0.3, 1, 3};
        int[] numIters = new int[]{8, 9, 10, 11, 12, 13, 14, 15};

        MatrixFactorizationModel bestModel = null;
        double bestValidationRmse = Double.MAX_VALUE;
        int bestRank = 0;
        double bestLambda = -0.01;
        int bestNumIter = 0;

        //对上面的3个参数做循环，这里使用三层嵌套循环来测试
        for (int rank : ranks) {
            for (int numIter : numIters) {
                for (double lambda : lambdas) {
                    MatrixFactorizationModel model = ALS.train(training.rdd(), rank, numIter, lambda);
                    Double rmse = computeRmse(model, validation, numValidation);
                    System.out.println("RMSE(校验集): " + rmse + ", rank = " + rank + ", lambda = " + lambda + ", numIter = " + numIter);
                    if (rmse < bestValidationRmse) {
                        bestModel = model;
                        bestRank = rank;
                        bestLambda = lambda;
                        bestNumIter = numIter;
                        bestValidationRmse = rmse;
                    }
                }

            }

        }

        Double testRmse = computeRmse(bestModel, test, numTest);
        System.out.println("测试数据集在最佳训练模型 rank=" + bestRank + ", lambda=" + bestLambda + ", numIter=" + bestNumIter + ", rmse=" + testRmse);

        //计算均值
        Double meanRating = training.union(validation).mapToDouble(v -> v.rating()).mean();

        //计算标准误差值
        double baselineRmse = Math.sqrt(test.map(v -> (meanRating - v.rating()) * (meanRating - v.rating())).reduce((v1, v2) -> (v1 + v2) / numTest));

        //计算准确率提升了多少
        double improvement = (baselineRmse - testRmse) / baselineRmse * 100;

        System.out.println("最佳训练模型的准确率提升了: " + String.format("%.2f", improvement) + "%.");

        //构建最佳训练模型
        MatrixFactorizationModel model = ALS.train(ratings.values().rdd(), bestRank, bestNumIter, bestLambda);
        //商品推荐
        Rating[] recommendProducts = model.recommendProducts(301, 10);

        for (Rating product : recommendProducts) {
            System.out.println("userId = "+ product.user() +", product = " + product.product() + ", rating = " + product.rating());
        }

        jsc.stop();
    }

    /**
     * 校验集预测数据和实际数据之间的均方根误差
     */
    public static Double computeRmse(MatrixFactorizationModel model, JavaRDD<Rating> data, Long n) {
        //进行预测
        JavaRDD<Rating> predictions = model.predict(data.mapToPair(v -> new Tuple2<>(v.user(), v.product())));

        JavaRDD<Tuple2<Double, Double>> predictionsAndRatings = predictions.mapToPair(v -> new Tuple2<>(new Tuple2<>(v.user(), v.product()), v.rating()))
                .join(data.mapToPair(v -> new Tuple2<>(new Tuple2<>(v.user(), v.product()), v.rating()))).values();

        Double reduce = predictionsAndRatings.map(v -> (v._1 - v._2) * (v._1 - v._2)).reduce((v1, v2) -> (v1 + v2) / n);

        //正平方根
        return Math.sqrt(reduce);
    }
}
