//STEP 1. Import required packages
import java.sql.*;

public class Database {
	static final String DB_URL = "jdbc:mysql://localhost/";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "sarathisalwaysbusy";

	Connection conn;
	Statement stmt;

	public Database() {
		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			System.out.println("Creating database...");
			stmt.executeUpdate("CREATE DATABASE ChatApp");
			System.out.println("Database created successfully...");
		} catch (SQLException e) {
			if (e.getMessage().matches(".*database exists.*")) {
				System.err.println("The Database already exists");
			} else {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	void execQuery(String query) {
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void selectDatabase() {
		try {
			System.out.println("Using Database chatapp");
			stmt.executeUpdate("use chatapp");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void createRelations() {
		try {
			//Entity DB_Users
			//stmt.executeUpdate("create table db_users(username varchar(64), password varchar(64), admin bit, primary key(username));");
			//Entity User
			stmt.executeUpdate("create table user ("
					+ "name varchar (64) not null, "
					+ "username varchar (64), "
					+ "password varchar (64) not null, "
					+ "dob date not null, "
					+ "primary key (username) "
					+ "); "  );
			
			//Relation user_conversation
			stmt.executeUpdate("create table user_conversation (user_conversation_id int (16),  sent_by_username varchar(64) not null, sent_to_username varchar(64) not null,  user_text varchar (500) not null, user_percent_delivered int  (2),  user_percent_read int (2) not null,   user_time_sent int (13) not null,  primary key (user_conversation_id),  foreign key (sent_by_username) references user (username),  foreign key(sent_to_username) references user(username) );  "  );
			
			//Entity user_group
			stmt.executeUpdate("create table user_group ( "
					+ "group_id int (16), "
					+ "group_name varchar (64) not null, "
					+ "primary key(group_id) ); "    );
			
			//Relation user_belongsto
			stmt.executeUpdate("create table user_belongsto ( username varchar(64), group_id int , primary key (group_id, username),  foreign key (group_id) references user_group (group_id), foreign key (username) references user (username) );" );
			
			//Group_Conversations:
			stmt.executeUpdate("create table grp_conversation ( grp_conversation_id int (15),  grp_text varchar (500) not null,  grp_id int (15),  grp_percent_delivered int (2) not null,  grp_percent_read int (2) not null,  grp_time_sent varchar (10) not null,  grp_sent_by varchar(64) not null,  primary key (grp_conversation_id),  foreign key (grp_sent_by) references user (username), foreign key (grp_id) references user_group (group_id) ); "   );
			
			
			//3 sequences
			stmt.executeQuery("create Sequence seq_user_convo "
					+ "start with 1 "
					+ "increment by 1 "
					+ "max value 99999999999999 "
					+ "nocycle ;");
			
			stmt.executeQuery("create Sequence seq_grp_id "
					+ "start with 1 "
					+ "increment by 1 "
					+ "max value 99999999999999 "
					+ "nocycle ;");
			
			stmt.executeQuery("create Sequence seq_grp_convo "
					+ "start with 1 "
					+ "increment by 1 "
					+ "max value 99999999999999 "
					+ "nocycle ;");
			
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) {

	}
}