package z_example_city.simpledb;

import java.sql.*;
import simpledb.remote.SimpleDriver;

public class CreateCityDB {
	
	public static void main(String[] args) {
		Connection conn = null;
		try {
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);
			Statement stmt = conn.createStatement();

			String s = "create table USERS(u_id int, u_name varchar(10), u_last varchar(10), u_bday int)";
			stmt.executeUpdate(s);
			System.out.println("Table USERS created.");

			s = "insert into USERS(u_id, u_name, u_last, u_bday) values ";
			String[] studvals = getStudents();
			for (int i=0; i<studvals.length; i++)
				stmt.executeUpdate(s + studvals[i]);
			System.out.println("STUDENT records inserted.");

			s = "create table PROVINCE(DId int, DName varchar(8))";
			stmt.executeUpdate(s);
			System.out.println("Table PROVINCE created.");

			s = "insert into PROVINCE(DId, DName) values ";
			String[] deptvals = getDepts();
			for (int i=0; i<deptvals.length; i++)
				stmt.executeUpdate(s + deptvals[i]);
			System.out.println("DEPT records inserted.");

			s = "create table COURSE(CId int, Title varchar(20), DeptId int)";
			stmt.executeUpdate(s);
			System.out.println("Table COURSE created.");

			s = "insert into COURSE(CId, Title, DeptId) values ";
			String[] coursevals = getCourses();
			for (int i=0; i<coursevals.length; i++)
				stmt.executeUpdate(s + coursevals[i]);
			System.out.println("COURSE records inserted.");

			s = "create table SECTION(SectId int, CourseId int, Prof varchar(8), YearOffered int)";
			stmt.executeUpdate(s);
			System.out.println("Table SECTION created.");

			s = "insert into SECTION(SectId, CourseId, Prof, YearOffered) values ";
			String[] sectvals = getSections();
			for (int i=0; i<sectvals.length; i++)
				stmt.executeUpdate(s + sectvals[i]);
			System.out.println("SECTION records inserted.");

			s = "create table ENROLL(EId int, StudentId int, SectionId int, Grade varchar(2))";
			stmt.executeUpdate(s);
			System.out.println("Table ENROLL created.");

			s = "insert into ENROLL(EId, StudentId, SectionId, Grade) values ";
			String[] enrollvals = getEnrolls(); 

			for (int i=0; i<enrollvals.length; i++)
				stmt.executeUpdate(s + enrollvals[i]);
			System.out.println("ENROLL records inserted.");

		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (conn != null)
					conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/* Data Set :D */
	/*
		"(1, 'joe', 10, 2004)",
		"(2, 'amy', 20, 2004)",
		"(3, 'max', 10, 2005)",
		"(4, 'sue', 20, 2005)",
		"(5, 'bob', 30, 2003)",
		"(6, 'kim', 20, 2001)",
		"(7, 'art', 30, 2004)",
		"(8, 'pat', 20, 2001)",
		"(9, 'lee', 10, 2004)",
	*/
	private static String[] getStudents(){
		String[] res = {
				"(1, 'joe', 10, 2004)",
				"(2, 'amy', 20, 2004)",
				"(3, 'max', 10, 2005)",
				"(4, 'sue', 20, 2005)",
				"(5, 'bob', 30, 2003)",
				"(6, 'kim', 20, 2001)",
				"(7, 'art', 30, 2004)",
				"(8, 'pat', 20, 2001)",
				"(9, 'lee', 10, 2004)",				 
		};  	
		return res;
	}
	
	/*
	 	"(10, 'compsci')",
		"(20, 'math')",
		"(30, 'drama')"   			
	 */
	private static String[] getDepts(){
		String[] res = {
				"(10, 'compsci')",
				"(20, 'math')",
				"(30, 'drama')"   			
		};
		return res;
	}

	/*
	 "(12, 'db systems', 10)",
	 "(22, 'compilers', 10)",
	 "(32, 'calculus', 20)",
	 "(42, 'algebra', 20)",
	 "(52, 'acting', 30)",
	 "(62, 'elocution', 30)"	
	 */
	private static String[] getCourses(){
		String[] res = {
				"(12, 'db systems', 10)",
				"(22, 'compilers', 10)",
				"(32, 'calculus', 20)",
				"(42, 'algebra', 20)",
				"(52, 'acting', 30)",
				"(62, 'elocution', 30)"				
		};
		return res;
	}
	
	/*
	    "(13, 12, 'turing', 2004)",
	 	"(23, 12, 'turing', 2005)",
		"(33, 32, 'newton', 2000)",
		"(43, 32, 'einstein', 2001)",
		"(53, 62, 'brando', 2001)"
	 */
	private static String[] getSections(){
		String[] res = {
				"(13, 12, 'turing', 2004)",
				"(23, 12, 'turing', 2005)",
				"(33, 32, 'newton', 2000)",
				"(43, 32, 'einstein', 2001)",
				"(53, 62, 'brando', 2001)"
		};
		return res;
	}
	
	/* 
	    "(14, 1, 13, 'A')",
		"(24, 1, 43, 'C' )",
		"(34, 2, 43, 'B+')",
		"(44, 4, 33, 'B' )",
		"(54, 4, 53, 'A' )",
		"(64, 6, 53, 'A' )"
	*/
	private static String[] getEnrolls(){
		String[] res = {
				"(14, 1, 13, 'A')",
				"(24, 1, 43, 'C' )",
				"(34, 2, 43, 'B+')",
				"(44, 4, 33, 'B' )",
				"(54, 4, 53, 'A' )",
				"(64, 6, 53, 'A' )"
		};
		return res;
	}
}
