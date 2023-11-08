package com.tom.spark.hang;

import java.util.Scanner;

public class Driver {
    public static void main(String[] args) {
        Scanner key = new Scanner(System.in);
        Hangman h1 = new Hangman();
        h1.initializeGame_collectWord(key);
        Hangman h2 = new Hangman();
        h2.initializeGame_collectWord(key);
        h2.playGame(key);
        Hangman h3 = new Hangman();
        Hangman h4 = new Hangman();
        h3.initializeGame_collectWord(key);
        h3.play_a_guess(key);
        h4.initializeGame_collectWord(key);
        h3.play_a_guess(key);
        h4.play_a_guess(key);
        h3.play_a_guess(key);
        h4.play_a_guess(key);
        h3.playGame(key);
        h4.playGame(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
        h1.play_a_guess(key);
    }
}
