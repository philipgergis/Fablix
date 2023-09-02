function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

// Populate the star table
// Find the empty table body by id "star_table_body"
    let numbers = jQuery("#numbers");
    let rowHTML = "<p>";

    for (let i = 0; i < 10; i++) {

        // Concatenate the html tags with resultData jsonObject
        rowHTML += '<a href="movie-list.html?queryType=browse&sort=rating&ratingSort=DESC&titleSort=ASC&limitNum=25&offset=0&character=' + i + '&genre=">'
            + i +
            '</a>';
        rowHTML += "   ";


        // Append the row created to the table body, which will refresh the page
    }
    rowHTML += "</p>";
    numbers.append(rowHTML);

    let alphabet = jQuery("#letters");
    rowHTML = "<p>";
    let alpha = "abcdefghijklmnopqrstuvqxyz";
    for (let i = 0; i < alpha.length; i++) {
        rowHTML += '<a href="movie-list.html?queryType=browse&sort=rating&ratingSort=DESC&titleSort=ASC&limitNum=25&offset=0&character=' + alpha[i] + '&genre=">'
            + alpha[i] +
            '</a>';
        rowHTML += "   ";
    }
    rowHTML += '<a href="movie-list.html?queryType=browse&sort=rating&ratingSort=DESC&titleSort=ASC&limitNum=25&offset=0&character=*&genre=">' + '*' + '</a>';
    rowHTML += "   " + "</p>";
    alphabet.append(rowHTML);


    let genres = jQuery("#genres");
    rowHTML = "<p>";

    for(let i =1; i<resultData.length; i++){
        rowHTML += '<a href="movie-list.html?queryType=browse&sort=rating&ratingSort=DESC&titleSort=ASC&limitNum=25&offset=0&character=&genre=' + resultData[i]["movie_genre"] + '">'
            + resultData[i]["movie_genre"] +
            '</a>';
        rowHTML += "   ";
    }
    rowHTML += "</p>";
    genres.append(rowHTML)

    let homeHead = jQuery("#home-link");
    homeHead.append("<a href = '" + resultData[0]["home_url"] + "'>Home</a>")
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/browse", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

