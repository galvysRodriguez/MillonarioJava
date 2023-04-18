/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import clases.Pregunta;
import clases.Usuario;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @@Galvys Rodriguez
 */
public class Interfaz extends javax.swing.JFrame {

    final static String BARRA = File.separator;
    final static String UBICACION = System.getProperty("user.dir") + BARRA + "src" + BARRA;

    static Pregunta arregloPreguntas[][] = new Pregunta[3][10];
    static String tablaPosiciones[] = new String[10];
    static Pregunta preguntaActual;
    static Usuario userActual;
    static JLabel etiquetaEliminada1 = null, etiquetaEliminada2;
    static JTextField campoEliminada1, campoEliminada2;

    static String nombreUsuario;
    static JPanel panelPadreLayout;
    static CardLayout cardLayout;

    /**
     * Creates new form InterfazMenu
     */
    public Interfaz() {
        initComponents();
        obtenerLayout();
        crearDirectorios();
        formatearCampos();
        reproducirMusica();
    }

    private void reproducirMusica() {
        AudioInputStream inputStream = null;

        try {
            String direccionMusica = UBICACION + "archivos" + BARRA + "musicaDeFondo.wav";
            Clip clip = AudioSystem.getClip();
            inputStream = AudioSystem.getAudioInputStream(new File(direccionMusica));
            clip.open(inputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public ImageIcon obtenerImagen(String img) {
        img = img.replace("/", BARRA);
        ImageIcon imagen;
        imagen = new ImageIcon(UBICACION + "imagenes" + BARRA + img);
        return imagen;
    }

    private void formatearCampos() {
        modificarFormatoTexto(campoPregunta);
        labelComoJugar.setVisible(false);
        labelMejoresPuntuaciones.setVisible(false);
    }

    private void modificarFormatoTexto(JTextPane campo) {
        StyledDocument documentStyle = campo.getStyledDocument();
        SimpleAttributeSet centerAttribute = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerAttribute, StyleConstants.ALIGN_CENTER);
        documentStyle.setParagraphAttributes(0, documentStyle.getLength(), centerAttribute, false);
    }

    private void obtenerLayout() {
        cardLayout = (CardLayout) panelPadre.getLayout();
        panelPadreLayout = panelPadre;
    }

    private void crearDirectorios() {
        File fichero = new File(UBICACION + "archivos" + BARRA);

        if (!fichero.exists()) {
            if (fichero.mkdirs()) {
                System.out.println("Se creo la carpeta");
            } else {
                System.out.println("Error al crear carpeta");
            }
        } else {
            System.out.println("El fichero ya existe");
        }
    }

    public Point posicionEnArreglo(Pregunta pregunta) {
        Point posicion = new Point(0, 0);

        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 9; j++) {
                if (arregloPreguntas[i][j] == pregunta) {
                    posicion.move(i, j);

                }
            }
        }

        return posicion;
    }

    public Pregunta leerPregunta(int numPregunta) {
        Pregunta nuevaPregunta = null;
        String busquedaPregunta = "";
        String txtPregunta, opc1, opc2, opc3, opc4, opcC, img;

        try {
            BufferedReader lector;
            lector = new BufferedReader(new FileReader(UBICACION + "archivos" + BARRA + "preguntas.txt"));
            while (!busquedaPregunta.trim().matches("P" + numPregunta)) {
                busquedaPregunta = lector.readLine();
            }
            txtPregunta = lector.readLine();
            opc1 = lector.readLine();
            opc2 = lector.readLine();
            opc3 = lector.readLine();
            opc4 = lector.readLine();
            opcC = lector.readLine();
            img = lector.readLine();

            nuevaPregunta = new Pregunta(txtPregunta, opc1, opc2, opc3, opc4, Integer.parseInt(opcC), img);

            lector.close();
        } catch (Exception e) {
            System.out.println("ERROR: Fallo en la lectura del archivo");
        }
        return nuevaPregunta;
    }

    public void llenarArregloPreguntas() {
        int numPregunta = 1;
        for (int j = 0; j <= 2; j++) {
            for (int k = 0; k <= 9; k++) {

                arregloPreguntas[j][k] = leerPregunta(numPregunta);
                numPregunta++;
            }
        }
    }

    public void partidaNueva(Usuario user) {
        llenarArregloPreguntas();
        user.iniciarPartida();
        actualizarPregunta(user);
        actualizarPuntuacion();

        botComodin5050.setVisible(true);
        botCambioPregunta.setVisible(true);
    }

    public Pregunta escogerPreguntaAleatoria(int nivel) {
        int seleccion = ThreadLocalRandom.current().nextInt(0, 9);

        while (arregloPreguntas[nivel][seleccion].getTextoPregunta() == null){
            seleccion = ThreadLocalRandom.current().nextInt(0, 9);
        }
        return arregloPreguntas[nivel][seleccion];
    }

    public void actualizarPregunta(Usuario user) {
        if (user.getRonda() < 6) {
            preguntaActual = escogerPreguntaAleatoria(0);
        } else if (user.getRonda() < 11) {
            preguntaActual = escogerPreguntaAleatoria(1);
        } else {
            preguntaActual = escogerPreguntaAleatoria(2);
        }

        llenarLabelsNuevaPregunta(preguntaActual);
    }

    public int puntuacion(int i) {
        int aux = 0;
        switch (i) {
            case 1:
                aux = 100;
                break;
            case 2:
                aux = 200;
                break;
            case 3:
                aux = 300;
                break;
            case 4:
                aux = 500;
                break;
            case 5:
                aux = 1000;
                break;
            case 6:
                aux = 2000;
                break;
            case 7:
                aux = 4000;
                break;
            case 8:
                aux = 6000;
                break;
            case 9:
                aux = 8000;
                break;
            case 10:
                aux = 10000;
                break;
            case 11:
                aux = 11000;
                break;
            case 12:
                aux = 12000;
                break;
            case 13:
                aux = 13000;
                break;
            case 14:
                aux = 14000;
                break;
            case 15:
                aux = 15000;
                break;

        }
        return aux;
    }

