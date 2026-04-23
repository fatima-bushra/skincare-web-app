package com.glowmatch.controller;

import com.glowmatch.model.*;
import com.glowmatch.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/questions")
    public ResponseEntity<List<QuizQuestion>> getQuestions() {
        return ResponseEntity.ok(quizService.getAllQuestions());
    }

    @PostMapping("/submit")
    public ResponseEntity<QuizResult> submitQuiz(@RequestBody QuizSubmission submission) {
        QuizResult result = quizService.processAnswers(submission);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("GlowMatch API is running ✨");
    }
}
