package com.tom.test;

import com.sun.xml.internal.fastinfoset.Encoder;
import com.tom.bean.Student;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.*;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;
import scala.Tuple2;
import scala.reflect.internal.util.Collections;

import java.util.List;

public class TestMLlib2 {

    @Test
    public void testRowMatrix() {
        JavaSparkContext jsc = getJSC();
        JavaRDD<String> r1 = jsc.textFile("/Users/Tom/Documents/Developer/IdeaProjects/DEMO/spark-test/src/test/resources/a.txt");
        JavaRDD<Vector> r2 = r1.map(v1 -> {
            String[] ss = v1.split(" ");
            double[] ds = new double[ss.length];
            for (int i = 0; i < ss.length; i++) {
                ds[i]=Double.valueOf(ss[i]);
            }
            return Vectors.dense(ds);
        });

        RowMatrix rmx = new RowMatrix(r2.rdd());

        System.out.println(rmx.numRows());
        System.out.println(rmx.numCols());
    }

    @Test
    public void testIndexedRowMatrix() {
        JavaSparkContext jsc = getJSC();
        JavaRDD<String> r1 = jsc.textFile("/Users/Tom/Documents/Developer/IdeaProjects/DEMO/spark-test/src/test/resources/a.txt");
        JavaRDD<Vector> r2 = r1.map(v1 -> {
            String[] ss = v1.split(" ");
            double[] ds = new double[ss.length];
            for (int i = 0; i < ss.length; i++) {
                ds[i]=Double.valueOf(ss[i]);
            }
            return Vectors.dense(ds);
        });

        JavaRDD<IndexedRow> rdd = r2.map(v -> new IndexedRow(v.size(),v));

        //行索引矩阵
        IndexedRowMatrix indexedRowMatrix = new IndexedRowMatrix(rdd.rdd());
        System.out.println(indexedRowMatrix.numRows());
        System.out.println(indexedRowMatrix.numCols());

        JavaRDD<IndexedRow> javaRDD = indexedRowMatrix.rows().toJavaRDD();
        List<IndexedRow> collect = javaRDD.collect();
        collect.forEach(n -> System.out.println(n));
    }

    @Test
    public void testCoordinateMatrix() {
        JavaSparkContext jsc = getJSC();
        JavaRDD<String> r1 = jsc.textFile("/Users/Tom/Documents/Developer/IdeaProjects/DEMO/spark-test/src/test/resources/a.txt");
        JavaRDD<MatrixEntry> r2 = r1.map(v1 -> {
            String[] ss = v1.split(" ");
            return new MatrixEntry(Long.valueOf(ss[0]), Long.valueOf(ss[1]), Double.valueOf(ss[2]));
        });

        //坐标矩阵
        CoordinateMatrix coordinateMatrix = new CoordinateMatrix(r2.rdd());
        List<MatrixEntry> collect = coordinateMatrix.entries().toJavaRDD().collect();
        collect.forEach(ele -> System.out.println(ele));
    }

    @Test
    public void testBlockMatrix() {
        JavaSparkContext jsc = getJSC();
        JavaRDD<String> r1 = jsc.textFile("/Users/Tom/Documents/Developer/IdeaProjects/DEMO/spark-test/src/test/resources/a.txt");
        JavaRDD<MatrixEntry> r2 = r1.map(v1 -> {
            String[] ss = v1.split(" ");
            return new MatrixEntry(Long.valueOf(ss[0]), Long.valueOf(ss[1]), Double.valueOf(ss[2]));
        });

        CoordinateMatrix coordinateMatrix = new CoordinateMatrix(r2.rdd());
        //分块矩阵
        BlockMatrix blockMatrix = coordinateMatrix.toBlockMatrix();
        //校验分块矩阵是否正确，不正确则抛出异常
        blockMatrix.validate();
        BlockMatrix add = blockMatrix.add(blockMatrix);
        List<Tuple2<Tuple2<Object, Object>, Matrix>> collect = add.blocks().toJavaRDD().collect();
        collect.forEach(e -> System.out.println(e));
    }

    @Test
    public void testDataSet() {
        SparkSession sparkSession = SparkSession
                .builder()
                .master("local[*]")
                .appName("TestMLlib")
                .getOrCreate();
        JavaRDD<String> r1 = sparkSession
                .read()
                .textFile("/Users/Tom/Documents/Developer/IdeaProjects/DEMO/spark-test/src/test/resources/users.txt")
                .javaRDD();
        JavaRDD<Student> studentJavaRDD = r1.map(v -> {
            String[] ss = v.split(",");
            Student s = new Student();
            s.setId(Long.valueOf(ss[0]));
            s.setName(ss[1]);
            s.setAge(Integer.valueOf(ss[2]));

            return s;
        });

        Dataset<Row> dataset = sparkSession.createDataFrame(studentJavaRDD, Student.class);
        dataset.show();

        dataset.select("id", "name").orderBy(dataset.col("id").desc()).show();
    }

    private JavaSparkContext getJSC() {
        SparkConf sparkConf = new SparkConf().setAppName("SparkMLlib").setMaster("local[*]");
        return new JavaSparkContext(sparkConf);
    }
}
