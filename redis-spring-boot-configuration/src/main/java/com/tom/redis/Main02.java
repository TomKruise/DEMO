package com.tom.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Main02 {

  public static String StringChallenge(String str) {
    char[] chars = str.toCharArray();
    List<Integer> list = new ArrayList<Integer>();
    for (char c : chars) {
      list.add((int)c);
    }
    Collections.sort(list);
    // code goes here
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      b.append((char)((int)list.get(i)));
    }
    str = b.toString();
    return str;
  }

  public static void main (String[] args) {  
    // keep this function call here     
    Scanner s = new Scanner(System.in);
    System.out.print(StringChallenge(s.nextLine())); 
  }

}