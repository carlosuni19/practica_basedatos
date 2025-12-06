import java.sql.*;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class SensoresBDD {
    // Parámetros de conexión
    private Connection myconnection;
    // Se conecta a la base de datos de oracle
    public void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            System.err.println("Error al cargar el driver de Oracle: " + e.getMessage());
        }
        try {
            myconnection = DriverManager.getConnection(
                "jdbc:oracle:thin:@danae04.unizar.es:1521:barret","a818193","Dorsy240");
            System.out.println("Conexion exitosa a la base de datos.");
        } catch (Exception e) {
            System.err.println("Error de conexion: " + e.getMessage());
        }
    }
/**
     * Recupera todos los registros de la tabla 'sensores'.
     * @return Una lista de objetos Sensor.
     */
    public List<Sensor> obtenerTodos() {
        List<Sensor> sensores = new ArrayList<>();
        // Sentencia SQL para obtener todos los campos
        String sql = "SELECT nombre, tipo, valor FROM sensores";

        try {
            Statement stmt = myconnection.createStatement();
            ResultSet theSet = stmt.executeQuery(sql);
            // Iterar sobre cada fila (registro) devuelta por la base de datos
            while (theSet.next()) {
                // Crear un nuevo objeto Sensor y llenar sus atributos
                Sensor sensor = new Sensor(
                    theSet.getString("nombre"),
                    theSet.getString("tipo"),
                    theSet.getDouble("valor")
                );
                sensores.add(sensor);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar los sensores: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return sensores;
    }

    /**
     * Inserta un nuevo registro de sensor en la base de datos.
     * @param sensor Objeto Sensor con los datos a guardar.
     * @return true si la inserción fue exitosa, false en caso contrario.
     */
    public boolean insertar(Sensor sensor) {
        // 1. crear la sentencia SQL
        String sql = "INSERT INTO sensores (nombre, tipo, valor) VALUES (?, ?, ?)";
        try {
            // 1. Crear el objeto Statement
            PreparedStatement stmt = myconnection.prepareStatement(sql);
            // 2. Ejecutar la sentencia. executeUpdate() devuelve el número de filas afectadas.
            stmt.setString(1, sensor.getNombre());
            stmt.setString(2, sensor.getTipo());
            stmt.setDouble(3, sensor.getValor());
            
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
            // 3. Cerrar el Statement

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al insertar el sensor: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    public Sensor obtenerPorclave(String nombre) {
        Sensor sensor = null;
        String sql = "SELECT nombre, tipo, valor FROM sensores WHERE nombre = ?";

        if (myconnection == null) {
            System.out.println("No hay conexion a la base de datos.");
            return null;
        }

        try {
            PreparedStatement stmt = myconnection.prepareStatement(sql);
            stmt.setString(1, nombre);
            ResultSet theSet = stmt.executeQuery();

            if (theSet.next()) {
                sensor = new Sensor(
                    theSet.getString("nombre"),
                    theSet.getString("tipo"),
                    theSet.getDouble("valor")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el sensor: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return sensor;
    }

    public boolean borrarPorClave(String nombre) {
        String sql = "DELETE FROM sensores WHERE nombre = ?";

        if (myconnection == null) {
            System.out.println("No hay conexion a la base de datos.");
            return false;
        }

        try {
            PreparedStatement stmt = myconnection.prepareStatement(sql);
            stmt.setString(1, nombre);
            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al borrar el sensor: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

        /**
     * Actualiza el tipo y el valor de un sensor existente, identificado por su nombre (PK).
     * @param sensor El objeto Sensor con los nuevos datos y el nombre original.
     * @return true si se actualizó una fila, false en caso contrario.
     */
    public boolean actualizar(Sensor sensor) {
        // La sentencia SQL utiliza el nombre (PK) en el WHERE para saber qué fila actualizar.
        String sql = "UPDATE sensores SET tipo = ?, valor = ? WHERE nombre = ?";
        
        if (myconnection == null) {
            System.err.println("Error: Conexión nula en actualizar.");
            return false;
        }

        try (PreparedStatement pstmt = myconnection.prepareStatement(sql)) {
            
            // 1. Asignar los nuevos valores a actualizar
            pstmt.setString(1, sensor.getTipo());
            pstmt.setDouble(2, sensor.getValor());
            
            // 2. Usar el nombre del sensor (PK) para identificar la fila
            pstmt.setString(3, sensor.getNombre()); 
            
            int filasAfectadas = pstmt.executeUpdate();
            
            return filasAfectadas > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al intentar modificar el sensor: " + e.getMessage(), "Error DB", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
        }
}