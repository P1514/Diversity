package resources;

import java.sql.SQLException;
import java.util.*;
import java.io.*;

public class Main {
	
	
	public static void main(String[] args) {
		try {
			Data test = new Data();
			test.load();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("TEST");
		
	}

}
