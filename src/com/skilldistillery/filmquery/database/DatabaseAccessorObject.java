package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;
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

		String sql = "SELECT actor.id, actor.first_name, actor.last_name "
	               + "FROM actor "
	               + "JOIN film_actor ON actor.id = film_actor.actor_id "
	               + "WHERE film_actor.film_id = ?";
		
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
}