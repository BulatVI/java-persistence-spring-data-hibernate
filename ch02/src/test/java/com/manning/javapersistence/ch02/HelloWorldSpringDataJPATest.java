package com.manning.javapersistence.ch02;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.manning.javapersistence.ch02.configuration.SpringDataConfiguration;
import com.manning.javapersistence.ch02.repositories.MessageRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringDataConfiguration.class })
class HelloWorldSpringDataJPATest {
    
    @Autowired
    private MessageRepository messageRepository;

    @Test
    void storeLoadMessage() {
        Message message = new Message();
        message.setText("Hello World from Spring Data JPA!");
        messageRepository.save(message);
        List<Message> messages = (List<Message>) messageRepository.findAll();
        assertAll(
                () -> assertEquals(1, messages.size()),
                () -> assertEquals("Hello World from Spring Data JPA!", messages.get(0).getText()));
    }
}
