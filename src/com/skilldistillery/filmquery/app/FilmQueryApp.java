package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	DatabaseAccessor db = new DatabaseAccessorObject();

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
		app.test();
//		app.launch();

		Actor actor = new Actor("Norma", "Jean");
	}

	private void test() {
		
		Film film;
		Actor actor;
		List<Actor> actors;
		

		try {
			film = db.findFilmById(16);
			if (film != null) {
				System.out.println(film.getId() + " " + film.getTitle() + " " + film.getDescription());
				System.out.println("Actors in the Film:");
				for (Actor actorId : film.getActors()) {
					System.out.println(" - " + actorId.getFirstName() + " " + actorId.getLastName());
				}
			} else {
				System.out.println("Film not found.");
			}

			System.out.println();

			actor = db.findActorById(17);
			if (actor != null) {
				System.out.println(actor.getId() + " " + actor.getFirstName() + " " + actor.getLastName());
			} else {
				System.out.println("Actor not found.");
			}

			System.out.println();

			actors = db.findActorsByFilmId(12);
			System.out.println("Actors in Film ID: ");

			for (Actor filmActor : actors) {
				System.out
						.println(filmActor.getId() + " - " + filmActor.getFirstName() + " " + filmActor.getLastName());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void launch() {

		Scanner input = new Scanner(System.in);

		startUserInterface(input);

		input.close();
	}

	private void startUserInterface(Scanner input) {

		// menu here
		Scanner menu = new Scanner(System.in);
		boolean running = true;

		while (running) {
			System.out.println("Please select an option (1-3) from the menu below:");
			System.out.println("------------ MENU --------------------");
			System.out.println("1) Look up a film by its id           ");
			System.out.println("2) Look up a film by a search keyword ");
			System.out.println("3) Exit the application               ");
			System.out.println("--------------------------------------");

			int selection;
			if (menu.hasNextInt()) {
				selection = menu.nextInt();
				menu.nextLine();
			} else {
				System.out.println("Invalid input. Please enter a Number between 1 and 3 ");
				menu.nextLine();
				continue;
			}

			try {
				
				switch (selection) {
				case 1:
					System.out.println("Enter the film ID:");
					
					int filmId = menu.nextInt();
					menu.nextLine();
					
					Film film = db.findFilmById(filmId);
					if (film != null) {
						System.out.println("Film found: " + film.getTitle() + ", Year: " + film.getReleaseYear()
								+ ", Rating: " + film.getRentalRate() + ", Description: " + film.getDescription());
					} else {
						System.out.println("No film found with ID: " + filmId);
					}
					break;

				case 2:
					System.out.println("Enter a keyword to search for a film: ");
					String keyword = menu.nextLine();

					List<Film> results = db.searchFilms(keyword);

					if (!results.isEmpty()){
						System.out.println("Numbers of Films found: " + results.size());
						System.out.println("Results of Films found:");
						
						for (Film result : results) {
							System.out.println(
									"Title: " + result.getTitle() + ", Description: " + result.getDescription() + ", Rating: "
											+ result.getRating() + ", Year: " + result.getReleaseYear());
	
						}
					} else {
						System.out.println("No films found matching the keyword: " + keyword);
					}
					break;
					
				case 3:
					System.out.println("Exiting the application...");
					running = false; // Exit the loop
					break;

				default:
					System.out.println("Invalid choice. Please select 1, 2, or 3.");
					break;
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		menu.close();
	}

}
