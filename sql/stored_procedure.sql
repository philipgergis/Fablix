use moviedb;

-- Change DELIMITER to $$ 
DELIMITER $$ 

DROP Procedure if exists add_movie$$
CREATE PROCEDURE add_movie (
    IN inputTitle VARCHAR(100),
    IN inputYear INTEGER,
    IN inputDirector VARCHAR(100),
    IN inputStar VARCHAR(100),
    IN inputGenre VARCHAR(32))
    startBlock: BEGIN
		-- check if the movie already exists 
        IF ((SELECT COUNT(*) FROM movies WHERE title = inputTitle AND year = inputYear AND director = inputDirector) > 0) THEN
			Select "Movie not added, it already exists." as message;
            LEAVE startBlock; 
        END IF;  

        SET @movieID = concat((select max(substring(id, 1, 2)) from movies) , (select LPAD(((select max(substring(id, 3)) from movies) + 1),7,'0')));
        INSERT INTO movies VALUES (@movieID, inputTitle, inputYear, inputDirector);
        
        -- check to see if the genre exists
        IF(  (SELECT COUNT(*) from genres where name = inputGenre) = 1   ) THEN 
            SET @genreID = (select id from genres where name = inputGenre); 
        END IF; 

        -- check if the genre does NOT exist & add it to the genre table if it doesn't exist 
        IF((SELECT COUNT(*) from genres where name = inputGenre) = 0) THEN 
            SET @genreID = ((select max(id) from genres) + 1);
            INSERT INTO genres VALUES (@genreID, inputGenre);
        END IF; 

        -- link to genres_in_movies
        INSERT INTO genres_in_movies VALUES(@genreID, @movieID);

        IF((SELECT COUNT(*) from stars where name = inputStar) >= 1) THEN 
            SET @starID = (select id from stars where name = inputStar); 
        END IF; 

        -- check if the genre does NOT exist & add it to the genre table if it doesn't exist 
        IF((SELECT COUNT(*) from stars where name = inputStar) = 0) THEN 
            SET @starID = concat(   (select max(substring(id, 1, 2)) from stars)    ,(   select LPAD(((select max(substring(id, 3)) from stars) + 1),7,'0')   ) );
            INSERT INTO stars VALUES (@starID, inputStar, null);
        END IF; 

        -- link to stars_in_movies 
        INSERT INTO stars_in_movies VALUES(@starID, @movieID);



        -- add null values to the ratings
        INSERT INTO ratings VALUES(@movieID, 0, 0);
        
        SELECT concat('Movied added: (Movie ID = ', @movieId, ', Star ID = ', @starId, ', Genre ID = ', @genreId, ")") as message;
END
$$
-- Change back DELIMITER to ; 
DELIMITER ;