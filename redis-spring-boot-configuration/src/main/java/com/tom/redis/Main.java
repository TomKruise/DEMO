package com.tom.redis;

import java.util.Scanner;

class Main {

  public static String StringChallenge(String str) {
    int left = 0;
    int right = 0;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == '(') {
        left++;
      } else {
        right ++;
      }
    }
    // code goes here
    int num = left < right ? right - left : left - right;
    return "" + num;
  }

  public static void main (String[] args) {  
    // keep this function call here     
    Scanner s = new Scanner(System.in);
    System.out.print(StringChallenge(s.nextLine())); 
  }

}