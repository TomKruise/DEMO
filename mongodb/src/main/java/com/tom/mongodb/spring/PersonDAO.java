package com.tom.mongodb.spring;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.tom.mongodb.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonDAO {
    @Autowired
    private MongoTemplate mongoTemplate;

    public Person savePerson(Person person) {
        return mongoTemplate.save(person);
    }

    public List<Person> queryPersonListByName(String name) {
        Query query = Query.query(Criteria.where("name").is(name));
        return mongoTemplate.find(query, Person.class);
    }

    public List<Person> queryPersonListByPage(Integer page, Integer rows) {
        Query query = new Query().limit(rows).skip((page-1)*rows);
        return mongoTemplate.find(query, Person.class);
    }

    public UpdateResult update(Person p) {
        Query query = Query.query(Criteria.where("id").is(p.getId()));
        Update update = Update.update("age", p.getAge());
        return mongoTemplate.updateFirst(query, update, Person.class);
    }

    public DeleteResult deleteById(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, Person.class);
    }
}
