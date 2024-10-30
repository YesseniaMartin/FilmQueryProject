package com.skilldistillery.filmquery.entities;

import java.util.List;
import java.util.Objects;

public class Actor {
	private int actorId;
	private String firstName;
	private String lastName;
	private List<Film> films;

	// Methods
	public Actor() {

	}

	public Actor(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;

	}

	public Actor(int sagMemberNumber, String firstName, String lastName) {
		this.actorId = sagMemberNumber;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Actor(int sagMemberNumber, String firstName, String lastName, List<Film> films) {
		this.actorId = sagMemberNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.films = films;
	}

// getters and setters

	public int getActorId() {
		return actorId;
	}

	public void setActorId(int actorId) {
		this.actorId = actorId;
	}

	public int getId() {
		return actorId;
	}

	public List<Film> getFilms() {
		return films;
	}

	public void setFilms(List<Film> films) {
		this.films = films;
	}

	public void setId(int actorId) {
		this.actorId = actorId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(actorId);
	}

	// hascode equals
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		return actorId == other.actorId;
	}

// toString
	@Override
	public String toString() {
		return "Actor [id=" + actorId + ", firstName=" + firstName + ", lastName=" + lastName + "]";
	}

}
