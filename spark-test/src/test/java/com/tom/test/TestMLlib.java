package com.tom.test;

import org.apache.spark.ml.feature.LabeledPoint;
import org.apache.spark.ml.linalg.Matrices;
import org.apache.spark.ml.linalg.Matrix;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.junit.Test;


public class TestMLlib {

    @Test
    public void testDense() {
        Vector vd = Vectors.dense(9, 5, 2, 7);//定义密集型数据集
        double v = vd.apply(2);//根据下标获取值
        System.out.println(v);
    }

    @Test
    public void testSparse() {
        int[] indexes = new int[]{0,1,2,4};
        double[] values = new double[]{9,5,2,7};

//        Vector sparse = Vectors.sparse(4, indexes, values);
        Vector sparse = Vectors.sparse(5, indexes, values);//定义稀疏型数据集
        System.out.println(sparse.apply(2));
        System.out.println(sparse.apply(3));
        System.out.println(sparse.apply(4));
    }

    @Test //测试向量标签
    public void testLabeledPoint() {
        Vector dense = Vectors.dense(9, 5, 2, 7);
        LabeledPoint labeledPoint = new LabeledPoint(1, dense);
        System.out.println(labeledPoint.label());
        System.out.println(labeledPoint.features());
        System.out.println(labeledPoint);

        int[] indexes = new int[]{0,1,2,3};
        double[] values = new double[]{9,5,2,7};
        Vector sparse = Vectors.sparse(4, indexes, values);
        LabeledPoint labeledPoint2 = new LabeledPoint(2, sparse);
        System.out.println(labeledPoint2.label());
        System.out.println(labeledPoint2.features());
        System.out.println(labeledPoint2);
    }

    @Test
    public void testMatrix() {
        double[] values = new double[]{1,2,3,4,5,6};
        Matrix mx = Matrices.dense(2, 3, values);
        System.out.println(mx);
    }
}
