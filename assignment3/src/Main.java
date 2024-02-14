import java.util.ArrayList;
import java.util.Properties;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            boolean mainLoop = true;
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://localhost:5432/student";
            Properties authorization = new Properties();
            authorization.put("user", "postgres");
            authorization.put("password", "260108");
            while (mainLoop) {
                System.out.println("1. Create table\n2. Insert row\n3. Read table\n4. Delete row\n5. Update row");
                String value = sc.next();
                switch (value) {
                    case ("1"):
                        createTable(url, authorization);
                        break;
                    case ("2"):
                        insertValue(url, authorization);
                        break;
                    case ("3"):
                        readTable(url, authorization);
                        break;
                    case ("4"):
                        deleteRow(url, authorization);
                        break;
                    case ("5"):
                        updateRow(url, authorization);
                        break;
                    case ("exit"):
                        mainLoop = false;
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
           /* System.err.println("Error accessing database!");
            e.printStackTrace();*/
        }
    }

    public static void createTable(String url, Properties authorization) {
        try {
            Connection connection = DriverManager.getConnection(url, authorization);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String request = "CREATE TABLE ";
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter table name: ");
            String tableName = sc.next();
            request += tableName + " (";
            System.out.println("Type 'end' when you will end adding columns");
            String value = "";
            boolean strStart = true;
            while (true) {
                System.out.println("Enter column name: ");
                value = sc.next();
                if (value.equals("end")) {
                    break;
                }
                if (!strStart) {
                    request += ", ";
                }
                request += value + " ";
                System.out.println("Choose data type");
                System.out.println("1. INT\n2. VARCHAR\n3. DECIMAL\n4. INT PRIMARY KEY");
                value = sc.next();
                switch (value) {
                    case ("1"):
                        request += "INT";
                        break;
                    case ("2"):
                        request += "VARCHAR(50)";
                        break;
                    case ("3"):
                        request += "DECIMAL(10,2)";
                        break;
                    case ("4"):
                        request += "INT PRIMARY KEY";
                        break;
                    default:
                        break;
                }
                strStart = false;
            }
            request += ");";
            statement.executeQuery(request);
        } catch (Exception e) {
        }
    }

    public static void insertValue(String url, Properties authorization) {
        try {
            Connection connection = DriverManager.getConnection(url, authorization);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            ArrayList<String> columnList = new ArrayList<>();

            String request = "INSERT INTO ";

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter table name: ");
            String tableName = sc.next();
            request += tableName + " (";
            ResultSet table = statement.executeQuery("SELECT * FROM " + tableName);
            boolean loopWork = true;
            while (columnList.size() != table.getMetaData().getColumnCount() && loopWork) {
                System.out.println("Choose the column to insert: ");
                table.first();
                for (int i = 1; i <= table.getMetaData().getColumnCount(); i++) {
                    System.out.println(i + ". " + table.getMetaData().getColumnName(i));
                }
                int column = sc.nextInt();
                if (!columnList.contains(table.getMetaData().getColumnName(column))) {
                    columnList.add(table.getMetaData().getColumnName(column));
                } else {
                    System.out.println("Column is already choosen");
                }
                System.out.println("If you want to choose another column print 'Y', else print 'N'");
                String choose = sc.next();
                switch (choose) {
                    case "Y":
                        continue;
                    case "y":
                        continue;
                    default:
                        loopWork = false;
                        break;
                }
            }
            for (int i = 0; i < columnList.size(); i++) {
                if (i != 0) {
                    request += ", ";
                }
                request += columnList.get(i);
            }
            request += ") VALUES (";
            loopWork = true;
            while (loopWork) {
                for (int i = 0; i < columnList.size(); i++) {
                    if (i != 0) {
                        request += ", ";
                    }
                    System.out.println("Enter value for " + columnList.get(i));
                    String value = sc.next();
                    if (isNumeric(value)) {
                        request += value;
                    } else {
                        request += "'" + value + "'";
                    }
                }
                request += ")";
                System.out.println("If you want to insert another row print 'Y', else print 'N'");
                String choose = sc.next();
                switch (choose) {
                    case "Y":
                        request += ", (";
                        continue;
                    case "y":
                        request += ", (";
                        continue;
                    default:
                        loopWork = false;
                        break;
                }
            }
            request += ";";
            statement.executeQuery(request);

        } catch (Exception e) {

        }
    }

    public static void readTable(String url, Properties authorization) {
        try {
            Connection connection = DriverManager.getConnection(url, authorization);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter table name: ");
            String tableName = sc.next();
            ResultSet table = statement.executeQuery("SELECT * FROM " + tableName);
            table.first();
            for (int i = 1; i <= table.getMetaData().getColumnCount(); i++) {
                System.out.print(table.getMetaData().getColumnName(i) + "\t | \t");
            }
            System.out.println();
            table.beforeFirst();
            while (table.next()) {
                for (int i = 1; i <= table.getMetaData().getColumnCount(); i++) {
                    System.out.print(table.getString(i) + "\t | \t");
                }
                System.out.println();
            }
        } catch (Exception e) {

        }
    }

    public static void deleteRow(String url, Properties authorization) {
        try {
            Connection connection = DriverManager.getConnection(url, authorization);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter table name: ");
            String tableName = sc.next();
            String request = "DELETE FROM " + tableName + " WHERE ";
            ResultSet table = statement.executeQuery("SELECT * FROM " + tableName);
            System.out.println("Choose the column find deleteing row: ");
            table.first();
            for (int i = 1; i <= table.getMetaData().getColumnCount(); i++) {
                System.out.println(i + ". " + table.getMetaData().getColumnName(i));
            }
            int column = sc.nextInt();
            request += table.getMetaData().getColumnName(column) + " = ";
            System.out.println("Insert the value to find deleteing row: ");
            String value = sc.next();
            if (isNumeric(value)) {
                request += value + ";";
            } else {
                request += "'" + value + "'" + ";";
            }
            statement.executeQuery(request);
        } catch (Exception e) {
        }
    }

    ;

    public static void updateRow(String url, Properties authorization) {
        try {
            Connection connection = DriverManager.getConnection(url, authorization);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter table name: ");
            String tableName = sc.next();
            String request = "UPDATE " + tableName + " SET ";
            ResultSet table = statement.executeQuery("SELECT * FROM " + tableName);
            System.out.println("Choose the column to update: ");
            table.first();
            for (int i = 1; i <= table.getMetaData().getColumnCount(); i++) {
                System.out.println(i + ". " + table.getMetaData().getColumnName(i));
            }
            int column = sc.nextInt();
            request += table.getMetaData().getColumnName(column) + " = ";
            System.out.println("Insert the value to update " + table.getMetaData().getColumnName(column) + ": ");
            String value = sc.next();
            if (isNumeric(value)) {
                request += value;
            } else {
                request += "'" + value + "'";
            }
            System.out.println("Choose the column to find updating row: ");
            table.first();
            for (int i = 1; i <= table.getMetaData().getColumnCount(); i++) {
                System.out.println(i + ". " + table.getMetaData().getColumnName(i));
            }
            column = sc.nextInt();
            request += " WHERE " + table.getMetaData().getColumnName(column) + " = ";
            System.out.println("Insert the value to find deleteing row: ");
            value = sc.next();
            if (isNumeric(value)) {
                request += value + ";";
            } else {
                request += "'" + value + "'" + ";";
            }
            statement.executeQuery(request);
        } catch (Exception e) {
        }
    }

    ;

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}