package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
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
//    app.launch();
	}

	private void test() {
		Film film;
		Actor actor;
		List<Actor> actors;

		try {
			{
				film = db.findFilmById(16);
				if (film != null) {
					System.out.println(film.getTitle() + " " + film.getDescription());
					System.out.println("Actors in the Film:");
					for (Actor actorId : film.getActors()) {
						System.out.println(" - " + actorId.getFirstName() + " " + actorId.getLastName());
					}
				} else {
					System.out.println("Film not found.");
				}

				System.out.println();

				actor = db.findActorById(16);
				if (actor != null) {
					System.out.println(actor.getId() + " " + actor.getFirstName() + " " + actor.getLastName());
				} else {
					System.out.println("Actor not found.");
				}

				System.out.println();

				List<Actor> actorByFilm = db.findActorsByFilmId(12);
				System.out.println("Actors in Film ID: ");
				for (Actor a : actorByFilm) {
					System.out.println(a.getId() + " - " + a.getFirstName() + " " + a.getLastName());
				}

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

	}

}
