package com.tom.mongodb;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class TestPerson {
    MongoCollection<Person> collection;
    @Before
    public void init() {
        CodecRegistry registry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));


        MongoClient client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = client.getDatabase("testdb").withCodecRegistry(registry);
        collection = database.getCollection("user", Person.class);
    }

    @Test
    public void testInsert() {
        Person p = new Person(ObjectId.get(), "Jerry", 20, new Address("Road", "Beijing city", "100101"));
        collection.insertOne(p);
    }

    @Test
    public void testInserts() {
        List<Person> list = Arrays.asList(new Person(ObjectId.get(), "Alice", 30, new Address("Road", "Shanghai city", "200202")),
                new Person(ObjectId.get(), "Black", 40, new Address("Road", "Nanjing city", "300303")));
        collection.insertMany(list);
    }

    @Test
    public void testQuery() {
        collection.find(
                eq("name", "Jerry")
        ).forEach((Consumer<? super Person>)p ->
            System.out.println(p)
        );
    }

    @Test
    public void testUpdate() {
        UpdateResult updateResult = collection.updateMany(eq("name", "Jerry"), set("age", 55));
        System.out.println(updateResult.getModifiedCount());
    }

    @Test
    public void testDelete() {
        DeleteResult deleteResult = collection.deleteOne(eq("name", "Jerry"));
        System.out.println(deleteResult.getDeletedCount());
    }
}
