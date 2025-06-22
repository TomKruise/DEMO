package com.tom.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;

public class TestMongoDB {
    MongoCollection<Document> collection;
    @Before
    public void init() {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("testdb");
        collection = database.getCollection("user");
    }

    @Test
    public void testQuery() {
        collection.find(
                Filters.and(
                        Filters.lte("age", 50),
                        Filters.gte("id", 100)
                )
        )
                .sort(Sorts.descending("id"))
                .projection(
                        Projections.fields(
                                Projections.include("id", "age"),
                                Projections.excludeId()
                        )
                )
                .forEach((Consumer<? super Document>) document -> {
                    System.out.println(document.toJson());
                });
    }

    @Test
    public void testInsert() {
        Document document = new Document();
        document.append("id", 9999);
        document.append("username", "Tom");
        document.append("age", 30);
        collection.insertOne(document);

        collection.find(eq("id", 9999)).forEach((Consumer<? super Document>) d -> {
            System.out.println(d.toJson());
        });
    }

    @Test
    public void testUpdate() {
        collection.updateOne(eq("id", 9999), Updates.set("age", 40));

        collection.find(eq("id", 9999)).forEach((Consumer<? super Document>) d -> {
            System.out.println(d.toJson());
        });
    }

    @Test
    public void testDelete() {
        DeleteResult age = collection.deleteMany(eq("age", 25));
        System.out.println(age.getDeletedCount());
    }
}
