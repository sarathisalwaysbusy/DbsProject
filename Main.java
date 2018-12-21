
public final class Main {

	public static void main(String[] args) {
		Database db = new Database();
		db.selectDatabase();
		db.createRelations();
		Utils.dataBase = db;
		Utils.displayLoginScreen();
		
	}

}
