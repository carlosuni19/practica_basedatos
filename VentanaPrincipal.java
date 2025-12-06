import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    // Constructor de la ventana principal
    public VentanaPrincipal() {
        // conectar a la base de datos
        // 1. Configuración de la Ventana
        super("Gestión de Sensores - Menú Principal");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 300);
        this.setLocationRelativeTo(null); // Centrar la ventana en la pantalla

        // 2. Creación del Panel y Layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 filas, 1 columna, con espacios de 10px

        // Añadir un borde para estética
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 3. Creación de Botones
        JButton btnListar = new JButton("1. Listar Sensores");
        JButton btnCrear = new JButton("2. Crear Nuevo Sensor");
        JButton btnBorrar = new JButton("3. Borrar Sensor");
        JButton btnModificar = new JButton("4. Modificar Sensor");

        // 4. Configuración de Eventos (Listeners)
        
        // Opción 1: Listar
        btnListar.addActionListener(e -> {
            VentanaListarInterna ventanalistar = new VentanaListarInterna();
            ventanalistar.setVisible(true);
        });

        // Opción 2: Crear
        btnCrear.addActionListener(e -> {
            VentanaCrearInterna ventanacrear = new VentanaCrearInterna();
            ventanacrear.setVisible(true);
        });

        // Opción 3: Borrar
        btnBorrar.addActionListener(e -> {
            VentanaBorrarInterna ventanaborrar = new VentanaBorrarInterna();
            ventanaborrar.setVisible(true);
        });

        // Opción 4: Modificar
        btnModificar.addActionListener(e -> {
            VentanaModificarInterna ventanamodificar = new VentanaModificarInterna();
            ventanamodificar.setVisible(true);
        });

        // 5. Añadir Botones al Panel
        panel.add(btnListar);
        panel.add(btnCrear);
        panel.add(btnBorrar);
        panel.add(btnModificar);

        // 6. Añadir el Panel a la Ventana
        this.add(panel);
    }
    // -------------------------------------------------------------
    // CLASE INTERNA (VentanaListar implementada internamente)
    // -------------------------------------------------------------
    private class VentanaListarInterna extends JDialog {
        private final SensoresBDD sensoresBDD = new SensoresBDD();
        private final JTable tablaSensores;

        public VentanaListarInterna() {
            super(VentanaPrincipal.this, "Listado de Sensores", true); // 'true' la hace modal
            this.setSize(600, 400);
            this.setLocationRelativeTo(VentanaPrincipal.this);
            this.setLayout(new BorderLayout(10, 10)); // Usamos BorderLayout para el JDialog

            sensoresBDD.connect();
            // Obtener datos de sensores desde la base de datos
            List<Sensor> listaSensores = sensoresBDD.obtenerTodos();

            // Crear modelo de tabla
            String[] columnas = {"Nombre", "Tipo", "Valor"};
            Object[][] datos = new Object[listaSensores.size()][3];

            for (int i = 0; i < listaSensores.size(); i++) {
                Sensor s = listaSensores.get(i);
                datos[i][0] = s.getNombre();
                datos[i][1] = s.getTipo();
                datos[i][2] = s.getValor();
            }

            tablaSensores = new JTable(datos, columnas);
            JScrollPane scrollPane = new JScrollPane(tablaSensores);

            this.add(scrollPane, BorderLayout.CENTER);
        }
    }
    // -------------------------------------------------------------
    // CLASE INTERNA (VentanaCrear implementada internamente)
    // -------------------------------------------------------------
    private class VentanaCrearInterna extends JDialog {
        // Componentes de la interfaz
        private final JTextField txtNombre;
        private final JTextField txtTipo;
        private final JTextField txtValor;
        private final JButton btnGuardar;
        private final SensoresBDD sensoresBDD = new SensoresBDD();
        public VentanaCrearInterna() {
            super(VentanaPrincipal.this, "Crear Nuevo Sensor", true); // 'true' la hace modal
            this.setSize(350, 250);
            this.setLocationRelativeTo(VentanaPrincipal.this);
            this.setLayout(new BorderLayout(10, 10)); // Usamos BorderLayout para el JDialog

            sensoresBDD.connect();
            // --- Panel de Campos (Formulario) ---
            JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10)); // 3 filas, 2 columnas
            formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // Campo Nombre
            formPanel.add(new JLabel("Nombre del Sensor:"));
            txtNombre = new JTextField(20);
            formPanel.add(txtNombre);
            
            // Campo Tipo
            formPanel.add(new JLabel("Tipo de Sensor:"));
            txtTipo = new JTextField(20);
            formPanel.add(txtTipo);
            
            // Campo Valor
            formPanel.add(new JLabel("Valor Inicial:"));
            txtValor = new JTextField(20);
            formPanel.add(txtValor);

            // --- Botón de Acción ---
            btnGuardar = new JButton("Crear Registro");
            btnGuardar.addActionListener(e -> crearSensor());

            // Añadir componentes a la ventana
            this.add(formPanel, BorderLayout.CENTER);
            this.add(btnGuardar, BorderLayout.SOUTH);
        }

        /**
        * Lógica para validar, obtener datos y guardar el sensor.
        */
        private void crearSensor() {
            try {
                // 1. Obtener y validar los datos
                String nombre = txtNombre.getText().trim();
                String tipo = txtTipo.getText().trim();
                double valor = Double.parseDouble(txtValor.getText().trim());

                if (nombre.isEmpty() || tipo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre y el tipo no pueden estar vacíos.", 
                                                    "Error de Validación", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // 2. Crear el objeto Sensor
                Sensor nuevoSensor = new Sensor(nombre, tipo, valor);
                
                // 3. Insertar en la Base de Datos usando el DAO
                boolean exito = sensoresBDD.insertar(nuevoSensor);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Sensor '" + nombre + "' creado exitosamente.", 
                                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    // Limpiar campos o cerrar ventana
                    dispose(); 
                } else {
                    // El mensaje de error de DB ya lo maneja SensorDAO, pero se muestra un fallback.
                    JOptionPane.showMessageDialog(this, "Fallo al crear el sensor. Verifique la conexión a la DB.", 
                                                    "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El valor debe ser un número válido.", 
                                                "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // -------------------------------------------------------------
    // CLASE INTERNA (VentanaBorrar implementada internamente)
    // -------------------------------------------------------------
    private class VentanaBorrarInterna extends JDialog {
        
        private final JTextField txtNombreBusqueda;
        private final JTextField txtTipoMostrar;
        private final JTextField txtValorMostrar;
        private final JButton btnBuscar;
        private final JButton btnBorrar;
        private final SensoresBDD sensoresBDD = new SensoresBDD();

        // Almacena el nombre del sensor encontrado para el borrado
        private String nombreEncontrado = null; 

        public VentanaBorrarInterna() {
            super(VentanaPrincipal.this, "Borrar Sensor por Nombre", true);
            this.setSize(400, 300);
            this.setLocationRelativeTo(VentanaPrincipal.this);
            this.setLayout(new BorderLayout(10, 10));

            sensoresBDD.connect(); // Establecer conexión

            // --- Panel de Formulario ---
            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Campo de entrada (Solo para la búsqueda por PK)
            formPanel.add(new JLabel("Nombre (Clave Primaria):"));
            txtNombreBusqueda = new JTextField(20);
            formPanel.add(txtNombreBusqueda);
            
            // Campos de Salida (Mostrar el registro completo)
            formPanel.add(new JLabel("Tipo:"));
            txtTipoMostrar = new JTextField(20);
            txtTipoMostrar.setEditable(false); // No se puede modificar
            formPanel.add(txtTipoMostrar);
            
            formPanel.add(new JLabel("Valor:"));
            txtValorMostrar = new JTextField(20);
            txtValorMostrar.setEditable(false); // No se puede modificar
            formPanel.add(txtValorMostrar);
            
            // --- Panel de Botones ---
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            btnBuscar = new JButton("Buscar Registro");
            btnBorrar = new JButton("BORRAR DEFINITIVAMENTE");
            btnBorrar.setEnabled(false); // Deshabilitado hasta que se encuentre un registro

            btnBuscar.addActionListener(e -> buscarSensor());
            btnBorrar.addActionListener(e -> borrarSensor());
            
            buttonPanel.add(btnBuscar);
            buttonPanel.add(btnBorrar);

            this.add(formPanel, BorderLayout.CENTER);
            this.add(buttonPanel, BorderLayout.SOUTH);
        }

        private void buscarSensor() {
            String nombre = txtNombreBusqueda.getText().trim();
            limpiarCamposSalida(); // Limpiar campos antes de la búsqueda
            nombreEncontrado = null;
            btnBorrar.setEnabled(false);

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe introducir el nombre del sensor (PK).", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Sensor sensor = sensoresBDD.obtenerPorclave(nombre);

            if (sensor != null) {
                // Mostrar el registro completo
                txtTipoMostrar.setText(sensor.getTipo());
                txtValorMostrar.setText(String.valueOf(sensor.getValor()));
                
                nombreEncontrado = sensor.getNombre();
                btnBorrar.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Registro encontrado. Puede proceder a borrar.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún sensor con el nombre: " + nombre, "No Encontrado", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        private void borrarSensor() {
            if (nombreEncontrado == null) return; // Protección

            int confirmacion = JOptionPane.showConfirmDialog(this, 
                                                            "¿Está seguro de que desea BORRAR el sensor: " + nombreEncontrado + "? Esta acción es irreversible.", 
                                                            "Confirmar Borrado", 
                                                            JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                if (sensoresBDD.borrarPorClave(nombreEncontrado)) {
                    JOptionPane.showMessageDialog(this, "Sensor '" + nombreEncontrado + "' eliminado exitosamente.", "Borrado Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Cerrar la ventana
                } else {
                    JOptionPane.showMessageDialog(this, "Fallo al borrar el sensor. Verifique la conexión.", "Error Borrado", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        private void limpiarCamposSalida() {
            txtTipoMostrar.setText("");
            txtValorMostrar.setText("");
            btnBorrar.setEnabled(false);
            nombreEncontrado = null;
        }
    }
    

    private class VentanaModificarInterna extends JDialog {
        private final JTextField txtNombreBusqueda; // Campo de búsqueda (no editable después de buscar)
        private final JTextField txtTipoEditable;   // Campo editable para el Tipo
        private final JTextField txtValorEditable;  // Campo editable para el Valor
        private final JButton btnBuscar;
        private final JButton btnModificar;
        private final SensoresBDD sensoresBDD = new SensoresBDD();

        // Almacena el nombre del sensor original (PK)
        private String nombreOriginal = null; 

        public VentanaModificarInterna() {
            super(VentanaPrincipal.this, "Modificar Sensor por Nombre", true);
            this.setSize(450, 300);
            this.setLocationRelativeTo(VentanaPrincipal.this);
            this.setLayout(new BorderLayout(10, 10));

            sensoresBDD.connect(); // Establecer conexión

            // --- Panel de Formulario ---
            JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Campo Nombre (Búsqueda e Identificación)
            formPanel.add(new JLabel("Nombre (Clave Primaria):"));
            txtNombreBusqueda = new JTextField(20);
            formPanel.add(txtNombreBusqueda);
            
            // Campo Tipo (Editable)
            formPanel.add(new JLabel("Tipo (Editable):"));
            txtTipoEditable = new JTextField(20);
            txtTipoEditable.setEditable(false); // Inicialmente deshabilitado
            formPanel.add(txtTipoEditable);
            
            // Campo Valor (Editable)
            formPanel.add(new JLabel("Valor (Editable):"));
            txtValorEditable = new JTextField(20);
            txtValorEditable.setEditable(false); // Inicialmente deshabilitado
            formPanel.add(txtValorEditable);
            
            // --- Panel de Botones ---
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            btnBuscar = new JButton("Buscar Registro");
            btnModificar = new JButton("Guardar Modificación");
            btnModificar.setEnabled(false); // Deshabilitado hasta que se encuentre un registro

            btnBuscar.addActionListener(e -> buscarSensor());
            btnModificar.addActionListener(e -> guardarModificacion());
            
            buttonPanel.add(btnBuscar);
            buttonPanel.add(btnModificar);

            this.add(formPanel, BorderLayout.CENTER);
            this.add(buttonPanel, BorderLayout.SOUTH);
        }

        private void buscarSensor() {
            String nombre = txtNombreBusqueda.getText().trim();
            limpiarCamposEditables(false); // Limpiar y deshabilitar

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe introducir el nombre del sensor (PK).", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Sensor sensor = sensoresBDD.obtenerPorclave(nombre);

            if (sensor != null) {
                // Rellenar campos con datos existentes
                txtTipoEditable.setText(sensor.getTipo());
                txtValorEditable.setText(String.valueOf(sensor.getValor()));
                
                // Habilitar la edición y el botón de guardar
                txtTipoEditable.setEditable(true);
                txtValorEditable.setEditable(true);
                btnModificar.setEnabled(true);
                nombreOriginal = sensor.getNombre();
                
                // Bloquear la edición del nombre (PK)
                txtNombreBusqueda.setEditable(false); 
                
                JOptionPane.showMessageDialog(this, "Registro encontrado. Puede modificar los campos y guardar.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró ningún sensor con el nombre: " + nombre, "No Encontrado", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        private void guardarModificacion() {
            if (nombreOriginal == null) return; // Protección

            try {
                String nuevoTipo = txtTipoEditable.getText().trim();
                double nuevoValor = Double.parseDouble(txtValorEditable.getText().trim());

                if (nuevoTipo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El tipo no puede estar vacío.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Crear un nuevo objeto Sensor con los datos modificados (usando el nombre original como PK)
                Sensor sensorModificado = new Sensor(nombreOriginal, nuevoTipo, nuevoValor);

                if (sensoresBDD.actualizar(sensorModificado)) {
                    JOptionPane.showMessageDialog(this, "Sensor '" + nombreOriginal + "' modificado exitosamente.", "Modificación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Cerrar la ventana
                } else {
                    JOptionPane.showMessageDialog(this, "Fallo al modificar el sensor. Verifique la conexión.", "Error Modificación", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El valor debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void limpiarCamposEditables(boolean limpiarNombre) {
            if(limpiarNombre) txtNombreBusqueda.setText("");
            txtTipoEditable.setText("");
            txtValorEditable.setText("");
            txtTipoEditable.setEditable(false);
            txtValorEditable.setEditable(false);
            btnModificar.setEnabled(false);
            nombreOriginal = null;
            txtNombreBusqueda.setEditable(true);
        }
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        // Ejecutar la interfaz gráfica en el Event Dispatch Thread (recomendado por Swing)
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}