/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.teide.aortiz.satisfaccion;

import org.teide.aortiz.satisfaccion.bean.FieldsValues;
import org.teide.aortiz.satisfaccion.bean.DataBean;
import org.teide.aortiz.satisfaccion.bean.DataOrderBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.teide.aortiz.satisfaccion.bean.CourseBean;

/**
 *
 * @author antonio
 */
public class Satisfaccion {
        
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            Satisfaccion s = new Satisfaccion();
            s.obtainResults("/Users/antonio/Downloads/encuestas2/2EI.txt", 
                    "/Users/antonio/Downloads/GENERICA_v2016.xls",
                    "/Users/antonio/Downloads/encuestas2/resultado/resultado.xls");
            
        } catch (Exception ex) {
            System.out.println("Error: "+ex.getMessage());
            ex.printStackTrace();
        }
    }*/
    
    /**
     * Este método obtiene los resultados de cada alumnos, devolverá un listado de tipo DataBean por cada alumno
     * @param inputCSVfilename ruta completa del fichero CVS extraído de Moodle
     * @param inputXLSfilename ruta al fichero de entrada Excel que servirá de copia
     * @param outputXLSfilename ruta completa al fichero que se generará
     * @throws FileNotFoundException
     * @throws IOException 
     * @throws jxl.read.biff.BiffException 
     * @throws jxl.write.WriteException 
     */
    public void obtainResults (String inputCSVfilename, String inputXLSfilename, String outputXLSfilename) throws FileNotFoundException, IOException, BiffException, WriteException {
        CourseBean cb = new CourseBean();
        ArrayList<DataBean> al = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(new File(inputCSVfilename)));
        String line;
        boolean obtainDateAndName = true;
        String name = null, date = null;
        
        //La primera línea no hace falta que la leamos porque lleva las cabeceras
        br.readLine();
        while ((line=br.readLine())!=null) {
            String[] results = line.split("\t");
            //Para el primer alumno obtendremos la fecha y el nombre del grupo
            if (obtainDateAndName) {
                date = results[FieldsValues.DATE_FIELD].split(" ")[0];
                name = new File(inputCSVfilename).getName().split("\\.")[0];
                obtainDateAndName = false;
            }
            //Obtenemos todos las respuestas del usuario
            String[] resp = getResp(results);
            String sex = getSex(results);
            String[] know = getKnow(results);
            String[] opinion = getOpinion(results);
            
            DataBean db = new DataBean();
            db.setResp(resp);
            db.setSex(sex);
            db.setKnow(know);
            db.setOpinion(opinion);
            
            al.add(db);
        }
        
        cb.setDate(date);
        cb.setName(name);
        cb.setList(al);
        
        //Prueba impresión
        transformAll(cb, inputXLSfilename, outputXLSfilename);
    }
    
    /**
     * Permite extraer las respuestas de un usuario
     * @param values representa una respuesta completa de un usuario
     * @return todas las respuestas de satisfacción e importancia del usuario
     */
    private String[] getResp (String[] values) {
        return Arrays.copyOfRange(values, FieldsValues.START_RESP, FieldsValues.END_RESP);
    }
    
    /**
     * Permite extraer el sexo de un usuario
     * @param values representa una respuesta completa de un usuario
     * @return el sexo del usuario, 1 hombre 2 mujer
     */
    private String getSex (String[] values) {
        return values[FieldsValues.SEX].split(" ")[0];
    }
    
    /**
     * Permite extraer los conocimientos de un usuario
     * @param values representa una respuesta completa de un usuario
     * @return los conocimientos del usuario (como conoció el centro y como se considera como estudiante).
     */
    private String[] getKnow (String[] values) {
        return Arrays.copyOfRange(values, FieldsValues.START_KNOW, FieldsValues.END_KNOW);
    }
    
    /**
     * Permite extraer la opinión de un usuario
     * @param values representa una respuesta completa de un usuario
     * @return las opiniones del usuario (aspectos de mejora y que le agradan al alumno). Máximo 3 por cada
     */
    private String[] getOpinion (String[] values) {
        return Arrays.copyOfRange(values, FieldsValues.START_OPINION, FieldsValues.END_OPINION);
    }
    
    /**
     * Permite transformar el listado de respuestas del usuario y asignarlo a un int[][] 
     * @param userValues el listado de respuestas del usuario en formato int[][] para llevarlo a la Excel
     * @param values el listado de respuestas de un usuario
     * @return el listado de respuestas en formato para pasarlo a la Excel
     */
    private void transformResp (int[][] userValuesSatisfaccion, int[][] userValuesImportancia, String[] resp, int col) {
        int row = 0;
        for (int i=0;i<resp.length;i++) {
            if (i%2==0) {
                //Escribimos la satisfacción
                userValuesSatisfaccion[row][col]=Integer.parseInt(resp[i]);
            }
            else {
                //Escribimos la importancia
                userValuesImportancia[row++][col]=Integer.parseInt(resp[i]);
            }
        }
    }
    
    /**
     * Permite transformar el sexo de un usuario y asignarlo a un int[] 
     * @param userValuesel listado de sexo del usuario en formato in[] para llevarlo a la Excel
     * @param sex el sexo del usuario
     */
    private void transformSex (int[] userValues, String sex) {
        if (sex.equals(FieldsValues.MAN_VALUE)) userValues[Integer.parseInt(FieldsValues.MAN_VALUE)-1]++;
        else userValues[Integer.parseInt(FieldsValues.WOMAN_VALUE)-1]++;
    }
    
    /**
     * Permite transformar la opinión de un usuario y asignarlo a dos int[], uno con la información de cómo comoció el centro y otro como se valora como estudiante 
     * @param userValueKnow cómo conoció el centro
     * @param userValueStudent cómo se valora como estudiante
     * @param resp listado de respuestas del usuario
     */
    private void transformKnow (int[] userValueKnow, int[] userValueStudent, String[] resp) {
        //Primero tratamos como conoció el centro
        userValueKnow[Integer.parseInt(resp[0].split(" ")[0])-1]++;
        //Después tratamos como se valora como estudiante
        userValueStudent[Integer.parseInt(resp[2].split(" ")[0])-1]++;
    }
    
    /**
     * Permite transformar las preguntas abiertas del usuario
     * @param userValuesBetter aspectos a mejorar del centro
     * @param userValuesPlease aspectos que le agradan
     * @param resp respuestas del usuario
     */
    private void transformOpinion (ArrayList<DataOrderBean> userValuesBetter, ArrayList<DataOrderBean> userValuesPlease, String[] resp) {
        for (int i = 0; i < resp.length; i++) {
            //Aspectos a mejorar
            if (i<3) {
                if (resp[i]!= null && !resp[i].trim().isEmpty()) {
                    if (userValuesBetter.contains(new DataOrderBean(resp[i].trim()))) userValuesBetter.get(userValuesBetter.indexOf(new DataOrderBean(resp[i].trim().replace("&nbsp;", " ")))).increment();
                    else userValuesBetter.add(new DataOrderBean(resp[i].trim().replace("&nbsp;", " ")));
                }
            }
            else {
                if (resp[i]!= null && !resp[i].trim().isEmpty()) {
                    if (userValuesPlease.contains(new DataOrderBean(resp[i].trim()))) userValuesPlease.get(userValuesPlease.indexOf(new DataOrderBean(resp[i].trim().replace("&nbsp;", " ")))).increment();
                    else userValuesPlease.add(new DataOrderBean(resp[i].trim().replace("&nbsp;", " ")));
                }
            }
        }
    }
    
    /**
     * Transforma todas las respuestas en los valores necesarios para su paso a la Excel
     * @param values listado de valores de los usuarios
     */
    private void transformAll (CourseBean cb, String inputXLSfilename, String outputXLSfilename) throws IOException, BiffException, WriteException {
        int[][] userRespSatisfaccion = new int[FieldsValues.USER_VALUES_ROWS][cb.getList().size()];
        int[][] userRespImportancia = new int[FieldsValues.USER_VALUES_ROWS][cb.getList().size()];
        int[] sex = new int[2];
        int[] centerKnow = new int[FieldsValues.CENTER_VALUES];
        int[] studentKnow = new int[FieldsValues.STUDENT_VALUES];
        ArrayList<DataOrderBean> alBetter = new ArrayList<>();
        ArrayList<DataOrderBean> alPlease = new ArrayList<>();
        int col = 0;
        for (DataBean value : cb.getList()) {
            transformResp(userRespSatisfaccion, userRespImportancia, value.getResp(), col++);
            transformSex(sex, value.getSex());
            transformKnow(centerKnow, studentKnow, value.getKnow());
            transformOpinion(alBetter, alPlease, value.getOpinion());
        }
        
        //Imprimimos
        /*
        for (int i = 0; i < userRespSatisfaccion.length; i++) {
            for (int j = 0; j < userRespSatisfaccion[i].length; j++) {
                System.out.print(userRespSatisfaccion[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("--------------------------------");
        
        for (int i = 0; i < userRespImportancia.length; i++) {
            for (int j = 0; j < userRespImportancia[i].length; j++) {
                System.out.print(userRespImportancia[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("--------------------------------");
        /*
        System.out.println("SEX: "+sex[0]+" - "+sex[1]);
        System.out.println("Center: ");
        for (int i = 0; i < centerKnow.length; i++) {
            System.out.print(centerKnow[i]+" ");
        }
        System.out.println("-------------------------------");
        
        System.out.println("Student");
        for (int i = 0; i < studentKnow.length; i++) {
            System.out.print(studentKnow[i]+" ");
        }
        System.out.println("-------------------------------");
        
        for (DataOrderBean dataOrderBean : alBetter) {
            System.out.println(dataOrderBean.getResp()+" - "+dataOrderBean.getnResp());
        }
        System.out.println("-------------------------------");
        for (DataOrderBean dataOrderBean : alPlease) {
            System.out.println(dataOrderBean.getResp()+" - "+dataOrderBean.getnResp());
        }
        */
        
        //Escribimos en la excel
        exportExcel(userRespSatisfaccion, userRespImportancia, sex, centerKnow, studentKnow, alBetter, alPlease, cb.getDate(), cb.getName(), inputXLSfilename, outputXLSfilename);
    }
    
    /**
     * Permite escribir todos los valores en la Excel
     * @param userResp representa las respuestas de los usuarios
     * @param sex representa los sexos de los usuarios
     * @param centerKnow representa cómo han conocido el centro los usuarios
     * @param studentKnow representa cómo se consideran como estudiantes los usuarios
     * @param alBetter representan los aspectos que le gustan
     * @param alPlease representan los aspectos a mejorar
     * @param date repreenta la fecha de la encuesta
     * @param name representa el nombre del grupo
     * @param inputXLSfilename representa el fichero Excel de entrada que servidrá de copia
     * @param outputXLSfilename representa el fichero Excel de salida que se generará
     * @throws IOException
     * @throws BiffException
     * @throws WriteException 
     */
    private void exportExcel (int[][] userRespSatisfaccion, int[][] userRespImportancia, int[] sex, int[] centerKnow, int[] studentKnow,  ArrayList<DataOrderBean> alBetter, 
             ArrayList<DataOrderBean> alPlease, String date, String name,
            String inputXLSfilename, String outputXLSfilename) throws IOException, BiffException, WriteException {
        WorkbookSettings ws = new WorkbookSettings(); 
        ws.setEncoding("iso-8859-1");
        Workbook inputXLS = Workbook.getWorkbook(new File(inputXLSfilename),ws); 
        WritableWorkbook outputXLS = Workbook.createWorkbook(new File(outputXLSfilename), inputXLS);
        
        //Depositamos los valores en la primera hoja
        WritableSheet sheet = outputXLS.getSheet(0);
        
        //Escribimos nombre del ciclo y fecha
        sheet.addCell((WritableCell) new Label (FieldsValues.NAME_CORDS[1],FieldsValues.NAME_CORDS[0], name));
        sheet.addCell((WritableCell) new Label (FieldsValues.DATE_CORDS[1],FieldsValues.DATE_CORDS[0], date));
        
        //Escribimos número de hombres, mujeres y total de encuestados
        sheet.addCell((WritableCell) new Number (FieldsValues.MAN_CORDS[1],FieldsValues.MAN_CORDS[0], sex[0]));
        sheet.addCell((WritableCell) new Number (FieldsValues.WOMAN_CORDS[1],FieldsValues.WOMAN_CORDS[0], sex[1]));
        sheet.addCell((WritableCell) new Number (FieldsValues.PEOPLE_CORDS[1],FieldsValues.PEOPLE_CORDS[0], sex[0]+sex[1]));
        
        //Escribimos las respuestas de los clientes - Satisfacción
        int row = FieldsValues.START_USER_RESP_SATISFACCION[0], col = FieldsValues.START_USER_RESP_SATISFACCION[1];
        for (int i = 0; i < userRespSatisfaccion.length; i++) {
            for (int j = 0; j < userRespSatisfaccion[i].length; j++) {
                sheet.addCell((WritableCell) new Number (col+j,row+i, userRespSatisfaccion[i][j]));
            }
        }
        
        //Escribimos las respuestas de los clientes - Importancia
        row = FieldsValues.START_USER_RESP_IMPORTANCIA[0];
        col = FieldsValues.START_USER_RESP_IMPORTANCIA[1];
        for (int i = 0; i < userRespImportancia.length; i++) {
            for (int j = 0; j < userRespImportancia[i].length; j++) {
                sheet.addCell((WritableCell) new Number (col+j,row+i, userRespImportancia[i][j]));
            }
        }
        
        //Escribimos cómo nos ha conocido y como es como estudiante
        row = FieldsValues.CENTER_KNOW[0];
        col = FieldsValues.CENTER_KNOW[1];
        for (int i = 0; i < centerKnow.length; i++) {
            sheet.addCell((WritableCell) new Number (col,row+i, centerKnow[i]));
        }
        row = FieldsValues.STUDENT_KNOW[0];
        col = FieldsValues.STUDENT_KNOW[1];
        for (int i = 0; i < studentKnow.length; i++) {
            sheet.addCell((WritableCell) new Number (col,row+i, studentKnow[i]));
        }     
        
        //Por último escribimos las preguntas abiertas
        row = FieldsValues.BETTER_OPINION[0];
        col = FieldsValues.BETTER_OPINION[1];
        int rowN = FieldsValues.BETTER_OPINION_COUNT[0];
        int colN = FieldsValues.BETTER_OPINION_COUNT[1];
        Collections.sort(alBetter);
        for (int i = 0; i < alBetter.size() && i < FieldsValues.N_MAX_OPINION; i++) {
            sheet.addCell((WritableCell) new Label (col,row+i, alBetter.get(i).getResp()));
            sheet.addCell((WritableCell) new Number (colN,rowN+i, alBetter.get(i).getnResp()));
        }
        row = FieldsValues.PLEASE_OPINION[0];
        col = FieldsValues.PLEASE_OPINION[1];
        rowN = FieldsValues.PLEASE_OPINION_COUNT[0];
        colN = FieldsValues.PLEASE_OPINION_COUNT[1];
        Collections.sort(alPlease);
        for (int i = 0; i < alPlease.size() && i < FieldsValues.N_MAX_OPINION; i++) {
            sheet.addCell((WritableCell) new Label (col,row+i, alPlease.get(i).getResp()));
            sheet.addCell((WritableCell) new Number (colN,rowN+i, alPlease.get(i).getnResp()));

        }
       
        //Escribimos todos los valores en la Excel    
        outputXLS.write(); 
        outputXLS.close();
        inputXLS.close();
    }
    
}
