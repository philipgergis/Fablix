/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
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

function setSortParam(param, urlParam) {
    let currentUrl = window.location.href;
    let urlNew = new URL(currentUrl);
    urlNew.searchParams.set(param, urlParam);
    let finalUrl = urlNew.href;
    window.location.replace(finalUrl);

}

function changePage(value)
{
    let tempOffset = value + parseInt(offset);
    setSortParam("offset", ""+tempOffset);
}

function changeLimit(value)
{
    let entryForm = jQuery("#" + value);
    let formValue = entryForm.val();

    let currentUrl = window.location.href;
    let urlNew = new URL(currentUrl);
    urlNew.searchParams.set("limitNum", ""+formValue);
    urlNew.searchParams.set("offset", "0");
    let finalUrl = urlNew.href;
    window.location.replace(finalUrl);
}

function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">'
            + resultData[i]["movie_title"] +
            '</a>' +
            "<form action='shopping-cart.html'>" +
            "    <input type=\"hidden\" name=\"id\" value=\"" + resultData[i]["movie_id"] + "\" />" +
            "    <input type=\"hidden\" name=\"title\" value=\"" + resultData[i]["movie_title"] + "\" />" +
            "    <input type=\"hidden\" name=\"add\" value=\"1\" />" +
            "    <input type=\"submit\" value=\"+\" />" + "</form>" +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        if(resultData[i]["movie_votes"] == 0)
        {
            rowHTML += "<th>" + "N/A" + "</th>";
        }
        else
        {
            rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        }


        rowHTML += "<th>";


        let genres = resultData[i]["movie_genre"];
        const genreNames = genres.split(", ");
        for(let j=0; j < genreNames.length; j++){
            rowHTML +=
                '<a href="movie-list.html?queryType=browse&sort=rating&ratingSort=DESC&titleSort=ASC&limitNum=25&offset=0&character=&genre=' + genreNames[j] + '">' + genreNames[j] + ' </a> ';
        }

        rowHTML += "</th>";

        let count = resultData[i]['star_count'];
        const starNames = resultData[i]["movie_star"].split(",");
        const starIds = resultData[i]["star_id"].split(",");
        for (let j = 0; j < count; j++)
        {
            rowHTML +=
                "<th>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="single-star.html?id=' + starIds[j] + '">'
                + starNames[j] +     // display star_name for the link text
                '</a>' +
                "</th>";
        }
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }

    let pageButtons = "<tr>";
    if(parseInt(offset) != 0)
    {
        pageButtons += "<th><button onclick='changePage(-1)'>Prev</button></th>"
    }
    if(resultData.length < parseInt(limitNum))
    {

        starTableBodyElement.append("<tr><th>No movies left.</th></tr>");
    }
    else
    {
        pageButtons += "<th><button onclick='changePage(1)'>Next</button></th>";
    }


    pageButtons += "</tr>";
    starTableBodyElement.append(pageButtons);

}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let urlToUse = "api/movie-list?";
let queryType = getParameterByName('queryType');
let sort = getParameterByName('sort')
let ratingSort = getParameterByName('ratingSort');
let titleSort = getParameterByName('titleSort');
let limitNum = getParameterByName('limitNum');
let offset = getParameterByName('offset');

urlToUse += "queryType=" + queryType + "&sort=" + sort + "&ratingSort=" + ratingSort + "&titleSort=" + titleSort + "&limitNum=" + limitNum + "&offset=" + offset;

if(queryType == "search")
{
    let searchStar = getParameterByName('searchStar');
    let searchTitle = getParameterByName('searchTitle');
    let searchYear = getParameterByName('searchYear');
    let searchDirector = getParameterByName('searchDirector');
    urlToUse += "&searchTitle=" + searchTitle + "&searchYear=" + searchYear + "&searchDirector=" + searchDirector + "&searchStar=" + searchStar
}
else if(queryType == "browse")
{
    let character = getParameterByName('character');
    let genre = getParameterByName('genre');
    urlToUse += "&character=" + character + "&genre=" + genre
}


// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: urlToUse, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});