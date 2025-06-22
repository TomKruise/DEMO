package com.tom.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.function.Consumer;

public class MongoDBDemo {
    public static void main(String[] args) {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("testdb");
        MongoCollection<Document> user = database.getCollection("user");
        user.find().limit(5).forEach((Consumer<? super Document>) document -> {
            System.out.println(document.toJson());
        });
    }
}
