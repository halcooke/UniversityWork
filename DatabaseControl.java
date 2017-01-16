
import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DatabaseControl {

	public DatabaseControl() {

	}

	public String[] generateNames(int size) throws Exception {
		String FirstNamesDirectory = "H:/workspace/DatabasesAssignment/src/FirstNames.csv";
		String LastNamesDirectory = "H:/workspace/DatabasesAssignment/src/LastNames.csv";
		BufferedReader fr = new BufferedReader(new FileReader(FirstNamesDirectory));
		BufferedReader lr = new BufferedReader(new FileReader(LastNamesDirectory));
		String line = null;
		// String[] FirstNames = null;
		ArrayList FirstNames = new ArrayList();
		while (FirstNames.size() < 5000) {
			line = fr.readLine();
			String name = line;
			FirstNames.add(name);
		}

		ArrayList LastNames = new ArrayList();
		while (LastNames.size() < 8000) {
			line = lr.readLine();
			String name = line;
			LastNames.add(name);
		}

		String[] fullNames = new String[size];

		for (int i = 0; i < size; i++) {
			int RandomNum1 = ThreadLocalRandom.current().nextInt(0, 1500);
			int RandomNum2 = ThreadLocalRandom.current().nextInt(0, 1500);
			String completeName = ((FirstNames.get(RandomNum1)) + " " + (LastNames.get(RandomNum2)));
			fullNames[i] = completeName;
		}

		return fullNames;
	}

	public void populateGift(Connection conn) throws SQLException {

		ArrayList gifts = new ArrayList();

		String gift1 = "Barbie Doll";
		gifts.add(gift1);
		String gift2 = "Pedal Car";
		gifts.add(gift2);
		String gift3 = "Lego Blocks";
		gifts.add(gift3);
		String gift4 = "Scooter";
		gifts.add(gift4);
		String gift5 = "Water Slide";
		gifts.add(gift5);
		String gift6 = "Model Train Set";
		gifts.add(gift6);
		String gift7 = "Drum Kit";
		gifts.add(gift7);
		String gift8 = "Dinosaur Plushie";
		gifts.add(gift8);
		String gift9 = "Elf on the shelf";
		gifts.add(gift9);
		String gift10 = "Remote Controlled Car";
		gifts.add(gift10);

		for (Object gift : gifts) {
			Statement addGift = conn.createStatement();
			String sql = "INSERT INTO gift (description) " + "VALUES ('" + gift + "');";
			addGift.execute(sql);
		}

		System.out.println("Finished populating gift table");
	}

	public void populateChildren(Connection conn, int size) throws Exception {
		String[] names = generateNames(size);
		String AddressDirectory = "H:/workspace/DatabasesAssignment/src/addresses.csv";
		BufferedReader br = new BufferedReader(new FileReader(AddressDirectory));
		ArrayList addresses = new ArrayList();
		String line = null;
		while (addresses.size() < size) {
			line = br.readLine();
			String name = line;
			addresses.add(name);
		}

		try {

			// Load the PostgreSQL JDBC driver
			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException ex) {
			System.out.println("Driver not found");
			System.exit(1);
		}

		for (int i = 0; i < size; i++) {
			Statement addChild = conn.createStatement();
			int RandomNum2 = ThreadLocalRandom.current().nextInt(0, addresses.size() - 1);
			String addressToEnter = (String) addresses.get(RandomNum2);
			String sql = "INSERT INTO child (name, address) " + "VALUES ('" + names[i] + "','" + addressToEnter + "')";
			addChild.execute(sql);
		}

		System.out.println("Finished populating child table");

	}

	public void populateHelpers(Connection conn, int size) throws Exception {
		String[] names = generateNames(size);
		for (int i = 0; i < size; i++) {
			Statement addHelper = conn.createStatement();
			String sql = "INSERT INTO santaslittlehelper (name) " + "VALUES ('" + names[i] + "');";
			addHelper.execute(sql);
		}

		System.out.println("Finished populating helper table");
	}

	public void populatePresents(Connection conn, int size) throws Exception {
		ArrayList gid = new ArrayList();
		ArrayList cid = new ArrayList();
		ArrayList slhid = new ArrayList();

		Statement getData = conn.createStatement();
		String gidData = "SELECT gid FROM gift;";
		ResultSet rs = getData.executeQuery(gidData);
		String gidString = null;
		while (rs.next()) {
			gidString = rs.getString("gid");
			gid.add(gidString);
		}
		String cidData = "SELECT cid FROM child;";
		ResultSet rs2 = getData.executeQuery(cidData);
		String cidString = null;
		while (rs2.next()) {
			cidString = rs2.getString("cid");
			cid.add(cidString);
		}
		String shlidData = "SELECT slhid FROM santaslittlehelper;";
		ResultSet rs3 = getData.executeQuery(shlidData);
		String slhidString = null;
		while (rs3.next()) {
			slhidString = rs3.getString("slhid");
			slhid.add(slhidString);
		}

		if ((slhid.size() * 15) < size) {
			int maxPresents = slhid.size() * 15;
			System.out.println(
					"Each helper can only do a reasonable amount of presents(15) so the maximum ammount in this case is "
							+ maxPresents + ". And the number has been reduced in turn.");
			size = maxPresents;
		}

		int[] gidNew = new int[size];
		int[] cidNew = new int[size];
		int[] slhidNew = new int[size];

		System.out.println("Starting Input");

		for (int i = 0; i < size; i++) {
			int RandomNum1 = ThreadLocalRandom.current().nextInt(1, cid.size() + 1);
			int RandomNum2 = ThreadLocalRandom.current().nextInt(1, gid.size() + 1);
			if (checkExists(gidNew, cidNew, RandomNum2, RandomNum1) == true) {
				while (checkExists(gidNew, cidNew, RandomNum2, RandomNum1) == true) {
					RandomNum2 = ThreadLocalRandom.current().nextInt(1, gid.size() + 1);
				}
			}
			int RandomNum3 = ThreadLocalRandom.current().nextInt(1, slhid.size() + 1);
			if (checkFrequency(slhidNew, RandomNum3) > 14) {
				while (checkFrequency(slhidNew, RandomNum3) > 14) {
					RandomNum3 = ThreadLocalRandom.current().nextInt(1, slhid.size() + 1);
				}
			}
			gidNew[i] = RandomNum2;
			cidNew[i] = RandomNum1;
			slhidNew[i] = RandomNum3;
		}

		for (int j = 0; j < size; j++) {
			Statement addPresent = conn.createStatement();
			String sql = "INSERT INTO present (gid,cid,slhid) " + "VALUES ('" + gidNew[j] + "','" + cidNew[j] + "','"
					+ slhidNew[j] + "')";
			addPresent.execute(sql);
		}

		System.out.println("Completed present generation");
	}

	public boolean checkExists(int[] gidArray, int[] cidArray, int gid, int cid) {
		for (int i = 0; i < gidArray.length; i++) {
			if ((gidArray[i] == gid) && (cidArray[i] == cid)) {
				return true;
			}
		}
		return false;

	}

	public int checkFrequency(int[] array, int a) {
		int count = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == a) {
				count++;
			}
		}
		return count;
	}

	//Generates the tables if they don't exist
	public void generateTables(Connection conn) {

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException ex) {
			System.out.println("Driver not found");
			System.exit(1);
		}

		System.out.println("PostgreSQL driver registered.");

		try {
			Statement createChild = conn.createStatement();
			String child = "CREATE TABLE IF NOT EXISTS Child(cid SERIAL NOT NULL PRIMARY KEY,name varchar(50) NOT NULL,address varchar(20) NOT NULL);";
			createChild.execute(child);

		} catch (SQLException sqlE) {
			System.out.println("SQL code is broken");

		}

		try {
			Statement createHelpers = conn.createStatement();
			String santashelpers = "CREATE TABLE IF NOT EXISTS SantasLittleHelper(slhid SERIAL NOT NULL PRIMARY KEY, name varchar(50) NOT NULL);";
			createHelpers.execute(santashelpers);

		} catch (SQLException sqlE) {
			System.out.println("SQL code is broken");

		}

		try {
			Statement createGifts = conn.createStatement();
			String gift = "CREATE TABLE IF NOT EXISTS Gift(gid SERIAL NOT NULL PRIMARY KEY,description varchar(50) NOT NULL);";
			createGifts.execute(gift);

		} catch (SQLException E) {
			System.out.println("SQL code is broken");

		}

		try {
			Statement createGifts = conn.createStatement();
			String gift = "CREATE TABLE IF NOT EXISTS Present(gid integer REFERENCES gift(gid),cid integer REFERENCES child(cid),slhid integer REFERENCES santaslittlehelper(slhid));";
			createGifts.execute(gift);

		} catch (SQLException E) {
			System.out.println("SQL code is broken");

		}

	}

	//Generates a child report
	public void generateChildReport(Connection conn, int id) throws Exception {
		Statement getData = conn.createStatement();
		String nameSQL = "SELECT name FROM child WHERE cid = '" + id + "';";
		ResultSet rsName = getData.executeQuery(nameSQL);
		String nameString = null;
		while (rsName.next()) {
			nameString = rsName.getString("name");
			String ChildName = nameString;
		}
		if (nameString != null) {
			String addressSQL = "SELECT address FROM child WHERE cid = '" + id + "';";
			ResultSet rsAddress = getData.executeQuery(addressSQL);
			String addressString = null;
			while (rsAddress.next()) {
				addressString = rsAddress.getString("address");
				String ChildAddress = addressString;
			}

			String presentSQL = "SELECT description, gid FROM gift WHERE gid IN (SELECT gid FROM present WHERE cid = '"
					+ id + "');";
			ResultSet rsPresent = getData.executeQuery(presentSQL);
			ArrayList giftDescriptions = new ArrayList();
			ArrayList giftID = new ArrayList();
			String descriptionString = null;
			String idString = null;
			while (rsPresent.next()) {
				descriptionString = rsPresent.getString("description");
				idString = rsPresent.getString("gid");
				giftDescriptions.add(descriptionString);
				giftID.add(idString);
			}

			System.out.println("The child queried is called " + nameString + ".\n\tTheir ID is " + id
					+ ".\n\tTheir address is " + addressString + ".");
			System.out.println("\tThe presents they will be getting are: ");
			if (giftDescriptions.size() == 0) {
				System.out.println("\t\tNothing.");
			} else {
				for (int i = 0; i < giftDescriptions.size(); i++) {
					System.out.println("\t\t" + giftDescriptions.get(i) + " which has an ID of: " + giftID.get(i));
				}
			}
		} else {
			System.out.println("No child with that ID.");
		}

	}

	//Generates a helper report 
	public void generateHelperReport(Connection conn, int id) throws Exception {
		Statement getData = conn.createStatement();
		String nameSQL = "SELECT name FROM santaslittlehelper WHERE slhid = '" + id + "';";
		ResultSet rsName = getData.executeQuery(nameSQL);
		String nameString = null;
		while (rsName.next()) {
			nameString = rsName.getString("name");
			String ChildName = nameString;
		}
		if (nameString != null) {

			String presentSQL = "SELECT * FROM present WHERE slhid = '" + id + "';";
			ResultSet rsChildPresent = getData.executeQuery(presentSQL);
			ArrayList childID = new ArrayList();
			ArrayList doneList = new ArrayList();
			String idString = null;
			while (rsChildPresent.next()) {
				idString = rsChildPresent.getString("cid");
				childID.add(idString);
			}

			System.out.println("The helper queried is called " + nameString + ".\n\tTheir ID is " + id + ".");
			System.out.println("\tThey have to deliver presents to : ");
			if (childID.size() == 0) {
				System.out.println("\t\tNo-one.");
			} else {
				for (int i = 0; i < childID.size(); i++) {
					if (doneList.contains(childID.get(i))) {
					} else {
						String childDescriptionSQL = "SELECT name,address FROM child WHERE cid = '" + childID.get(i)
								+ "';";
						ResultSet childDescription = getData.executeQuery(childDescriptionSQL);

						while (childDescription.next()) {
							String name = childDescription.getString("name");
							String address = childDescription.getString("address");
							System.out.println("\t\t" + name + " who lives at " + address);
						}

						ArrayList childsPresents = new ArrayList();
						String childPresentSQL = "SELECT gid FROM present WHERE slhid = '" + id + "' AND cid = '"
								+ childID.get(i) + "';";
						ResultSet rsChildPresentHelperID = getData.executeQuery(childPresentSQL);
						ArrayList specificPresentID = new ArrayList();
						ArrayList specificPresentDescription = new ArrayList();

						while (rsChildPresentHelperID.next()) {
							String specificPresentIDString = rsChildPresentHelperID.getString("gid");
							specificPresentID.add(specificPresentIDString);
						}

						for (int j = 0; j < specificPresentID.size(); j++) {
							String descriptionSQL = "SELECT description FROM gift WHERE gid = '" + specificPresentID.get(j)
									+ "';";
							ResultSet specificPresentDescriptionA = getData.executeQuery(descriptionSQL);

							while (specificPresentDescriptionA.next()) {
								String specificPresentDescriptionString = specificPresentDescriptionA
										.getString("description");
								specificPresentDescription.add(specificPresentDescriptionString);
							}
						}
						for (int k = 0; k < specificPresentID.size(); k++) {
							System.out.println("\t\t\t" + specificPresentDescription.get(k) + " which has an ID of: "
									+ specificPresentID.get(k));
						}
						doneList.add(childID.get(i));
					}
				}

			}
		} else {
			System.out.println("No helper with that ID.");
		}
	}

	// Main method to connect to database on the module server.
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		String username = "hxc598";
		String password = "aq4ks9gt3n";
		String database = "hxc598";
		String URL = "jdbc:postgresql://mod-intro-databases.cs.bham.ac.uk/" + database;
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, username, password);
		} catch (SQLException ex) {
			System.out.println("Ooops, couldn't get a connection");
			System.out.println("Check that <username> & <password> are right");
			System.exit(1);
		}
		if (conn != null) {
			System.out.println("Database accessed!");
		} else {
			System.out.println("Failed to make connection");
			System.exit(1);
		}

		DatabaseControl test = new DatabaseControl();
		test.generateTables(conn);

		// Need it fully qualified if connecting via vpn

		System.out.println("Would you like to report or generate?");
		String answer = input.nextLine();
		while (!answer.equals("report") && (!answer.equals("generate"))) {
			System.out.println("Please enter 'report' or 'generate'.");
			answer = input.nextLine();
		}
		if (answer.equals("report")) {
			System.out.println("Would you like to find child ID's with a certain number of presents?");
			String childQuestion = input.nextLine();
			while ((childQuestion.equals("Yes")) || (childQuestion.equals("yes"))) {
				System.out.println("Please enter a number of presents.");
				int numOfPresents = input.nextInt();
				Statement getData = conn.createStatement();
				String cidData = "SELECT cid FROM present GROUP BY cid HAVING COUNT(gid) = " + numOfPresents + ";";
				ResultSet rs = getData.executeQuery(cidData);
				String cidString = null;
				while (rs.next()) {
					cidString = rs.getString("cid");
					System.out.println("CID: " + cidString);
				}
				System.out.println("Would you like to search again?");
				input.nextLine();
				childQuestion = input.nextLine();

			}
			if ((!childQuestion.equals("Yes")) || (!childQuestion.equals("yes"))) {
				System.out.println("Would you like a report on a child or a helper?");
				String reportQuestion = input.nextLine();
				while (!reportQuestion.equals("child") && (!reportQuestion.equals("helper"))) {
					System.out.println("Please enter 'child' or 'helper'.");
					reportQuestion = input.nextLine();
				}

				if (reportQuestion.equals("child")) {
					System.out.println("Please input the child ID (-1 To exit)");
					int childID = input.nextInt();
					while (childID != -1) {
						test.generateChildReport(conn, childID);
						System.out.println("Please input the child ID (-1 To exit)");
						childID = input.nextInt();
					}
				} else {
					System.out.println("Please input the Helper ID (-1 To exit)");
					int helperID = input.nextInt();
					while (helperID != -1) {
						test.generateHelperReport(conn, helperID);
						System.out.println("Please input the Helper ID (-1 To exit)");
						helperID = input.nextInt();
					}
				}
			}
		} else {

			System.out.println("Please enter the number of children you wish to add");
			int numberChildren = input.nextInt();
			if (numberChildren != 0) {
				test.populateChildren(conn, numberChildren);
			}
			System.out.println("Please enter the number of helpers you wish to add");
			int numberHelpers = input.nextInt();
			if (numberHelpers != 0) {
				test.populateHelpers(conn, numberHelpers);
			}
			test.populateGift(conn);

			int maxPresents = numberHelpers * 15;

			System.out.println(
					"Please enter the number of presents you wish to add (Max in this case is " + maxPresents + ")");
			int numberPresents = input.nextInt();
			if (numberPresents != 0) {
				test.populatePresents(conn, numberPresents);
			}
		}

		try {
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		input.close();

	}
}