/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clases;

/**
 *
 * @Galvys Rodriguez
 */
public class Pregunta {
    
    private String textoPregunta;
    private final String opciones[] = new String[4];
    private int opcionCorrecta;
    private String imagen;
    
    public Pregunta(String pregunta,String opc1,String opc2,String opc3,String opc4,int opcC, String img){
        this.textoPregunta=pregunta;
        this.opciones[0]=opc1;
        this.opciones[1]=opc2;
        this.opciones[2]=opc3;
        this.opciones[3]=opc4;
        this.opcionCorrecta=opcC;
        this.imagen=img;
    }
    
    public String getTextoPregunta(){
        return this.textoPregunta;
    }
    
    public String getOpcion(int n){
        return this.opciones[n];
    }
    
    public int getOpcionCorrecta(){
        return this.opcionCorrecta;
    }
    
    public String getImagen(){
        return this.imagen;
    }
    
    public void setTextoPregunta(String pregunta){
        this.textoPregunta=pregunta;
    }
    
    public void setOpcion(int n,String opcion){
        this.opciones[n]=opcion;
    }
    
    public void setOpcionCorrecta(int opcionC){
        this.opcionCorrecta=opcionC;
    }
    
    public void setImagen(String img){
        this.imagen=img;
    }
    
}