    public void actualizarPuntuacionAux(JLabel label) {
        flecha1.setVisible(false);
        flecha2.setVisible(false);
        flecha3.setVisible(false);
        flecha4.setVisible(false);
        flecha5.setVisible(false);
        flecha6.setVisible(false);
        flecha7.setVisible(false);
        flecha8.setVisible(false);
        flecha9.setVisible(false);
        flecha10.setVisible(false);
        flecha11.setVisible(false);
        flecha12.setVisible(false);
        flecha13.setVisible(false);
        flecha14.setVisible(false);
        flecha15.setVisible(false);

        if (label != null) {
            label.setVisible(true);
        }
    }

    public void actualizarPuntuacion() {
        int ronda = userActual.getRonda();
        JLabel aux = null;

        switch (ronda) {
            case 1:
                aux = flecha1;
                break;
            case 2:
                aux = flecha2;
                break;
            case 3:
                aux = flecha3;
                break;
            case 4:
                aux = flecha4;
                break;
            case 5:
                aux = flecha5;
                break;
            case 6:
                aux = flecha6;
                break;
            case 7:
                aux = flecha7;
                break;
            case 8:
                aux = flecha8;
                break;
            case 9:
                aux = flecha9;
                break;
            case 10:
                aux = flecha10;
                break;
            case 11:
                aux = flecha11;
                break;
            case 12:
                aux = flecha12;
                break;
            case 13:
                aux = flecha13;
                break;
            case 14:
                aux = flecha14;
                break;
            case 15:
                aux = flecha15;
                break;
        }

        actualizarPuntuacionAux(aux);
    }

    public void cincuentaCincuenta() {
        int valor = preguntaActual.getOpcionCorrecta();
        int eliminar1 = 1;
        int eliminar2 = 3;

        do {
            eliminar1 = ThreadLocalRandom.current().nextInt(1, 5);
        } while (eliminar1 == valor);

        do {
            eliminar2 = ThreadLocalRandom.current().nextInt(1, 5);
        } while (eliminar2 == eliminar1 || eliminar2 == valor);

        JPanel panelInterno = (JPanel) panelPadre.getComponent(1);
        etiquetaEliminada1 = (JLabel) panelInterno.getComponent(9 + eliminar1);
        etiquetaEliminada2 = (JLabel) panelInterno.getComponent(9 + eliminar2);
        campoEliminada1 = (JTextField) panelInterno.getComponent(1 + eliminar1);
        campoEliminada2 = (JTextField) panelInterno.getComponent(1 + eliminar2);

        etiquetaEliminada1.setVisible(false);
        etiquetaEliminada2.setVisible(false);
        campoEliminada1.setVisible(false);
        campoEliminada2.setVisible(false);

        etiquetaEliminada1.setEnabled(false);
        etiquetaEliminada2.setEnabled(false);
        panelInterno.repaint();
        panelInterno.revalidate();
    }

    public void intercambio(int a, int b) {
        String aux = tablaPosiciones[a];
        tablaPosiciones[a] = tablaPosiciones[b];
        tablaPosiciones[b] = aux;
    }

