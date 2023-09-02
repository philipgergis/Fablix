/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Movie Name: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Release Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Director: " + resultData[0]["movie_director"] + "</p>"+
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>");

    starInfoElement.append("<p>" +
        "<form action='shopping-cart.html'>" +
        "    <input type=\"hidden\" name=\"title\" value=\"" + resultData[0]["movie_title"] + "\" />" +
        "    <input type=\"hidden\" name=\"add\" value=\"1\" />" +
        "    <input type=\"submit\" value=\"Add Movie\" />" + "</form>" + "</p>");

    let genreRow = '<p> Genre(s): ';
    let genres = resultData[0]["movie_genre"];
    const genreNames = genres.split(", ");
    for(let j=0; j < genreNames.length; j++){
            genreRow += '<a href="movie-list.html?queryType=browse&sort=rating&ratingSort=DESC&titleSort=ASC&limitNum=25&offset=0&character=&genre=' + genreNames[j] + '">' + genreNames[j] + ' </a> ';
    }
    genreRow += "</p>";
    starInfoElement.append(genreRow);

    let starList = "<p>" + "Star(s):" + "</p>";
    const starNames = resultData[0]["movie_star"].split(",");
    const starIds = resultData[0]["star_id"].split(",");
    for (let i = 0; i < starNames.length; i++)
    {
        starList +=
            "<p>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-star.html?id=' + starIds[i] + '">'
            + starNames[i] +  " " +   // display star_name for the link text
            '</a>' +
            "</p>";
    }
    starInfoElement.append(starList);

    let homeHead = jQuery("#home-link");
    homeHead.append("<a href = '" + resultData[0]["home_url"] + "'>Home</a>")
    //console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    //let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows

}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});