package ui;

import java.util.Scanner;

import main.Calculator;

public class ConsoleUI {

	public static void main(String[] args) {
		System.out.println("Enter an expression. To stop, type 'stop'.");
		Scanner sc = new Scanner(System.in);
		for (;;) {
			String input = sc.nextLine();
			if (input.equals("stop")) {
				sc.close();
				System.out.println("Application terminated successfully.");
				return;
			}
			System.out.println(Calculator.calculate(input));
		}
	}
}
