package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {
	private static final String URL = "jdbc:mysql://localhost:3306/sdvid";

	public DatabaseAccessorObject() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public boolean deleteActor(Actor actor) {
		String user = "student";
		String pass = "student";
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(URL, user, pass);
			// start the transaction
			conn.setAutoCommit(false);

			// film_actor is a child of (depends upon) both actor and film tables
			String sql = "DELETE FROM film_actor WHERE actor_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actor.getId());
			stmt.executeUpdate();

			// child rows for this actor are gone, can remove the Actor (parent) now
			sql = "DELETE FROM actor WHERE id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actor.getId());
			stmt.executeUpdate();

			conn.commit();
			conn.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			return false;
		}
		return true;
	}

	public boolean saveActor(Actor actor) {
		String user = "student";
		String pass = "student";
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(URL, user, pass);
			// start the transaction
			conn.setAutoCommit(false);

			String sql = "UPDATE actor SET first_name=?, last_name=?  WHERE id=?";
			PreparedStatement stmt = conn.prepareStatement(sql);

			stmt.setString(1, actor.getFirstName());
			stmt.setString(2, actor.getLastName());
			stmt.setInt(3, actor.getId());

			int updateCount = stmt.executeUpdate();

			if (updateCount == 1) {
				// We don't know which (if any) of the actor's films have changed, so
				// we will replace all the film ids currently associated with this actor

				// remove the old film ids
				sql = "DELETE FROM film_actor WHERE actor_id = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, actor.getId());
				stmt.executeUpdate();

				// insert the current film ids
				sql = "INSERT INTO film_actor (film_id, actor_id) VALUES (?,?)";
				stmt = conn.prepareStatement(sql);

				// iterate through all the actor's current film ids, to
				// (re)associate them with this actor
				for (Film film : actor.getFilms()) {
					stmt.setInt(1, film.getId());
					stmt.setInt(2, actor.getId());
					updateCount = stmt.executeUpdate();
				}

				// all data associated with the actor has been updated, so
				// let's commit now
				conn.commit();
				conn.close();
			}
		} catch (SQLException sqle) {
			// something went wrong, so the above commit() was never called
			// let's undo what we did
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			// not successful in the update
			return false;
		}
		// we rocked the update!
		return true;
	}

	public Actor createActor(Actor actor) {
		// each method manages its own connection
		String user = "student";
		String pass = "student";
		Connection conn = null;

		try {
			conn = DriverManager.getConnection(URL, user, pass);
			// start a transaction
			conn.setAutoCommit(false);

			// We'll be filling in the actor's first and last names
			String sql = "INSERT INTO actor (first_name, last_name) VALUES (?,?)";

			// compile / optimize the sql into the db, and request the generated keys be
			// accessable
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			// bind (assign) the name fields into our sql statements bind vars
			stmt.setString(1, actor.getFirstName());
			stmt.setString(2, actor.getLastName());

			// run the query in the database
			int updateCount = stmt.executeUpdate();

			// check if the INSERT was successful in creating 1 new Actor
			if (updateCount == 1) {
				// good news: we can grab this new Actor's id
				ResultSet keys = stmt.getGeneratedKeys();

				// we're expecting just 1 generated key
				if (keys.next()) {
					// grab the generated key (id)
					int newActorId = keys.getInt(1);

					// change the initial id in our Java entity to actor's 'real' id
					actor.setId(newActorId);

					// see if this new actor has been in previous films
					if (actor.getFilms() != null && actor.getFilms().size() > 0) {
						sql = "INSERT INTO film_actor (film_id, actor_id) VALUES (?,?)";
						stmt = conn.prepareStatement(sql);

						// associate each film they were in with their new Actor id
						for (Film film : actor.getFilms()) {
							stmt.setInt(1, film.getId());
							stmt.setInt(2, newActorId);
							updateCount = stmt.executeUpdate();
						}
					}

				}

				// an explicit commit of the transaction is required to prevent a rollback
				conn.commit();

			} else {
				// something went wrong with the INSERT
				actor = null;
			}

			conn.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException sqle2) {
					System.err.println("Error trying to rollback");
				}
			}
			throw new RuntimeException("Error inserting actor " + actor);
		}

		return actor;
	}

	@Override
	public Film createFilm(Film aFilm) throws SQLException {
		String name = "student";
		String pass = "student";

		String sql = "SELECT * FROM film";

		try (Connection conn = DriverManager.getConnection(URL, name, pass);
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String description = rs.getString("description");
				Integer releaseYear = rs.getInt("release_year");
				int languageId = rs.getInt("language_id");
				int rentalDuration = rs.getInt("rental_duration");
				double rentalRate = rs.getDouble("rental_rate");
				int length = rs.getInt("length");
				double replacementCost = rs.getDouble("replacement_cost");
				String rating = rs.getString("rating");
				String specialFeatures = rs.getString("special_features");

				Film film = new Film(id, title, description, releaseYear, languageId, rentalDuration, rentalRate,
						length, replacementCost, rating, specialFeatures);

				List<Actor> actors = findActorsByFilmId(film.getId());
				film.setActors(actors);
				actors.add(length, findActorById(id));

				rs.close();
				ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return aFilm;
	}

	@Override
	public Film findFilmById(int filmId) throws SQLException {
		Film film = null;
		String name = "student";
		String pass = "student";

		Connection conn = DriverManager.getConnection(URL, name, pass);
		String sql = "SELECT * FROM film WHERE id = ?";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, filmId);
		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			int id = rs.getInt("id");
			String title = rs.getString("title");
			String description = rs.getString("description");
			Integer releaseYear = rs.getInt("release_year");
			int languageId = rs.getInt("language_id");
			int rentalDuration = rs.getInt("rental_duration");
			double rentalRate = rs.getDouble("rental_rate");
			int length = rs.getInt("length");
			double replacementCost = rs.getDouble("replacement_cost");
			String rating = rs.getString("rating");
			String specialFeatures = rs.getString("special_features");

			film = new Film(id, title, description, releaseYear, languageId, rentalDuration, rentalRate, length,
					replacementCost, rating, specialFeatures);

			// the film exists now
			List<Actor> actors = findActorsByFilmId(film.getId());
			// now set its actors
			film.setActors(actors);

			rs.close();
			ps.close();
			conn.close();

		}
		return film;
	}

	@Override
	public Actor findActorById(int actorId) throws SQLException {
		Actor actor = null;
		String name = "student";
		String pass = "student";

		String sql = "SELECT id, first_name, last_name FROM actor WHERE id = ?";
		Connection conn = DriverManager.getConnection(URL, name, pass);
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setInt(1, actorId);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			int id = rs.getInt("id");
			String firstName = rs.getString("first_name");
			String lastName = rs.getString("last_name");

			actor = new Actor(id, firstName, lastName);
			List<Actor> actors = findActorsByFilmId(actorId);
			actors.add(actor);

		}
		rs.close();
		ps.close();
		conn.close();

		return actor;
	}

	@Override
	public List<Actor> findActorsByFilmId(int filmId) throws SQLException {
		List<Actor> actors = new ArrayList<>();
		String name = "student";
		String pass = "student";

		String sql = "SELECT actor.id, actor.first_name, actor.last_name " + "FROM actor "
				+ "JOIN film_actor ON actor.id = film_actor.actor_id " + "WHERE film_actor.film_id = ?";

		try (Connection conn = DriverManager.getConnection(URL, name, pass);
				PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, filmId);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int id = rs.getInt("id");
					String firstName = rs.getString("first_name");
					String lastName = rs.getString("last_name");

					Actor actor = new Actor(id, firstName, lastName);
					actors.add(actor);

				}
				rs.close();
				ps.close();
				conn.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return actors;
	}

	@Override
	public List<Film> searchFilms(String keyword) throws SQLException {
		List<Film> result = new ArrayList<>();
		String name = "student";
		String pass = "student";

		String sql = "SELECT * FROM film WHERE title = ?";

		Connection conn = DriverManager.getConnection(URL, name, pass);
		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setString(1, keyword);
		try (ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String description = rs.getString("description");
				Integer releaseYear = rs.getInt("release_year");
				int languageId = rs.getInt("language_id");
				int rentalDuration = rs.getInt("rental_duration");
				double rentalRate = rs.getDouble("rental_rate");
				int length = rs.getInt("length");
				double replacementCost = rs.getDouble("replacement_cost");
				String rating = rs.getString("rating");
				String specialFeatures = rs.getString("special_features");

				Film film = new Film(id, title, description, releaseYear, languageId, rentalDuration, rentalRate,
						length, replacementCost, rating, specialFeatures);

				result.add(film);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
}