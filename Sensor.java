public class Sensor {
    private String nombre; // Se asume autoincremental en la DB, no se pide al usuario
    private String tipo;
    private double valor;
    
    public Sensor(String nombre, String tipo, double valor) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public double getValor() { return valor; }
    
}