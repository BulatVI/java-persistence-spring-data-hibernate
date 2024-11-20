package com.manning.javapersistence.ch02;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.jupiter.api.Test;

class HelloWorldJPATest {

    @Test
    void storeLoadMessage() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ch02");

        try {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            Message message = new Message();
            message.setText("Hi I'm Bulat!");

            em.persist(message);

            em.getTransaction().commit();
            // INSERT into MESSAGE (ID, TEXT) values (1, 'Hello World!')

            em.getTransaction().begin();

            List<Message> messages = em.createQuery("select m from Message m", Message.class).getResultList();
            // SELECT * from MESSAGE

            messages.get(messages.size() - 1).setText("Hi I'm Bulat and I have a brother!");

            em.getTransaction().commit();
            // UPDATE MESSAGE set TEXT = 'Hello World from JPA!' where ID = 1

            assertAll(
                    () -> assertEquals(1, messages.size()),
                    () -> assertEquals("Hi I'm Bulat and I have a brother!", messages.get(0).getText()));

            em.close();

        } finally {
            emf.close();
        }
    }

}
