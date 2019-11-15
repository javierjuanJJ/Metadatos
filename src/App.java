/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author sergio
 */
public class App {

	static Connection conn = null;
	static Statement stmt = null;
	static DatabaseMetaData dbmd = null;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) throws SQLException {
		conn = ConexionBD.getConexion();
		if (conn != null) {
			dbmd = conn.getMetaData();
			ver_datos_tabla();
			// verMetadatos();
		}
		ConexionBD.cerrar();
	}

	private static void verMetadatos() {
		try {
			// dbmd = conn.getMetaData();
			// Informacion del producto de la base de datos
			infoProducto(dbmd);
			// Informacion del driver JDBC
			infoDriver(dbmd);
			// Informacion sobre las funciones de la base de datos
			infoFunciones(dbmd);
			// Tablas existentes
			infoTablas(dbmd);
			// Procedimientos existentes
			infoProcedimientos(dbmd);
			// Bases de datos disponibles
			infoBD(dbmd);
		} catch (SQLException se) {
			System.err.println(se);
		}
	}

	public static void infoProducto(DatabaseMetaData dbmd) throws SQLException {
		System.out.println(">>>Informacion sobre el SGBD:");
		String producto = dbmd.getDatabaseProductName();
		String version = dbmd.getDatabaseProductVersion();
		boolean soportaSQL = dbmd.supportsANSI92EntryLevelSQL();
		boolean soportaConvert = dbmd.supportsConvert();
		boolean usaFich = dbmd.usesLocalFiles();
		boolean soportaGRBY = dbmd.supportsGroupBy();
		boolean soportaMinSQL = dbmd.supportsMinimumSQLGrammar();
		boolean soportaTransac = dbmd.supportsTransactions();
		boolean soportaRsSensible = dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
		boolean soportaRsNoSensible = dbmd.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);
		boolean soportaRsForward = dbmd.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY);

		String nombre = dbmd.getUserName();
		String url = dbmd.getURL();
		System.out.println(" Producto: " + producto + " " + version);
		System.out.println(" Soporta el SQL ANSI92: " + soportaSQL);
		System.out.println(" Soporta la funcion CONVERT entre tipos SQL: " + soportaConvert);
		System.out.println(" Almacena las tablas en ficheros locales: " + usaFich);
		System.out.println(" Nombre del usuario conectado: " + nombre);
		System.out.println(" URL de la Base de Datos: " + url);
		System.out.println(" Soporta GROUP BY: " + soportaGRBY);
		System.out.println(" Soporta la minima gramatica SQL: " + soportaMinSQL);
		System.out.println(" Soporta transacciones: " + soportaTransac);
		System.out.println(" Soporta resultset sensible: " + soportaRsSensible);
		System.out.println(" Soporta resultset no sensible: " + soportaRsNoSensible);
		System.out.println(" Soporta resultset forward only: " + soportaRsForward);
		System.out.println(" ...");
		System.out.println();
	}

	public static void infoDriver(DatabaseMetaData dbmd) throws SQLException {
		System.out.println(">>>Informacion sobre el driver:");
		String driver = dbmd.getDriverName();
		String driverVersion = dbmd.getDriverVersion();
		int verMayor = dbmd.getDriverMajorVersion();
		int verMenor = dbmd.getDriverMinorVersion();
		System.out.println("---------------------------" + dbmd.getDatabaseProductVersion());
		System.out.println(" Driver: " + driver + " " + driverVersion);
		System.out.println(" Version superior del driver: " + verMayor);
		System.out.println(" Version inferior del driver: " + verMenor);
		System.out.println();
	}

	public static void infoFunciones(DatabaseMetaData dbmd) throws SQLException {
		System.out.println(">>>Funciones del DBMS:");
		String funcionesCadenas = dbmd.getStringFunctions();
		String funcionesSistema = dbmd.getSystemFunctions();
		String funcionesTiempo = dbmd.getTimeDateFunctions();
		String funcionesNumericas = dbmd.getNumericFunctions();
		System.out.println(" Funciones de Cadenas: " + funcionesCadenas);
		System.out.println(" Funciones Numericas: " + funcionesNumericas);
		System.out.println(" Funciones del Sistema: " + funcionesSistema);
		System.out.println(" Funciones de Fecha y Hora: " + funcionesTiempo);
		System.out.println();

	}

	public static void infoTablas(DatabaseMetaData dbmd) throws SQLException {
		System.out.println(">>>Tablas existentes:");
		String patron = "%"; // listamos todas las tablas
		String tipos[] = new String[1];
		tipos[0] = "TABLE"; // tablas de usuario
		// tipos[1] = patron; //vistas
		// tipos[2] = "SYSTEM TABLE"; //tablas del sistema
		ResultSet tablas = dbmd.getTables(null, null, patron, tipos);

		while (tablas.next()) {
			// Por cada tabla obtenemos su nombre y tipo
			System.out.println("Catálogo:" + tablas.getString("TABLE_CAT") + " - Nombre: "
					+ tablas.getString("TABLE_NAME") + " - Tipo: " + tablas.getString("TABLE_TYPE"));
			// System.out.println("Esquema:" + tablas.getString("TABLE_SCHEM")
			// + " - Nombre:" + tablas.getString("TABLE_NAME")
			// + " - Tipo:" + tablas.getString("TABLE_TYPE"));
		}
		System.out.println();

	}

	public static void infoProcedimientos(DatabaseMetaData dbmd) throws SQLException {
		if (dbmd.supportsStoredProcedures()) {
			System.out.println(">>>Procedimientos almacenados:");
			String patron = "%";
			ResultSet procedimientos = dbmd.getProcedures(null, null, patron);
			while (procedimientos.next()) {
				System.out.println(" " + procedimientos.getString("PROCEDURE_NAME") + " "
						+ procedimientos.getString("PROCEDURE_TYPE"));
			}
			// ResultSet funciones = dbmd.getFunctions(null, null, patron);
			// while (funciones.next()) {
			// System.out.println(" " + funciones.getString("FUNCTION_NAME") + " "
			// + funciones.getString("FUNCTION_TYPE"));
			// }
		} else {
			System.out.println(">>>El DBMS no soporta procedimientos almacenados");
		}
		System.out.println();
		ver_datos_tabla();
	}

	private static void infoBD(DatabaseMetaData dbmd) throws SQLException {
		System.out.println(">>>Bases de datos disponibles:");
		ResultSet rs = dbmd.getCatalogs();
		while (rs.next()) {
			System.out.println(rs.getString("TABLE_CAT"));
		}
	}

	/*
	 * private static void ver_datos_tabla() throws SQLException {
	 * System.out.println(">>>Bases de datos disponibles:"); String rs_sql =
	 * "select * from clientes"; Statement st =
	 * ConexionBD.getConexion().createStatement(); ResultSet
	 * rs=st.executeQuery(rs_sql); ResultSetMetaData rsmd= rs.getMetaData();
	 * 
	 * for (int c=0; c<rsmd.getColumnCount();c++) {
	 * System.out.println("Columna nombre: " + rsmd.getColumnName(c));
	 * System.out.println("Tipo tabla: " + rsmd.getColumnType(c)); } }
	 */

	private static void ver_datos_tabla() throws SQLException {
		String patron = "%";
		String tipos[] = new String[1];
		String nueva_linea = System.lineSeparator();
		tipos[0] = "TABLE";
		StringBuilder resultado = new StringBuilder();
		DatabaseMetaData m = ConexionBD.getConexion().getMetaData();
		ResultSet tables = m.getTables("empresa_ad", null, patron, tipos);
		String nombre_columna="";
		tables.beforeFirst();
		while (tables.next()) {
			String nombre_Tabla = tables.getString("TABLE_NAME");
			String rs_sql = "select * from " + nombre_Tabla;
			resultado.append("Tabla: ");
			resultado.append(nombre_Tabla);
			resultado.append(nueva_linea);
			ResultSet rs = conn.createStatement().executeQuery(rs_sql);
			rs.beforeFirst();
			ResultSetMetaData rsmd = rs.getMetaData();
			StringBuilder iguales = new StringBuilder();
			iguales = new StringBuilder();
			int columnas=rsmd.getColumnCount();
			for (int cont = 1; cont <= columnas; cont++) {

				nombre_columna=rsmd.getColumnName(cont);
				resultado.append(nombre_columna);
				
				iguales.append((repetir_caracter('=', nombre_columna.length() +7)) + "\t");
				resultado.append("\t\t");

			}
			resultado.append(nueva_linea);
			resultado.append(nueva_linea);
			resultado.append(iguales);
			resultado.append(nueva_linea);
			resultado.append(nueva_linea);

			while (rs.next()) {
				for (int a = 1; a <= columnas; a++) {
					resultado.append(rs.getObject(a) + "\t\t");
				}
				resultado.append(nueva_linea);
			}
		}
		
		System.out.println(resultado.toString());

	}

	private static Object repetir_caracter(char c, int i) {
		StringBuilder r = new StringBuilder();
		for (int a = 0; a <= i; a++) {
			r.append(c);
		}
		return r;
	}

}
