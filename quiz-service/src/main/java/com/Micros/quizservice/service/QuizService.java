package com.Micros.quizservice.service;

import com.Micros.quizservice.dao.QuizDao;
import com.Micros.quizservice.feign.QuizInterface;
import com.Micros.quizservice.model.QuestionWrapper;
import com.Micros.quizservice.model.Quiz;
import com.Micros.quizservice.model.Response;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

	@Autowired
	QuizDao quizDao;

	@Autowired
	QuizInterface quizInterface;

	public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

		List<Integer> questions = quizInterface.getQuestionsForQuiz(category, numQ).getBody();
		Quiz quiz = new Quiz();
		quiz.setTitle(title);
		quiz.setQuestionIds(questions);
		quizDao.save(quiz);

		return new ResponseEntity<>("Success", HttpStatus.CREATED);

	}

	@CircuitBreaker(name = "questionService", fallbackMethod = "fallbackGetQuestions")
	public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
		Quiz quiz = quizDao.findById(id).orElseThrow();
		List<Integer> questionIds = quiz.getQuestionIds();
		return quizInterface.getQuestionsFromId(questionIds);

	}

	public ResponseEntity<List<QuestionWrapper>> fallbackGetQuestions(Integer id, Throwable ex) {
		System.out.println("Question-Service DOWN — returning fallback response");
		return new ResponseEntity<>(List.of(), HttpStatus.SERVICE_UNAVAILABLE);

	}

	public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
		ResponseEntity<Integer> score = quizInterface.getScore(responses);
		return score;
	}
}
