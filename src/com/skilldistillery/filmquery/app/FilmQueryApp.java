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
//		app.test();
    app.launch();
	}

	private void test() {
		Film film;
		Actor actor;
		List<Actor> actors;

		try 
			{
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
					System.out.println(
							filmActor.getId() + " - " + filmActor.getFirstName() + " " + filmActor.getLastName());
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
		List<Film> filmList = new ArrayList<>();
		
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
			
			try 
			{
			switch (selection) {
			case 1:
				System.out.println("Enter the film ID:");
				int filmId = menu.nextInt();
				Film film = db.findFilmById(filmId);
				if (film != null) {
                    System.out.println("Film found: " + film.getTitle() + ", Year: " + film.getReleaseYear() +
                                       ", Rating: " + film.getRentalRate() + ", Description: " + film.getDescription());
                } else {
                    System.out.println("No film found with ID: " + filmId);
                }
				break;
				
			case 2:
				System.out.println("Enter a keyword to search for a film:");
                String keyword = menu.nextLine();
//				searchFilms(filmList, );
				List<Film> results = searchFilms(filmList, keyword);
                if (results.isEmpty()) {
                    System.out.println("No films found matching the keyword: " + keyword);
                } else {
                    System.out.println("Films found:");
                    for (Film result : results) {
                        System.out.println("Title: " + result.getTitle() + ", Year: " + result.getReleaseYear() +
                                           ", Rating: " + result.getRentalRate() + ", Description: " + result.getDescription());
                    }
                }
                break;

            case 3:
                System.out.println("Exiting the application...");
                running = false;  // Exit the loop
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
	public List<Film> searchFilms(List<Film> films, String keyword) {
//		 its title, year, rating, and description 
	    List<Film> result = new ArrayList<>();
	    String lowerCaseKeyword = keyword.toLowerCase();
	    for (Film film : films) {
	    	if (film.getTitle().toLowerCase().contains(lowerCaseKeyword) ||
	            String.valueOf(film.getReleaseYear()).contains(lowerCaseKeyword) ||
	            String.valueOf(film.getRentalRate()).contains(lowerCaseKeyword) ||
	            film.getDescription().toLowerCase().contains(lowerCaseKeyword)) {
	            result.add(film);
	            }
	    	}
	    return result;
	}
	
}
