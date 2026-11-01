package com.example.assignment2project;

import java.util.List;

public class Question {
    private long id;
    private long tournamentId;
    private String question;
    private String category;
    private String type;
    private String difficulty;
    private String correctAnswer;
    private List<String> incorrectAnswers;

    public Question() {}

    public Question(long id, long tournamentId, String question, String category, String type, String difficulty, String correctAnswer, List<String> incorrectAnswers) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.question = question;
        this.category = category;
        this.type = type;
        this.difficulty = difficulty;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
}