    public void ordenamiento(int max) {
        int puntuaciones[] = new int[10];
        int rondas[] = new int[10];
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max-1; j++) {
                String[] aux = tablaPosiciones[j].split("\\r?\\:");
                puntuaciones[j] = Integer.parseInt(aux[3].trim());
                rondas[j] = Integer.parseInt(aux[2].substring(0, aux[2].indexOf(" Premio")).trim());
                if (puntuaciones[j+1] < puntuaciones[j]) {
                    intercambio(j, j + 1);
                } else if (puntuaciones[j] == puntuaciones[j + 1] && rondas[j+1] < rondas[j]) {
                    intercambio(j, j + 1);
                }
            }
        }
    }

    public void mejoresPuntuaciones(String linea) {
        String[] aux = tablaPosiciones[0].split("\\r?\\:");
        String[] aux2 = linea.split("\\r?\\:");
        int valor = Integer.parseInt(aux2[3].trim());
        int puntaje = Integer.parseInt(aux[3].trim());
        int rondaActual = Integer.parseInt(aux2[2].substring(0, aux2[2].indexOf(" Premio")).trim());
        int rondaPrevia = Integer.parseInt(aux[2].substring(0, aux[2].indexOf(" Premio")).trim());
        if (valor > puntaje) {
            tablaPosiciones[0] = linea;
            ordenamiento(10);
        } else if (valor == puntaje && rondaActual > rondaPrevia) {
            tablaPosiciones[0] = linea;
            ordenamiento(10);
        }
    }

    public void mostrarTabla() {
        String ubicacion = UBICACION + "archivos" + BARRA + "resultados.txt";

        File archivo = new File(ubicacion);

        try {

            Scanner lector = new Scanner(archivo);

            int i = 0;
            while (lector.hasNextLine()) {
                String linea = lector.nextLine();
                if (i < 10) {
                    tablaPosiciones[i] = linea;
                    ordenamiento(i);
                } else {
                    mejoresPuntuaciones(linea);
                }
                i++;

            }
            lector.close();
            panelMenu.setVisible(false);
            panelPreguntas.setVisible(false);
            panelClasificacion.setVisible(true);
            String texto1 = "<html><p>";
            String texto2 = "<html><p>";
            String texto3 = "<html><p>";
            int a;
            if (i < 9) {
                a = i;
            } else {
                a = 9;
            }
            for (int k = a; k >= 0; k--) {
                String[] aux = tablaPosiciones[k].split("\\r?\\:");
                texto1 += aux[1].substring(0, aux[1].indexOf(" Ronda")) + "<br>";
                texto2 += aux[2].substring(0, aux[2].indexOf(" Premio")) + "<br>";
                texto3 += aux[3].trim() + "<br>";
                System.out.println(tablaPosiciones[k]);
            }
            texto1 += "</p></html>";
            texto2 += "</p></html>";
            texto3 += "</p></html>";
            users.setText(texto1);
            rondas.setText(texto2);
            jLabel10.setText(texto3);

        } catch (Exception e) {
            System.out.println("Error al leer los resultados" + e.getMessage());
        }

    }

    public void historialPartida() {
        String ubicacion = UBICACION + "archivos" + BARRA + "resultados.txt";

        try {
            File archivoNuevo = new File(ubicacion);
            try (PrintWriter escritor = new PrintWriter(new FileWriter(archivoNuevo, true))) {
                escritor.println("Nombre: " + userActual.getNombre() + " Ronda:" + userActual.getRonda() + " Premio: " + userActual.getPuntajeMaximo());
            }
        } catch (Exception ex) {
            System.out.println("Error de registro" + ex.getMessage());
        }
    }

    public void llenarCamposEliminados() {
        if (etiquetaEliminada1 != null) {
            JPanel panelInterno = (JPanel) panelPadre.getComponent(1);

            etiquetaEliminada1.setVisible(true);
            etiquetaEliminada2.setVisible(true);

            campoEliminada1.setVisible(true);
            campoEliminada2.setVisible(true);

            etiquetaEliminada1.setEnabled(true);
            etiquetaEliminada2.setEnabled(true);

            etiquetaEliminada1 = null;
            panelInterno.repaint();
            panelInterno.revalidate();
        }
    }

    public void bases() {
        if (userActual.getRonda() > 5) {
            userActual.setPuntajeMaximo(1000);
        } else if (userActual.getRonda() > 10) {
            userActual.setPuntajeMaximo(10000);
        } else {
            userActual.setPuntajeMaximo(0);
        }
    }

    public void respuestaPregunta(Pregunta preguntaActual, int eleccion, Usuario user) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(Interfaz.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (preguntaActual.getOpcionCorrecta() == eleccion) {
            int i = posicionEnArreglo(preguntaActual).x;
            int j = posicionEnArreglo(preguntaActual).y;

            user.avanzarRonda();
            actualizarPuntuacion();

            if (user.getRonda() == 15) {
                user.setPuntajeMaximo(1000000);
                historialPartida();
                cardLayout.show(panelPadreLayout, "cardResultado");
                darResultados();
            } else {
                actualizarPregunta(user);
                arregloPreguntas[i][j].setTextoPregunta(null);
            }
        } else {
            bases();
            historialPartida();
            darResultados();
            cardLayout.show(panelPadreLayout, "cardResultado");
            user.reiniciarRonda();
        }
    }

    public void darResultados() {
        labelDinero.setText(userActual.getPuntajeMaximo() + "$");
    }

    public void llenarLabelsNuevaPregunta(Pregunta preguntaActual) {
        campoPregunta.setText(preguntaActual.getTextoPregunta());
        campoOpcion1.setText(preguntaActual.getOpcion(0));
        campoOpcion2.setText(preguntaActual.getOpcion(1));
        campoOpcion3.setText(preguntaActual.getOpcion(2));
        campoOpcion4.setText(preguntaActual.getOpcion(3));

        if (!"null".equals(preguntaActual.getImagen())) {
            imgPregunta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/" + preguntaActual.getImagen())));
        } else {
            imgPregunta.setIcon(null);
        }

        llenarCamposEliminados();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelPadre = new javax.swing.JPanel();
        panelMenu = new javax.swing.JPanel();
        titulo = new javax.swing.JLabel();
        labelLuz1 = new javax.swing.JLabel();
        labelLuz2 = new javax.swing.JLabel();
        labelLuz3 = new javax.swing.JLabel();
        botComoJugar = new javax.swing.JButton();
        labelComoJugar = new javax.swing.JLabel();
        botScoreboard = new javax.swing.JButton();
        labelMejoresPuntuaciones = new javax.swing.JLabel();
        botNuevaPartida = new javax.swing.JButton();
        labelFondoMenu = new javax.swing.JLabel();
        panelPreguntas = new javax.swing.JPanel();
        campoPregunta = new javax.swing.JTextPane();
        labelFondoPregunta = new javax.swing.JLabel();
        campoOpcion1 = new javax.swing.JTextField();
        campoOpcion2 = new javax.swing.JTextField();
        campoOpcion3 = new javax.swing.JTextField();
        campoOpcion4 = new javax.swing.JTextField();
        labelOpcionAux1 = new javax.swing.JLabel();
        labelOpcionAux2 = new javax.swing.JLabel();
        labelOpcionAux3 = new javax.swing.JLabel();
        labelOpcionAux4 = new javax.swing.JLabel();
        labelOpcion1 = new javax.swing.JLabel();
        labelOpcion2 = new javax.swing.JLabel();
        labelOpcion3 = new javax.swing.JLabel();
        labelOpcion4 = new javax.swing.JLabel();
        imgPregunta = new javax.swing.JLabel();
        flecha1 = new javax.swing.JLabel();
        flecha2 = new javax.swing.JLabel();
        flecha3 = new javax.swing.JLabel();
        flecha4 = new javax.swing.JLabel();
        flecha5 = new javax.swing.JLabel();
        flecha6 = new javax.swing.JLabel();
        flecha7 = new javax.swing.JLabel();
        flecha8 = new javax.swing.JLabel();
        flecha9 = new javax.swing.JLabel();
        flecha10 = new javax.swing.JLabel();
        flecha11 = new javax.swing.JLabel();
        flecha12 = new javax.swing.JLabel();
        flecha13 = new javax.swing.JLabel();
        flecha14 = new javax.swing.JLabel();
        flecha15 = new javax.swing.JLabel();
        campoPuntuaciones3 = new javax.swing.JTextArea();
        campoPuntuaciones1 = new javax.swing.JTextArea();
        campoPuntuaciones4 = new javax.swing.JTextArea();
        campoPuntuaciones2 = new javax.swing.JTextArea();
        botCambioPregunta = new javax.swing.JButton();
        botComodin5050 = new javax.swing.JButton();
        botAbandonar = new javax.swing.JButton();
        labelPaleta = new javax.swing.JLabel();
        labelFondoAtril = new javax.swing.JLabel();
        labelFondoPreguntas = new javax.swing.JLabel();
        panelClasificacion = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labelUsuario = new javax.swing.JLabel();
        labelRonda = new javax.swing.JLabel();
        labelPremio = new javax.swing.JLabel();
        users = new javax.swing.JLabel();
        rondas = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        labelFondoMenu1 = new javax.swing.JLabel();
        panelResultado = new javax.swing.JPanel();
        labelTeHasRetiradoCon = new javax.swing.JLabel();
        labelDinero = new javax.swing.JLabel();
        botVolverAlMenu = new javax.swing.JButton();
        botOtraPartida = new javax.swing.JButton();
        labelAtril = new javax.swing.JLabel();
        labelFondoResultado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quien quiere ser millionario: Edicion Artistica");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/imagenes/iconoJuego.png")));
        setMinimumSize(new java.awt.Dimension(1280, 720));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelPadre.setMaximumSize(new java.awt.Dimension(1280, 720));
        panelPadre.setMinimumSize(new java.awt.Dimension(1280, 720));
        panelPadre.setPreferredSize(new java.awt.Dimension(1280, 720));
        panelPadre.setLayout(new java.awt.CardLayout());

        panelMenu.setBackground(new java.awt.Color(255, 255, 255));
        panelMenu.setMaximumSize(new java.awt.Dimension(1280, 720));
        panelMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titulo.setFont(new java.awt.Font("Montserrat", 0, 48)); // NOI18N
        titulo.setForeground(new java.awt.Color(255, 255, 255));
        titulo.setText("Quien Quiere Ser Millionario Edicion Arte ");
        panelMenu.add(titulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, -1, -1));

        labelLuz1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelMenu.add(labelLuz1, new org.netbeans.lib.awtextra.AbsoluteConstraints(-176, 200, -1, -1));

        labelLuz2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelMenu.add(labelLuz2, new org.netbeans.lib.awtextra.AbsoluteConstraints(128, 200, -1, -1));

        labelLuz3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        panelMenu.add(labelLuz3, new org.netbeans.lib.awtextra.AbsoluteConstraints(604, 200, -1, -1));

        botComoJugar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/botComoJugar.png"))); // NOI18N
        botComoJugar.setBorderPainted(false);
        botComoJugar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botComoJugar.setFocusPainted(false);
        botComoJugar.setFocusable(false);
        botComoJugar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botComoJugarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botComoJugarMouseExited(evt);
            }
        });
        botComoJugar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botComoJugarActionPerformed(evt);
            }
        });
        panelMenu.add(botComoJugar, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 240, -1, -1));

        labelComoJugar.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        labelComoJugar.setForeground(new java.awt.Color(255, 255, 255));
        labelComoJugar.setText("Como Jugar");
        panelMenu.add(labelComoJugar, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 530, -1, -1));

        botScoreboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/botScoreboard.png"))); // NOI18N
        botScoreboard.setBorderPainted(false);
        botScoreboard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botScoreboard.setFocusPainted(false);
        botScoreboard.setFocusable(false);
        botScoreboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botScoreboardMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botScoreboardMouseExited(evt);
            }
        });
        botScoreboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botScoreboardActionPerformed(evt);
            }
        });
        panelMenu.add(botScoreboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 240, -1, -1));

        labelMejoresPuntuaciones.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        labelMejoresPuntuaciones.setForeground(new java.awt.Color(255, 255, 255));
        labelMejoresPuntuaciones.setText("Mejores puntuaciones");
        panelMenu.add(labelMejoresPuntuaciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 530, -1, -1));

        botNuevaPartida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/botonNuevaPartida.png"))); // NOI18N
        botNuevaPartida.setBorderPainted(false);
        botNuevaPartida.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botNuevaPartida.setFocusPainted(false);
        botNuevaPartida.setFocusable(false);
        botNuevaPartida.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botNuevaPartidaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botNuevaPartidaMouseExited(evt);
            }
        });
        botNuevaPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botNuevaPartidaActionPerformed(evt);
            }
        });
        panelMenu.add(botNuevaPartida, new org.netbeans.lib.awtextra.AbsoluteConstraints(655, 240, -1, -1));

        labelFondoMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoMenu.png"))); // NOI18N
        panelMenu.add(labelFondoMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        panelPadre.add(panelMenu, "cardMenu");

        panelPreguntas.setBackground(new java.awt.Color(255, 255, 255));
        panelPreguntas.setMaximumSize(new java.awt.Dimension(1280, 720));
        panelPreguntas.setMinimumSize(new java.awt.Dimension(1280, 720));
        panelPreguntas.setName(""); // NOI18N
        panelPreguntas.setPreferredSize(new java.awt.Dimension(1280, 720));
        panelPreguntas.setRequestFocusEnabled(false);
        panelPreguntas.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        campoPregunta.setEditable(false);
        campoPregunta.setFont(new java.awt.Font("Montserrat", 0, 20)); // NOI18N
        campoPregunta.setForeground(new java.awt.Color(255, 255, 255));
        campoPregunta.setOpaque(false);
        panelPreguntas.add(campoPregunta, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 420, 730, 70));

        labelFondoPregunta.setBackground(new java.awt.Color(255, 255, 255));
        labelFondoPregunta.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelFondoPregunta.setForeground(new java.awt.Color(255, 255, 255));
        labelFondoPregunta.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelFondoPregunta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoPregunta.png"))); // NOI18N
        panelPreguntas.add(labelFondoPregunta, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 410, -1, 90));

        campoOpcion1.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        campoOpcion1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campoOpcion1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        campoOpcion1.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        campoOpcion1.setEnabled(false);
        campoOpcion1.setOpaque(false);
        campoOpcion1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opcion1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                opcion1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opcion1MouseExited(evt);
            }
        });
        campoOpcion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoOpcion1ActionPerformed(evt);
            }
        });
        panelPreguntas.add(campoOpcion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 530, 380, 40));

        campoOpcion2.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        campoOpcion2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campoOpcion2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        campoOpcion2.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        campoOpcion2.setEnabled(false);
        campoOpcion2.setOpaque(false);
        campoOpcion2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opcion2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                opcion2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opcion2MouseExited(evt);
            }
        });
        panelPreguntas.add(campoOpcion2, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 530, 380, 40));

        campoOpcion3.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        campoOpcion3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campoOpcion3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        campoOpcion3.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        campoOpcion3.setEnabled(false);
        campoOpcion3.setOpaque(false);
        campoOpcion3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opcion3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                opcion3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opcion3MouseExited(evt);
            }
        });
        panelPreguntas.add(campoOpcion3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 630, 380, 40));

        campoOpcion4.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        campoOpcion4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        campoOpcion4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        campoOpcion4.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        campoOpcion4.setEnabled(false);
        campoOpcion4.setOpaque(false);
        campoOpcion4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opcion4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ocion4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opcion4MouseExited(evt);
            }
        });
        panelPreguntas.add(campoOpcion4, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 630, 380, 40));

        labelOpcionAux1.setBackground(new java.awt.Color(255, 255, 255));
        labelOpcionAux1.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelOpcionAux1.setForeground(new java.awt.Color(255, 255, 255));
        labelOpcionAux1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOpcionAux1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelOpcionAux1opcion1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelOpcionAux1opcion1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelOpcionAux1opcion1MouseExited(evt);
            }
        });
        panelPreguntas.add(labelOpcionAux1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 510, -1, -1));

        labelOpcionAux2.setBackground(new java.awt.Color(255, 255, 255));
        labelOpcionAux2.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelOpcionAux2.setForeground(new java.awt.Color(255, 255, 255));
        labelOpcionAux2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOpcionAux2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelOpcionAux2opcion2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelOpcionAux2opcion2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelOpcionAux2opcion2MouseExited(evt);
            }
        });
        panelPreguntas.add(labelOpcionAux2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 510, -1, -1));

        labelOpcionAux3.setBackground(new java.awt.Color(255, 255, 255));
        labelOpcionAux3.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelOpcionAux3.setForeground(new java.awt.Color(255, 255, 255));
        labelOpcionAux3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOpcionAux3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelOpcionAux3opcion3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelOpcionAux3opcion3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelOpcionAux3opcion3MouseExited(evt);
            }
        });
        panelPreguntas.add(labelOpcionAux3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 610, -1, -1));

        labelOpcionAux4.setBackground(new java.awt.Color(255, 255, 255));
        labelOpcionAux4.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelOpcionAux4.setForeground(new java.awt.Color(255, 255, 255));
        labelOpcionAux4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOpcionAux4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                labelOpcionAux4opcion4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                labelOpcionAux4ocion4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                labelOpcionAux4opcion4MouseExited(evt);
            }
        });
        panelPreguntas.add(labelOpcionAux4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 610, -1, -1));

        labelOpcion1.setBackground(new java.awt.Color(255, 255, 255));
        labelOpcion1.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelOpcion1.setForeground(new java.awt.Color(255, 255, 255));
        labelOpcion1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOpcion1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoOpcion_A.png"))); // NOI18N
        labelOpcion1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelOpcion1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opcion1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                opcion1MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opcion1MouseExited(evt);
            }
        });
        panelPreguntas.add(labelOpcion1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 510, 395, 76));

        labelOpcion2.setBackground(new java.awt.Color(255, 255, 255));
        labelOpcion2.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelOpcion2.setForeground(new java.awt.Color(255, 255, 255));
        labelOpcion2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOpcion2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoOpcion_A.png"))); // NOI18N
        labelOpcion2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelOpcion2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opcion2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                opcion2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opcion2MouseExited(evt);
            }
        });
        panelPreguntas.add(labelOpcion2, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 510, 395, 76));

        labelOpcion3.setBackground(new java.awt.Color(255, 255, 255));
        labelOpcion3.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelOpcion3.setForeground(new java.awt.Color(255, 255, 255));
        labelOpcion3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOpcion3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoOpcion_A.png"))); // NOI18N
        labelOpcion3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelOpcion3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opcion3MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                opcion3MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opcion3MouseExited(evt);
            }
        });
        panelPreguntas.add(labelOpcion3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 610, 395, 76));

        labelOpcion4.setBackground(new java.awt.Color(255, 255, 255));
        labelOpcion4.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelOpcion4.setForeground(new java.awt.Color(255, 255, 255));
        labelOpcion4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelOpcion4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoOpcion_A.png"))); // NOI18N
        labelOpcion4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        labelOpcion4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opcion4MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ocion4MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opcion4MouseExited(evt);
            }
        });
        panelPreguntas.add(labelOpcion4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 610, 395, 76));
        panelPreguntas.add(imgPregunta, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 10, 341, 303));

        flecha1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 321, -1, -1));

        flecha2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 303, -1, -1));

        flecha3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 285, -1, -1));

        flecha4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 267, -1, -1));

        flecha5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha5, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 249, -1, -1));

        flecha6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha6, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 231, -1, -1));

        flecha7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha7, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 210, -1, -1));

        flecha8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha8, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 190, -1, -1));

        flecha9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 170, -1, -1));

        flecha10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha10, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 150, -1, -1));

        flecha11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha11, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 130, -1, -1));

        flecha12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha12, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 115, -1, -1));

        flecha13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha13, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 96, -1, -1));

        flecha14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 77, -1, -1));

        flecha15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaPequenia.png"))); // NOI18N
        panelPreguntas.add(flecha15, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 60, -1, -1));

        campoPuntuaciones3.setEditable(false);
        campoPuntuaciones3.setColumns(20);
        campoPuntuaciones3.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        campoPuntuaciones3.setForeground(new java.awt.Color(51, 153, 0));
        campoPuntuaciones3.setRows(5);
        campoPuntuaciones3.setText("15.\n\n\n\n\n10.\n\n\n\n\n5.");
        campoPuntuaciones3.setOpaque(false);
        panelPreguntas.add(campoPuntuaciones3, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 60, 60, 280));

        campoPuntuaciones1.setEditable(false);
        campoPuntuaciones1.setColumns(20);
        campoPuntuaciones1.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        campoPuntuaciones1.setForeground(new java.awt.Color(51, 0, 102));
        campoPuntuaciones1.setRows(5);
        campoPuntuaciones1.setText("\n14.\n13.\n12.\n11.\n\n9.\n8.\n7.\n6.\n\n4.\n3.\n2.\n1.");
        panelPreguntas.add(campoPuntuaciones1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 60, 60, 280));

        campoPuntuaciones4.setEditable(false);
        campoPuntuaciones4.setColumns(20);
        campoPuntuaciones4.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        campoPuntuaciones4.setForeground(new java.awt.Color(0, 204, 0));
        campoPuntuaciones4.setRows(5);
        campoPuntuaciones4.setText("1000000\n\n\n\n\n10000\n\n\n\n\n1000\n");
        campoPuntuaciones4.setOpaque(false);
        panelPreguntas.add(campoPuntuaciones4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 60, 100, 280));

        campoPuntuaciones2.setEditable(false);
        campoPuntuaciones2.setColumns(20);
        campoPuntuaciones2.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        campoPuntuaciones2.setRows(5);
        campoPuntuaciones2.setText("1000000\n300000\n100000\n30000\n15000\n10000\n8000\n6000\n4000\n2000\n1000\n500\n300\n200\n100");
        panelPreguntas.add(campoPuntuaciones2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 60, 100, 280));

        botCambioPregunta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/comodinCambio_A.png"))); // NOI18N
        botCambioPregunta.setContentAreaFilled(false);
        botCambioPregunta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botCambioPregunta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botCambioPreguntaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botCambioPreguntaMouseExited(evt);
            }
        });
        botCambioPregunta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botCambioPreguntaActionPerformed(evt);
            }
        });
        panelPreguntas.add(botCambioPregunta, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 510, -1, -1));

        botComodin5050.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/comodin5050_A.png"))); // NOI18N
        botComodin5050.setContentAreaFilled(false);
        botComodin5050.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botComodin5050.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botComodin5050MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botComodin5050MouseExited(evt);
            }
        });
        botComodin5050.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botComodin5050ActionPerformed(evt);
            }
        });
        panelPreguntas.add(botComodin5050, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 580, -1, -1));

        botAbandonar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/abandonarPartida_A.png"))); // NOI18N
        botAbandonar.setContentAreaFilled(false);
        botAbandonar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botAbandonar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botAbandonarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botAbandonarMouseExited(evt);
            }
        });
        botAbandonar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botAbandonarActionPerformed(evt);
            }
        });
        panelPreguntas.add(botAbandonar, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 560, -1, -1));

        labelPaleta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/paletaComodines.png"))); // NOI18N
        panelPreguntas.add(labelPaleta, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 490, -1, -1));

        labelFondoAtril.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/atrilPuntuaciones.png"))); // NOI18N
        panelPreguntas.add(labelFondoAtril, new org.netbeans.lib.awtextra.AbsoluteConstraints(940, 10, -1, -1));

        labelFondoPreguntas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoPrincipal.png"))); // NOI18N
        panelPreguntas.add(labelFondoPreguntas, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        panelPadre.add(panelPreguntas, "cardPrincipal");

        panelClasificacion.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/flechaRetroceder.png"))); // NOI18N
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });
        panelClasificacion.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 30, -1, -1));

        labelUsuario.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelUsuario.setText("Usuario");
        panelClasificacion.add(labelUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 190, -1, -1));

        labelRonda.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelRonda.setText("Ronda");
        panelClasificacion.add(labelRonda, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 190, -1, -1));

        labelPremio.setFont(new java.awt.Font("Montserrat", 1, 18)); // NOI18N
        labelPremio.setText("Premio");
        panelClasificacion.add(labelPremio, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 190, -1, -1));

        users.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        panelClasificacion.add(users, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 250, 250, -1));

        rondas.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        panelClasificacion.add(rondas, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 250, -1, -1));

        jLabel10.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        panelClasificacion.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 250, -1, -1));

        labelFondoMenu1.setFont(new java.awt.Font("Montserrat", 0, 18)); // NOI18N
        labelFondoMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoClasificacion.png"))); // NOI18N
        panelClasificacion.add(labelFondoMenu1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        panelPadre.add(panelClasificacion, "cardClasificacion");

        panelResultado.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        labelTeHasRetiradoCon.setFont(new java.awt.Font("Montserrat", 0, 48)); // NOI18N
        labelTeHasRetiradoCon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelTeHasRetiradoCon.setText("Te has retirado con:");
        panelResultado.add(labelTeHasRetiradoCon, new org.netbeans.lib.awtextra.AbsoluteConstraints(352, 40, 580, -1));

        labelDinero.setFont(new java.awt.Font("Montserrat", 0, 100)); // NOI18N
        labelDinero.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDinero.setToolTipText("");
        panelResultado.add(labelDinero, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 160, 580, -1));

        botVolverAlMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/botVolverAlMenu_A.png"))); // NOI18N
        botVolverAlMenu.setBorderPainted(false);
        botVolverAlMenu.setContentAreaFilled(false);
        botVolverAlMenu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botVolverAlMenu.setFocusable(false);
        botVolverAlMenu.setRequestFocusEnabled(false);
        botVolverAlMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botVolverAlMenuMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botVolverAlMenuMouseExited(evt);
            }
        });
        botVolverAlMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botVolverAlMenuActionPerformed(evt);
            }
        });
        panelResultado.add(botVolverAlMenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 350, -1, -1));

        botOtraPartida.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/botOtraPartida_A.png"))); // NOI18N
        botOtraPartida.setBorderPainted(false);
        botOtraPartida.setContentAreaFilled(false);
        botOtraPartida.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botOtraPartida.setFocusable(false);
        botOtraPartida.setRequestFocusEnabled(false);
        botOtraPartida.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botOtraPartidaMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botOtraPartidaMouseExited(evt);
            }
        });
        botOtraPartida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botOtraPartidaActionPerformed(evt);
            }
        });
        panelResultado.add(botOtraPartida, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 350, -1, -1));

        labelAtril.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelAtril.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/atrilGrande.png"))); // NOI18N
        panelResultado.add(labelAtril, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, -100, -1, -1));

        labelFondoResultado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/fondoPrincipal.png"))); // NOI18N
        panelResultado.add(labelFondoResultado, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        panelPadre.add(panelResultado, "cardResultado");

        getContentPane().add(panelPadre, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void botNuevaPartidaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botNuevaPartidaMouseEntered
        labelLuz3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes" + BARRA + "luz.png")));
    }//GEN-LAST:event_botNuevaPartidaMouseEntered

    private void botNuevaPartidaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botNuevaPartidaMouseExited
        labelLuz3.setIcon(null);
    }//GEN-LAST:event_botNuevaPartidaMouseExited

    private void botNuevaPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botNuevaPartidaActionPerformed
        DialogPeticionNombre dialog = new DialogPeticionNombre(this, true);
        dialog.setVisible(true);
        userActual = new Usuario(nombreUsuario);
        partidaNueva(userActual);
    }//GEN-LAST:event_botNuevaPartidaActionPerformed

    private void botComodin5050MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botComodin5050MouseEntered
        botComodin5050.setIcon(obtenerImagen("comodin5050_B.png"));
    }//GEN-LAST:event_botComodin5050MouseEntered

    private void botComodin5050MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botComodin5050MouseExited
        botComodin5050.setIcon(obtenerImagen("comodin5050_A.png"));

    }//GEN-LAST:event_botComodin5050MouseExited

    private void botCambioPreguntaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botCambioPreguntaMouseExited
        botCambioPregunta.setIcon(obtenerImagen("comodinCambio_A.png"));
    }//GEN-LAST:event_botCambioPreguntaMouseExited

    private void botCambioPreguntaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botCambioPreguntaMouseEntered
        botCambioPregunta.setIcon(obtenerImagen("comodinCambio_B.png"));
    }//GEN-LAST:event_botCambioPreguntaMouseEntered

    private void botAbandonarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botAbandonarMouseExited
        botAbandonar.setIcon(obtenerImagen("abandonarPartida_A.png"));
    }//GEN-LAST:event_botAbandonarMouseExited

    private void botAbandonarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botAbandonarMouseEntered
        botAbandonar.setIcon(obtenerImagen("abandonarPartida_B.png"));
    }//GEN-LAST:event_botAbandonarMouseEntered

    private void botComodin5050ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botComodin5050ActionPerformed
        if (userActual.getValorCincuentaCincuenta()) {
            cincuentaCincuenta();
            userActual.setValorCincuentaCincuenta(false);
            botComodin5050.setVisible(false);
        }
    }//GEN-LAST:event_botComodin5050ActionPerformed

    private void botCambioPreguntaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botCambioPreguntaActionPerformed
        if (userActual.getCambiarPregunta()) {
            preguntaActual.setTextoPregunta(null);;
            actualizarPregunta(userActual);
            userActual.setCambiarPregunta(false);
            botCambioPregunta.setVisible(false);
        }

    }//GEN-LAST:event_botCambioPreguntaActionPerformed

    private void botAbandonarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botAbandonarActionPerformed
        userActual.setPuntajeMaximo(puntuacion(userActual.getRonda()));
        historialPartida();
        darResultados();
        cardLayout.show(panelPadreLayout, "cardResultado");
        userActual.iniciarPartida();
    }//GEN-LAST:event_botAbandonarActionPerformed

    private void opcion1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion1MouseClicked
        respuestaPregunta(preguntaActual, 1, userActual);
    }//GEN-LAST:event_opcion1MouseClicked

    private void opcion2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion2MouseClicked
        respuestaPregunta(preguntaActual, 2, userActual);
    }//GEN-LAST:event_opcion2MouseClicked

    private void opcion3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion3MouseClicked
        respuestaPregunta(preguntaActual, 3, userActual);
    }//GEN-LAST:event_opcion3MouseClicked

    private void opcion4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion4MouseClicked
        respuestaPregunta(preguntaActual, 4, userActual);
    }//GEN-LAST:event_opcion4MouseClicked

    private void botComoJugarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botComoJugarMouseEntered
        labelLuz1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes" + BARRA + "luz.png")));
        labelComoJugar.setVisible(true);
    }//GEN-LAST:event_botComoJugarMouseEntered

    private void botComoJugarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botComoJugarMouseExited
        labelLuz1.setIcon(null);
        labelComoJugar.setVisible(false);
    }//GEN-LAST:event_botComoJugarMouseExited

    private void botScoreboardMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botScoreboardMouseEntered
        labelLuz2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes" + BARRA + "luz.png")));
        labelMejoresPuntuaciones.setVisible(true);
    }//GEN-LAST:event_botScoreboardMouseEntered

    private void botScoreboardMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botScoreboardMouseExited
        labelLuz2.setIcon(null);
        labelMejoresPuntuaciones.setVisible(false);
    }//GEN-LAST:event_botScoreboardMouseExited

    private void botVolverAlMenuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botVolverAlMenuMouseEntered
        botVolverAlMenu.setIcon(obtenerImagen("botVolverAlMenu_B.png"));
    }//GEN-LAST:event_botVolverAlMenuMouseEntered

    private void botVolverAlMenuMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botVolverAlMenuMouseExited
        botVolverAlMenu.setIcon(obtenerImagen("botVolverAlMenu_A.png"));
    }//GEN-LAST:event_botVolverAlMenuMouseExited

    private void botOtraPartidaMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botOtraPartidaMouseEntered
        botOtraPartida.setIcon(obtenerImagen("botOtraPartida_B.png"));
    }//GEN-LAST:event_botOtraPartidaMouseEntered

    private void botOtraPartidaMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_botOtraPartidaMouseExited
        botOtraPartida.setIcon(obtenerImagen("botOtraPartida_A.png"));
    }//GEN-LAST:event_botOtraPartidaMouseExited

    private void botVolverAlMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botVolverAlMenuActionPerformed
        cardLayout.show(panelPadreLayout, "cardMenu");
    }//GEN-LAST:event_botVolverAlMenuActionPerformed

    private void botOtraPartidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botOtraPartidaActionPerformed
        partidaNueva(userActual);
        cardLayout.show(panelPadreLayout, "cardPrincipal");
    }//GEN-LAST:event_botOtraPartidaActionPerformed

    private void botScoreboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botScoreboardActionPerformed
        mostrarTabla();
        cardLayout.show(panelPadreLayout, "cardClasificacion");
    }//GEN-LAST:event_botScoreboardActionPerformed

    private void opcion1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion1MouseEntered
        labelOpcion1.setIcon(obtenerImagen("fondoOpcion_B.png"));
    }//GEN-LAST:event_opcion1MouseEntered

    private void opcion1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion1MouseExited
        labelOpcion1.setIcon(obtenerImagen("fondoOpcion_A.png"));
    }//GEN-LAST:event_opcion1MouseExited

    private void opcion2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion2MouseEntered
        labelOpcion2.setIcon(obtenerImagen("fondoOpcion_B.png"));
    }//GEN-LAST:event_opcion2MouseEntered

    private void opcion2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion2MouseExited
        labelOpcion2.setIcon(obtenerImagen("fondoOpcion_A.png"));
    }//GEN-LAST:event_opcion2MouseExited

    private void opcion3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion3MouseEntered
        labelOpcion3.setIcon(obtenerImagen("fondoOpcion_B.png"));
    }//GEN-LAST:event_opcion3MouseEntered

    private void opcion3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion3MouseExited
        labelOpcion3.setIcon(obtenerImagen("fondoOpcion_A.png"));
    }//GEN-LAST:event_opcion3MouseExited

    private void ocion4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ocion4MouseEntered
        labelOpcion4.setIcon(obtenerImagen("fondoOpcion_B.png"));
    }//GEN-LAST:event_ocion4MouseEntered

    private void opcion4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opcion4MouseExited
        labelOpcion4.setIcon(obtenerImagen("fondoOpcion_A.png"));
    }//GEN-LAST:event_opcion4MouseExited

    private void campoOpcion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoOpcion1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoOpcion1ActionPerformed

    private void labelOpcionAux1opcion1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux1opcion1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux1opcion1MouseClicked

    private void labelOpcionAux1opcion1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux1opcion1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux1opcion1MouseEntered

    private void labelOpcionAux1opcion1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux1opcion1MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux1opcion1MouseExited

    private void labelOpcionAux2opcion2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux2opcion2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux2opcion2MouseClicked

    private void labelOpcionAux2opcion2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux2opcion2MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux2opcion2MouseEntered

    private void labelOpcionAux2opcion2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux2opcion2MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux2opcion2MouseExited

    private void labelOpcionAux3opcion3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux3opcion3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux3opcion3MouseClicked

    private void labelOpcionAux3opcion3MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux3opcion3MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux3opcion3MouseEntered

    private void labelOpcionAux3opcion3MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux3opcion3MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux3opcion3MouseExited

    private void labelOpcionAux4opcion4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux4opcion4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux4opcion4MouseClicked

    private void labelOpcionAux4ocion4MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux4ocion4MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux4ocion4MouseEntered

    private void labelOpcionAux4opcion4MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_labelOpcionAux4opcion4MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_labelOpcionAux4opcion4MouseExited

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        cardLayout.show(panelPadreLayout, "cardMenu");
    }//GEN-LAST:event_jLabel1MouseClicked

    private void botComoJugarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botComoJugarActionPerformed
        File myFile = new File(UBICACION + "archivos" + BARRA + "ComoJugarQQSM.pdf");
        try {
            Desktop.getDesktop().open(myFile);
        } catch (IOException ex) {
            System.out.println("Error con el manejo de archivo");
        }
    }//GEN-LAST:event_botComoJugarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Interfaz().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botAbandonar;
    private javax.swing.JButton botCambioPregunta;
    private javax.swing.JButton botComoJugar;
    private javax.swing.JButton botComodin5050;
    private javax.swing.JButton botNuevaPartida;
    private javax.swing.JButton botOtraPartida;
    private javax.swing.JButton botScoreboard;
    private javax.swing.JButton botVolverAlMenu;
    private javax.swing.JTextField campoOpcion1;
    private javax.swing.JTextField campoOpcion2;
    private javax.swing.JTextField campoOpcion3;
    private javax.swing.JTextField campoOpcion4;
    private javax.swing.JTextPane campoPregunta;
    private javax.swing.JTextArea campoPuntuaciones1;
    private javax.swing.JTextArea campoPuntuaciones2;
    private javax.swing.JTextArea campoPuntuaciones3;
    private javax.swing.JTextArea campoPuntuaciones4;
    private javax.swing.JLabel flecha1;
    private javax.swing.JLabel flecha10;
    private javax.swing.JLabel flecha11;
    private javax.swing.JLabel flecha12;
    private javax.swing.JLabel flecha13;
    private javax.swing.JLabel flecha14;
    private javax.swing.JLabel flecha15;
    private javax.swing.JLabel flecha2;
    private javax.swing.JLabel flecha3;
    private javax.swing.JLabel flecha4;
    private javax.swing.JLabel flecha5;
    private javax.swing.JLabel flecha6;
    private javax.swing.JLabel flecha7;
    private javax.swing.JLabel flecha8;
    private javax.swing.JLabel flecha9;
    private javax.swing.JLabel imgPregunta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel labelAtril;
    private javax.swing.JLabel labelComoJugar;
    private javax.swing.JLabel labelDinero;
    private javax.swing.JLabel labelFondoAtril;
    private javax.swing.JLabel labelFondoMenu;
    private javax.swing.JLabel labelFondoMenu1;
    private javax.swing.JLabel labelFondoPregunta;
    private javax.swing.JLabel labelFondoPreguntas;
    private javax.swing.JLabel labelFondoResultado;
    private javax.swing.JLabel labelLuz1;
    private javax.swing.JLabel labelLuz2;
    private javax.swing.JLabel labelLuz3;
    private javax.swing.JLabel labelMejoresPuntuaciones;
    private javax.swing.JLabel labelOpcion1;
    private javax.swing.JLabel labelOpcion2;
    private javax.swing.JLabel labelOpcion3;
    private javax.swing.JLabel labelOpcion4;
    private javax.swing.JLabel labelOpcionAux1;
    private javax.swing.JLabel labelOpcionAux2;
    private javax.swing.JLabel labelOpcionAux3;
    private javax.swing.JLabel labelOpcionAux4;
    private javax.swing.JLabel labelPaleta;
    private javax.swing.JLabel labelPremio;
    private javax.swing.JLabel labelRonda;
    private javax.swing.JLabel labelTeHasRetiradoCon;
    private javax.swing.JLabel labelUsuario;
    private javax.swing.JPanel panelClasificacion;
    private javax.swing.JPanel panelMenu;
    private javax.swing.JPanel panelPadre;
    private javax.swing.JPanel panelPreguntas;
    private javax.swing.JPanel panelResultado;
    private javax.swing.JLabel rondas;
    private javax.swing.JLabel titulo;
    private javax.swing.JLabel users;
    // End of variables declaration//GEN-END:variables
}
