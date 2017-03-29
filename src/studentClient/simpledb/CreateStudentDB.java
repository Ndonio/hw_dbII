package studentClient.simpledb;

import java.sql.*;
import simpledb.remote.SimpleDriver;

public class CreateStudentDB {
	
	public static void main(String[] args) {
		Connection conn = null;
		try {
			Driver d = new SimpleDriver();
			conn = d.connect("jdbc:simpledb://localhost", null);
			Statement stmt = conn.createStatement();

			String s = "create table STUDENT(SId int, SName varchar(10), MajorId int, GradYear int)";
			stmt.executeUpdate(s);
			System.out.println("Table STUDENT created.");

			s = "insert into STUDENT(SId, SName, MajorId, GradYear) values ";
			String[] studvals = getStudents();
			for (int i=0; i<studvals.length; i++)
				stmt.executeUpdate(s + studvals[i]);
			System.out.println("STUDENT records inserted.");

			s = "create table DEPT(DId int, DName varchar(8))";
			stmt.executeUpdate(s);
			System.out.println("Table DEPT created.");

			s = "insert into DEPT(DId, DName) values ";
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
				"(10, 'pino', 20, 2002)",		
				"(11, 'anto', 20, 2002)",		
				"(12, 'mich', 30, 2001)",		
				"(13, 'mino', 30, 2004)",		
				"(14, 'male', 20, 2002)",		
				"(15, 'andr', 10, 2005)",		
				"(16, 'laur', 10, 2003)",
				"(17, 'umbi', 20, 2002)",		
				"(18, 'fran', 30, 2001)",		
				"(19, 'mori', 10, 2005)",		
				"(20, 'silv', 20, 2004)",		
				"(21, 'anna', 30, 2004)",		
				"(22, 'lena', 10, 2003)",		
				"(23, 'mich', 20, 2002)",					
				"(24, 'pgro', 20, 2002)",		
				"(25, 'agro', 20, 2002)",		
				"(26, 'mdfh', 30, 2001)",		
				"(27, 'mhdf', 30, 2004)",		
				"(28, 'mhfd', 20, 2002)",		
				"(29, 'afff', 10, 2005)",		
				"(30, 'lfge', 10, 2003)",
				"(31, 'uasi', 20, 2002)",		
				"(32, 'fssn', 30, 2001)",		
				"(33, 'mfyi', 10, 2005)",		
				"(34, 'swev', 20, 2004)",		
				"(35, 'weea', 30, 2004)",		
				"(36, 'ewea', 10, 2003)",		
				"(37, 'eweh', 20, 2002)",		
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
				"(30, 'drama')",
				"(4, 'biology')",
				"(12, 'physics')",
				"(28, 'art')"    			
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
				"(62, 'elocution', 30)"/*,	
				
				"(22, 'algoritms', 10)",
				"(32, 'electronics', 20)",
				"(22, 'logic', 10)",
				"(32, 'networks', 20)",
				"(22, 'compilers', 10)",
				"(32, 'calculus', 20)",*/
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
