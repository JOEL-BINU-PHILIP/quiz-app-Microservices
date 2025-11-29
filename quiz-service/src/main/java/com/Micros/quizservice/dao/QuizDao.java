package com.Micros.quizservice.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.Micros.quizservice.model.Quiz;

public interface QuizDao extends JpaRepository<Quiz,Integer> {
}
