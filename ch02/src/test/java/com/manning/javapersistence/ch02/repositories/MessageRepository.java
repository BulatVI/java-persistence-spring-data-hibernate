package com.manning.javapersistence.ch02.repositories;

import org.springframework.data.repository.CrudRepository;

import com.manning.javapersistence.ch02.Message;

public interface MessageRepository extends CrudRepository<Message, Long>{
    
} 
