package RI.practica2_2;

import java.util.List;
import java.util.Scanner;

public class Main {

	// Únicamente para uso por terminal
	// Para iniciar la interfaz gráfica, iniciar desde SearchGUI
	public static void main(String[] args) throws Exception {

		CustomIndex index = new CustomIndex();

		Scanner in = new Scanner(System.in);

		System.out.print("Do you want to index the CSV files? (y/n): ");
		String wantToIndex = in.nextLine();

		if (wantToIndex.equals("y") || wantToIndex.equals("Y")) {
			index.index();
		}

		System.out.println("Query: ");
		String query = in.nextLine();
		List<DocumentRank> rankedDocuments = index.searchIndex(query);

		if (rankedDocuments.isEmpty()) {
			System.out.println("No documents matched your query.");
		}
		else {
			for (DocumentRank document : rankedDocuments) {
				System.out.println(document);
			}
		}
		in.close();

	}

}