CREATE SCHEMA IF NOT EXISTS moviedb;

DROP TABLE IF EXISTS moviedb.stars_in_movies; 
DROP TABLE IF EXISTS moviedb.genres_in_movies; 
DROP TABLE IF EXISTS moviedb.sales; 
DROP TABLE IF EXISTS moviedb.customers;
DROP TABLE IF EXISTS moviedb.ratings;
DROP TABLE IF EXISTS moviedb.stars;
DROP TABLE IF EXISTS moviedb.movies;
DROP TABLE IF EXISTS moviedb.genres;
DROP TABLE IF EXISTS moviedb.creditcards;


CREATE TABLE moviedb.movies(
	id varchar(10) NOT NULL,
	title varchar(100) NOT NULL,
	year integer NOT NULL,
	director varchar(100) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE moviedb.stars(
	id varchar(10) NOT NULL,
	name varchar(100) NOT NULL,
	birthYear integer,
	PRIMARY KEY(id)
);

CREATE TABLE moviedb.genres( 
	id integer NOT NULL AUTO_INCREMENT,
	name varchar(32) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE moviedb.creditcards(
	id varchar(20) NOT NULL,
	firstName varchar(50) NOT NULL,
	lastName varchar(50) NOT NULL,
	expiration date NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE moviedb.customers( 
	id integer NOT NULL AUTO_INCREMENT,
	firstName varchar(50) NOT NULL,
	lastName varchar(50) NOT NULL,
	ccId varchar(20),
	address varchar(200) NOT NULL,
	email varchar(50) NOT NULL,
	password varchar(20) NOT NULL,
	PRIMARY KEY(id), 
	FOREIGN KEY (ccId) REFERENCES moviedb.creditcards(id)
);

CREATE TABLE moviedb.ratings(
	movieId varchar(10) NOT NULL,
	rating float NOT NULL,
	numVotes integer NOT NULL,
	FOREIGN KEY(movieId) REFERENCES moviedb.movies(id)
);

CREATE TABLE moviedb.sales(
	id integer NOT NULL AUTO_INCREMENT,
	customerId integer NOT NULL,
	movieId varchar(10) NOT NULL,
	saleDate date NOT NULL,
	PRIMARY KEY(id), 
	FOREIGN KEY(customerId) REFERENCES moviedb.customers(id),
	FOREIGN KEY(movieId) REFERENCES moviedb.movies(id)
);

CREATE TABLE moviedb.stars_in_movies(
	starID varchar(10) NOT NULL,
	movieID varchar(10) NOT NULL, 
	FOREIGN KEY(starID) REFERENCES moviedb.stars(id),
	FOREIGN KEY(movieID) REFERENCES moviedb.movies(id)
);

CREATE TABLE moviedb.genres_in_movies(
	genreID integer NOT NULL, 
	movieID varchar(10) NOT NULL, 
	FOREIGN KEY(genreID) REFERENCES moviedb.genres(id),
	FOREIGN KEY(movieID) REFERENCES moviedb.movies(id)
);



