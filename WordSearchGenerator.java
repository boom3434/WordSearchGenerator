import java.util.*;
import java.io.*;

public class WordSearchGenerator {
	public static void print(char[][] wordSearch) throws FileNotFoundException {
		Scanner sc = new Scanner(System.in);
		boolean propChoice = false;
		System.out.println("Enter 1 to print to console");
		System.out.println("Enter 0 to print to a file");
		String userInput = sc.nextLine();
		int choice = -1;
		try {
			choice = Integer.parseInt(userInput);
		} catch (NumberFormatException e) {
			System.out.println("That wasn't an integer. Please try again");
		}
		if (choice == 1) {
			for (int i = 0; i < wordSearch.length; i++) {
				for (int j = 0; j < wordSearch[i].length; j++) {
					System.out.print(wordSearch[i][j] + " ");
				}
				System.out.println();
			}
		} else if (choice == 0) {
			System.out.println("Please enter an output pathfile:");
			String outFilePath = sc.nextLine();
			PrintStream output = new PrintStream(new File(outFilePath));
			for (int i = 0; i < wordSearch.length; i++) {
				for (int j = 0; j < wordSearch[i].length; j++) {
					output.print(wordSearch[i][j] + " ");
				}
				output.println();
			}
			System.out.println("Successfully printed to "+outFilePath);
		}
	}

	public static char printIntro() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please select an option:");
		System.out.println("Generate a new word search (g)");
		System.out.println("Print out your word search (p)");
		System.out.println("Show the solution to your word search (s)");
		System.out.println("Quit the program (q)");
		String responseStr = sc.nextLine();
		char response = responseStr.toLowerCase().charAt(0);
		if (response != 'g' && response != 'p' && response != 's' && response != 'q') {
			System.out.println("That wasn't an option! Please try again...");
			return 'z';
		}
		return response;
	}

	public static char[][] generate() {
		// get list of words
		int longestWord = 0;
		Queue<String> wordList = new LinkedList<>();
		boolean correctChoice = false;
		while (!correctChoice) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter 1 if you would like to enter words one at a time");
			System.out.println("Or enter 0 if you'd like to input a file of words separated by spaces");
			String str2Parse = sc.nextLine();
			int choice = -1;
			try {
				choice = Integer.parseInt(str2Parse);
			} catch (NumberFormatException e) {
				System.out.println(str2Parse + " is not a valid option");
			}

			if (choice == 1) {
				System.out.println("Please enter the number of words you'd like to put into the word search:");
				str2Parse = sc.nextLine();
				int wordCount = 0;
				try {
					wordCount = Integer.parseInt(str2Parse);
				} catch (NumberFormatException e) {
					System.out.println(str2Parse + " is not an integer");
				}
				for (int i = 0; i < wordCount; i++) {
					boolean goodInput = false;
					String word = "1";
					while (!goodInput) {
						System.out.println("Please enter a word containing only letters: ");
						word = sc.next();
						goodInput = word.chars().allMatch(Character::isLetter);
					}
					int temp = word.length();
					if (temp > longestWord) {
						longestWord = temp;
					}
					wordList.add(word);
				}
				correctChoice = true;
			} else if (choice == 0) {
				System.out.println("Please enter input file path:");
				String inFilePath = sc.nextLine();
				File f = new File(inFilePath);
				Scanner input = null;
				try {
					input = new Scanner(f);
				} catch (FileNotFoundException e) {
					System.out.println("Couldn't find your file. Please try again.");
				}

				while (input.hasNext()) {
					String inWord = input.next();
					wordList.add(inWord);
					int temp = inWord.length();
					if (temp > longestWord) {
						longestWord = temp;
					}
				}

				correctChoice = true;
			}
		}

		int totalLength = longestWord * 5;
		char[][] wordSearch = new char[totalLength][totalLength];
		int[][] available = new int[totalLength][totalLength];

		for (int i = 0; i < available.length; i++) {
			for (int j = 0; j < available.length; j++) {
				available[i][j] = 1;
			}
		}

		while (!wordList.isEmpty()) {
			boolean horizontal = false;
			boolean vertical = false;
			char[] insertWord = wordList.remove().toCharArray();
			int column = (int) (Math.floor(Math.random() * (totalLength)));
			int row = (int) (Math.floor(Math.random() * (totalLength)));

			if (totalLength - row < insertWord.length) {
				horizontal = true;
			}
			if (totalLength - column < insertWord.length) {
				vertical = true;
			}
			boolean avail = availCheck(available, row, column, insertWord, vertical, horizontal);

			while (!avail) {
				column = (int) (Math.floor(Math.random() * (totalLength)));
				row = (int) (Math.floor(Math.random() * (totalLength)));
				avail = availCheck(available, row, column, insertWord, vertical, horizontal);
			}

			if (!vertical && horizontal) {
				for (int i = 0; i < insertWord.length; i++) {
					wordSearch[row][column + i] = insertWord[i];
					available[row][column + i] = 0;
				}
			} else if (vertical && !horizontal) {
				for (int i = 0; i < insertWord.length; i++) {
					wordSearch[row + i][column] = insertWord[i];
					available[row + i][column] = 0;
				}
			} else if (vertical && horizontal) {
				for (int i = 0; i < insertWord.length; i++) {
					wordSearch[row - i][column] = insertWord[(insertWord.length - 1) - i];
					available[row - i][column] = 0;
				}
			} else {
				int direction = (int) Math.floor(Math.random());
				if (direction == 0) {
					for (int i = 0; i < insertWord.length; i++) {
						wordSearch[row][column + i] = insertWord[i];
						available[row][column + i] = 0;
					}
				} else {
					for (int i = 0; i < insertWord.length; i++) {
						wordSearch[row + i][column] = insertWord[i];
						available[row + i][column] = 0;
					}
				}
			}
		}

		return wordSearch;
	}

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Welcome to my word search generator!");
		System.out.println("This program will allow you to generate your own word search puzzle");
		char[][] wordSearch = null;
		char[][] solution = null;
		boolean more2Do = true;
		while (more2Do) {
			char choice = printIntro();
			if (choice == 'g') {
				wordSearch = generate();
				solution = wordSearch;
			} else if (choice == 'p') {
				wordSearch = fill(wordSearch);
				print(wordSearch);
			} else if (choice == 's') {
				print(solution);
			} else if (choice == 'z') {
				printIntro();
			} else if (choice == 'q') {
				System.out.println("Thanks for playing!");
				more2Do = false;
			}
		}
	}

	public static boolean availCheck(int[][] available, int row, int column, char[] insertWord, boolean vertical,
			boolean horizontal) {
		if (vertical && !horizontal) {
			for (int i = 0; i < insertWord.length; i++) {

				if (available[row + i][column] == 0) {
					return false;
				}
			}
		} else if (horizontal && !vertical) {
			for (int i = 0; i < insertWord.length; i++) {
				if (available[row][column + i] == 0) {
					return false;
				}
			}
		} else if (horizontal && vertical) {
			for (int i = insertWord.length - 1; i >= 0; i--) {
				if (available[row - i][column] == 0) {
					return false;
				}
			}
		}
		return true;
	}

	public static char[][] fill(char[][] wordSearch) {
		for (int i = 0; i < wordSearch.length; i++) {
			for (int j = 0; j < wordSearch[i].length; j++) {

				if (wordSearch[i][j] == '\0') {
					Random rnd = new Random();
					char insertSearch = (char) ('a' + rnd.nextInt(26));
					wordSearch[i][j] = insertSearch;

				}
			}
		}
		return wordSearch;
	}
}