package com.tom.mongodb;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.tom.mongodb.spring.PersonDAO;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSpring {
    @Autowired
    private PersonDAO personDAO;

    @Test
    public void testSave() {
        Person person = new Person(ObjectId.get(), "Zoe", 18, new Address("M Road", "Tokyo city", "1250054"));
        personDAO.savePerson(person);
    }

    @Test
    public void testQueryPersonListByName() {
        List<Person> list = personDAO.queryPersonListByName("Tom");
        list.stream().forEach(t -> System.out.println(t));
    }

    @Test
    public void testQueryPersonListByPage() {
        List<Person> list = personDAO.queryPersonListByPage(1, 2);
        list.stream().forEach(t -> System.out.println(t));
    }

    @Test
    public void testUpdate() {
        Person p = new Person();
        p.setId(ObjectId.get());
        p.setAge(99);
        UpdateResult update = personDAO.update(p);
        System.out.println(update);
    }

    @Test
    public void testDelete() {
        ObjectId id = ObjectId.get();
        DeleteResult deleteResult = personDAO.deleteById(id.toString());
        System.out.println(deleteResult);
    }
}
