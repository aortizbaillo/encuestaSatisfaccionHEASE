/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.teide.aortiz.satisfaccion.bean;

/**
 *
 * @author antonio
 */
public interface FieldsValues {
    
    public static final int DATE_FIELD = 1;
    public static final int NAME_FIELD = 4;
    public static final int START_RESP = 10;
    public static final int END_RESP = 56; // Cambiado HEASE
    public static final int SEX = 9;
    public static final int START_KNOW = 56; // Cambiado HEASE
    public static final int END_KNOW = 59; //Cambiado HEASE
    public static final int START_OPINION = 59; // Cambiado HEASE
    public static final int END_OPINION = 65; //Cambiado HEASE
    
    public static final int USER_VALUES_ROWS = 23;
    //public static final int USER_VALUES_COLS = 25;
    public static final int[] SATISFACTION_COLS = {8,0,2,4,6};
    public static final int SATISFACTION_COL = 10;
    public static final int[] IMPORTANCE_COLS = {9,1,3,5,7};
    public static final int IMPORTANCE_COL = 11;
    public static String MAN_VALUE = "1";
    public static String WOMAN_VALUE = "2";
    public static final int CENTER_VALUES = 6;
    public static final int STUDENT_VALUES = 4;
    
    public static final int[] NAME_CORDS = {2,1};
    public static final int[] DATE_CORDS = {2,19};
    public static final int[] MAN_CORDS = {4,6};
    public static final int[] WOMAN_CORDS = {4,12};
    public static final int[] PEOPLE_CORDS = {6,24};
    public static final int[] START_USER_RESP_SATISFACCION = {12,1};
    public static final int[] START_USER_RESP_IMPORTANCIA = {39,1};
    public static final int[] CENTER_KNOW = {65,7};
    public static final int[] STUDENT_KNOW = {74,5};
    public static final int[] BETTER_OPINION = {81,0};
    public static final int[] BETTER_OPINION_COUNT = {81,12};
    public static final int[] PLEASE_OPINION = {94,0};
    public static final int[] PLEASE_OPINION_COUNT = {94,12};
    public static final int N_MAX_OPINION = 10; 
}
