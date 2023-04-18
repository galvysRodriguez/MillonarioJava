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
public class Usuario {
    private String nombre;
    private int ronda;
    private int puntajeMaximo;
    private boolean cincuentaCincuenta;
    private boolean cambiarPregunta;
    
    public Usuario(String nom){
        this.nombre=nom;
        this.ronda=1;
        this.puntajeMaximo=0;
        this.cincuentaCincuenta=true;
        this.cambiarPregunta = true;
    }
    
    public boolean getCambiarPregunta() {
        return cambiarPregunta;
    }

    public void setCambiarPregunta(boolean cambiarPregunta) {
        this.cambiarPregunta = cambiarPregunta;
    }
    
    public String getNombre(){
        return this.nombre;
    }
    
    public int getRonda(){
        return this.ronda;
    }
    
    public int getPuntajeMaximo(){
        return this.puntajeMaximo;
    }
    
    public boolean getValorCincuentaCincuenta(){
        return this.cincuentaCincuenta;
    }
    
    public void setNombre(String nom){
        this.nombre=nom;
    }
    
    public void setPuntajeMaximo(int punt){
        this.puntajeMaximo= punt;
    }
    
    public void setValorCincuentaCincuenta(boolean val){
        this.cincuentaCincuenta=val;
    }
    
    public void avanzarRonda(){
        this.ronda+=1;
    }
    
    public void reiniciarRonda(){
        this.ronda=0;
    }
    
    public void iniciarPartida(){
        this.cincuentaCincuenta=true;
        this.cambiarPregunta = true;
        this.puntajeMaximo = 0;
        this.reiniciarRonda();
    }
    
}
