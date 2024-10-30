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
		List<Actor> films;

		try {
			{

			film = db.findFilmById(779);
			System.out.println(film.getTitle() + " " + film.getDescription());
			actor = db.findActorById(6);
			System.out.println(actor.getActorId() + " " + actor.getFirstName() + " " + actor.getLastName());
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
