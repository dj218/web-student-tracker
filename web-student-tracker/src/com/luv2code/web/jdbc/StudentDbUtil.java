package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {

	private DataSource dataSource;
	
	public StudentDbUtil(DataSource thedataSource)
	{
		dataSource=thedataSource;
	}
	
	public List<Student> getStudents() throws Exception{
		
		List<Student> students = new ArrayList<>();
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try {
			//get a connection
			myConn = dataSource.getConnection();
			
			//create sql Statement
			String sql="select * from student order by last_name";
			
			myStmt = myConn.createStatement();
			
			//Execute query
			myRs = myStmt.executeQuery("sql");
			
			//process result set
			while(myRs.next())
			{
				//receive data from result set row
				int id=myRs.getInt("id");
				String firstName=myRs.getString("first_name");
				String lastName=myRs.getString("last_name");
				String email=myRs.getString("email");
				
				//create new student object
				Student tempStudent = new Student(id,firstName,lastName,email);
				
				students.add(tempStudent);
			}
			
			return students;
		}
		finally{
			
			//close JDBC objects;
			close(myConn,myStmt,myRs);
		}
	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		try {
			if(myConn != null)
			{
				myConn.close();  //doesn't really close it ... just puts back in connection poll 
			}
			if(myStmt != null)
			{
				myStmt.close();
			}
			if(myRs != null){
				myRs.close();
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}

	public void addStudent(Student theStudent) throws Exception{

		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
		//get db connection
			myConn = dataSource.getConnection();
		
		//create sql for insert
			String sql = "insert into student " 
					+ "(first_name, last_name, email) " 
					+ "values(?, ?, ?)";
			
			myStmt = myConn.prepareStatement("sql");
		
		//set the param value for the student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
		//execute sql insert
			myStmt.execute();
		}
		finally {
		//clean up JDBC object
			close(myConn, myStmt, null);
		}
	}

	public Student getStudent(String theStudentId) throws Exception {
		Student theStudent = null;
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int studentId;
		try {
			// convert student id to int
			studentId =Integer.parseInt(theStudentId);
			
			// get connection to the database
			myConn = dataSource.getConnection();
			
			//create sql to get the selected student
			String sql = "select * from student where id=?";
			
			//create prepared statement
			myStmt = myConn.prepareStatement(sql);
			
			//set param
			myStmt.setInt(1, studentId);
			
			//execute statement
			myRs = myStmt.executeQuery();
			
			//retrieve data from that result set row
			if(myRs.next())
			{
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");

				//use the studentId during Construction
				theStudent = new Student(studentId, firstName, lastName, email);
			}
			else {
				throw new Exception("Could not find student id: " + studentId);
			}
			
		return theStudent;
		}
		finally {
			//clean up JDBC objects
			close(myConn, myStmt, myRs);
		}
	}

	public void updateStudent(Student theStudent) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
		//get Db Connection
		myConn = dataSource.getConnection();
		
		//create sql update statement
		String sql = "update student "
				+ "set first_name=?, last_name=?, email=? "
				+ "where id=?";
		
		//prepare statement
		myStmt = myConn.prepareStatement(sql);
		
		//set params
		myStmt.setString(1, theStudent.getFirstName());
		myStmt.setString(2, theStudent.getLastName());
		myStmt.setString(3, theStudent.getEmail());
		myStmt.setInt(4, theStudent.getId());
		
		//execute sql statement 
		myStmt.execute();
		}
		finally {
			close(myConn, myStmt, null);	
		}
	}

	public void deleteStudent(String theStudentId) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		
		try {
			//convert studentId into an integer
			int studentId = Integer.parseInt(theStudentId);
			
			//get connection to the database
			myConn = dataSource.getConnection();
			
			//create the sql delete student
			String sql = "delete from student where id=?";
			
			//prepare statement
			myStmt = myConn.prepareStatement(sql);
			
			//set params
			myStmt.setInt(1, studentId);
			
			//execute sql statement
			myStmt.execute();
		}
		finally {
			//clean up JDBC objects
			close(myConn, myStmt, null);
		}
	}
}
