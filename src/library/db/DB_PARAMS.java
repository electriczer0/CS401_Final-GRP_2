package library.db;

public class DB_PARAMS {
	public static final String dbLocation = "Library.db";
	public static final String dbURL =  "jdbc:sqlite:" + dbLocation;
	public static final String dbInitSchema = "PRAGMA foreign_keys = ON";

}
