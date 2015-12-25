package com.example.elmina.equations;

/**
 * Created by elmina on 04.12.15.
 */
public class Equation {
    String text;
    Boolean answ;
    public Equation(String text, Boolean answ){
        this.text = text;
        this.answ = answ;
    }
    public Boolean isRight(){
        return answ;
    }
}
