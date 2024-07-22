package com.test.account;

import java.util.Scanner;
import static java.lang.Class.forName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main_Menu {

	private static final String url = "jdbc:mysql://localhost:3306/assignment";
	private static final String username = "root";
	private static final String password = "9674040677";

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("okay");
		// TODO Auto-generated method stub
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.println("1. Add New Customer Account");
				System.out.println("2. View Account Details");
				System.out.println("3. Close Account");
				System.out.println("4. Deposit Funds");
				System.out.println("5. Withdraw Funds");
				System.out.println("6. Transfer Funds");
				System.out.println("7. Exit");
				System.out.print("Choose an option: ");
				int choice = scanner.nextInt();
				switch (choice) {
				case 1:

					String open_account_query = "INSERT INTO Account(account_id, customer_id, account_type, balance) VALUES(?, ?, ?, ?)";
					scanner.nextLine();
					int c_id = 1;
					System.out.print("Enter Account Id: ");
					int acc_id = scanner.nextInt();
					System.out.print("Enter Initial Amount: ");
					double balance = scanner.nextDouble();
					scanner.nextLine();
					System.out.print("Enter Account Type ");
					String account_type = scanner.nextLine();
					try {

						PreparedStatement preparedStatement = connection.prepareStatement(open_account_query);
						long account_number = acc_id;
						preparedStatement.setLong(1, account_number);
						preparedStatement.setInt(2, c_id);

						preparedStatement.setString(3, account_type);
						preparedStatement.setDouble(4, balance);

						int rowsAffected = preparedStatement.executeUpdate();
						if (rowsAffected > 0) {
							System.out.println("Account Added Successfully");
						} else {
							throw new RuntimeException("Account Creation failed!!");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;
				case 2:

					System.out.print("Enter Account_id ");
					acc_id = scanner.nextInt();
					try {
						PreparedStatement preparedStatement = connection
								.prepareStatement("SELECT balance FROM Account WHERE account_id = ?");
						preparedStatement.setLong(1, acc_id);

						ResultSet resultSet = preparedStatement.executeQuery();
						if (resultSet.next()) {
							balance = resultSet.getDouble("balance");
							System.out.println("Balance: " + balance);
						} else {
							System.out.println("Invalid Id!");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;

				case 3:
					// Update account information
					System.out.print("Enter Account_id ");
					acc_id = scanner.nextInt();
					try {
						PreparedStatement preparedStatement = connection
								.prepareStatement("DELETE FROM Account WHERE account_id = ?");
						preparedStatement.setLong(1, acc_id);
						int rowsAffected = preparedStatement.executeUpdate();
						if (rowsAffected > 0) {
							System.out.println("Account Removed Successfully");
						} else {
							throw new RuntimeException("Account Removing failed!!");
						}

					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;

				case 4: {
					System.out.print("Enter Amount: ");
					double amount = scanner.nextDouble();
					scanner.nextLine();
					System.out.print("Enter Account_Id: ");
					acc_id = scanner.nextInt();

					try {
						connection.setAutoCommit(false);
						if (acc_id != 0) {
							PreparedStatement preparedStatement = connection
									.prepareStatement("SELECT * FROM Account WHERE account_id = ?");
							preparedStatement.setLong(1, acc_id);

							ResultSet resultSet = preparedStatement.executeQuery();

							if (resultSet.next()) {
								String credit_query = "UPDATE Account SET balance = balance + ? WHERE account_id = ?";
								PreparedStatement preparedStatement1 = connection.prepareStatement(credit_query);
								preparedStatement1.setDouble(1, amount);
								preparedStatement1.setLong(2, acc_id);
								int rowsAffected = preparedStatement1.executeUpdate();
								if (rowsAffected > 0) {
									System.out.println("Rs." + amount + " credited Successfully");
									connection.commit();
									connection.setAutoCommit(true);
									return;
								} else {
									System.out.println("Transaction Failed!");
									connection.rollback();
									connection.setAutoCommit(true);
								}
							} else {
								System.out.println("Invalid Security Pin!");
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					connection.setAutoCommit(true);
				}

					break;
				case 5: {
					scanner.nextLine();
					System.out.print("Enter Amount: ");
					double amount = scanner.nextDouble();
					scanner.nextLine();
					System.out.print("Enter Account_id: ");
					acc_id = scanner.nextInt();
					try {
						connection.setAutoCommit(false);
						if (acc_id != 0) {
							PreparedStatement preparedStatement = connection
									.prepareStatement("SELECT * FROM Account WHERE account_id = ?");
							preparedStatement.setLong(1, acc_id);

							ResultSet resultSet = preparedStatement.executeQuery();

							if (resultSet.next()) {
								double current_balance = resultSet.getDouble("balance");
								if (amount <= current_balance) {
									String debit_query = "UPDATE Account SET balance = balance - ? WHERE account_id = ?";
									PreparedStatement preparedStatement1 = connection.prepareStatement(debit_query);
									preparedStatement1.setDouble(1, amount);
									preparedStatement1.setLong(2, acc_id);
									int rowsAffected = preparedStatement1.executeUpdate();
									if (rowsAffected > 0) {
										System.out.println("Rs." + amount + " debited Successfully");
										connection.commit();
										connection.setAutoCommit(true);
										return;
									} else {
										System.out.println("Transaction Failed!");
										connection.rollback();
										connection.setAutoCommit(true);
									}
								} else {
									System.out.println("Insufficient Balance!");
								}
							} else {
								System.out.println("Invalid Pin!");
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					connection.setAutoCommit(true);
				}
					break;
				case 6: {
					scanner.nextLine();
					System.out.print("Enter Sender Account Id: ");
					int sender_account_number = scanner.nextInt();
					System.out.print("Enter Receiver Account Id: ");
					int receiver_account_number = scanner.nextInt();
					System.out.print("Enter Amount: ");
					double amount = scanner.nextDouble();
					scanner.nextLine();

					try {
						connection.setAutoCommit(false);
						if (sender_account_number != 0 && receiver_account_number != 0) {
							PreparedStatement preparedStatement = connection
									.prepareStatement("SELECT * FROM Account WHERE account_id = ? ");
							preparedStatement.setInt(1, sender_account_number);

							ResultSet resultSet = preparedStatement.executeQuery();

							if (resultSet.next()) {
								double current_balance = resultSet.getDouble("balance");
								if (amount <= current_balance) {

									// Write debit and credit queries
									String debit_query = "UPDATE Account SET balance = balance - ? WHERE account_id = ?";
									String credit_query = "UPDATE Account SET balance = balance + ? WHERE account_id = ?";

									// Debit and Credit prepared Statements
									PreparedStatement creditPreparedStatement = connection
											.prepareStatement(credit_query);
									PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);

									// Set Values for debit and credit prepared statements
									creditPreparedStatement.setDouble(1, amount);
									creditPreparedStatement.setInt(2, receiver_account_number);
									debitPreparedStatement.setDouble(1, amount);
									debitPreparedStatement.setInt(2, sender_account_number);
									int rowsAffected1 = debitPreparedStatement.executeUpdate();
									int rowsAffected2 = creditPreparedStatement.executeUpdate();
									if (rowsAffected1 > 0 && rowsAffected2 > 0) {
										System.out.println("Transaction Successful!");
										System.out.println("Rs." + amount + " Transferred Successfully");
										connection.commit();
										connection.setAutoCommit(true);
										return;
									} else {
										System.out.println("Transaction Failed");
										connection.rollback();
										connection.setAutoCommit(true);
									}
								} else {
									System.out.println("Insufficient Balance!");
								}
							} else {
								System.out.println("Invalid Security Pin!");
							}
						} else {
							System.out.println("Invalid account number");
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					connection.setAutoCommit(true);
				}
					break;

				case 7:
					System.exit(0);
					break;
				default:
					System.out.println("Invalid option. Please try again.");
				}
			}
		}

	}

}
