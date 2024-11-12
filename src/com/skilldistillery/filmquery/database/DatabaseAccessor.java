package com.skilldistillery.filmquery.database;

import java.sql.SQLException;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public interface DatabaseAccessor {
	public Film findFilmById(int filmId) throws SQLException;

	public Actor findActorById(int actorId) throws SQLException;

	public List<Actor> findActorsByFilmId(int filmId) throws SQLException;
	
	public Actor createActor(Actor actor) throws SQLException;
	
	public boolean saveActor(Actor actor) throws SQLException;
	
	public boolean deleteActor(Actor actor) throws SQLException;
	
	public Film createFilm(Film aFilm) throws SQLException;

	public List<Film> searchFilms(String keyword) throws SQLException;

}
