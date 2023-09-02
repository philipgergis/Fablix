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

function deleteEntry(title)
{
    console.log(title);
    console.log(movieTitle);
}

function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    let cartTableElement = jQuery("#cart_table_body")
    let total = 0;
    for (let i = 1; i < resultData.length; i++)
    {
        total += parseFloat((Math.round(parseInt(resultData[i]["movie_quantity"]) * parseFloat(resultData[i]["movie_cost"]) * 100)/100).toFixed(2));
        let rowHTML = "<tr>";
        rowHTML += "<th>" +
            "<form action='shopping-cart.html'>" +
            "    <input type=\"hidden\" name=\"id\" value=\"" + resultData[i]["movie_id"] + "\" />" +
            "    <input type=\"hidden\" name=\"title\" value=\"" + resultData[i]["movie_title"] + "\" />" +
            "    <input type=\"hidden\" name=\"add\" value=\"" + -resultData[i]["movie_quantity"] + "\" />" +
            "    <input type=\"submit\" value=\"X\" />" + "</form>" + "</th>";
        rowHTML += "<th>" +
            "<form action='shopping-cart.html'>" +
            "    <input type=\"hidden\" name=\"id\" value=\"" + resultData[i]["movie_id"] + "\" />" +
            "    <input type=\"hidden\" name=\"title\" value=\"" + resultData[i]["movie_title"] + "\" />" +
            "    <input type=\"hidden\" name=\"add\" value=\"1\" />" +
            "    <input type=\"submit\" value=\"+\" />" + "</form>" +
            "<form action='shopping-cart.html'>" +
            "    <input type=\"hidden\" name=\"id\" value=\"" + resultData[i]["movie_id"] + "\" />" +
            "    <input type=\"hidden\" name=\"title\" value=\"" + resultData[i]["movie_title"] + "\" />" +
            "    <input type=\"hidden\" name=\"add\" value=\"-1\" />" +
            "    <input type=\"submit\" value=\"-\" />" + "</form>" +
            resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_quantity"] + "</th>";
        rowHTML += "<th>" + parseFloat(resultData[i]["movie_cost"]).toFixed(2) + "</th>";
        rowHTML += "<th>" + (Math.round(parseInt(resultData[i]["movie_quantity"]) * parseFloat(resultData[i]["movie_cost"]) * 100)/100).toFixed(2) + "</th></tr>";
        cartTableElement.append(rowHTML);
    }
    cartTableElement.append("<tr><th></th><th></th><th></th><th></th><th>Total: " + total.toFixed(2) + " </th></tr>");

    let homeHead = jQuery("#home-link");
    homeHead.append("<a href = '" + resultData[0]["home_url"] + "'>Home</a>")

    if(movieAdd !== ""+0 )
    {
        setSortParam("add", "0")
    }


}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let urlToUse = "api/shopping-cart?";
let movieTitle = getParameterByName("title");
let movieAdd = getParameterByName("add");
let movieId = getParameterByName("id");
urlToUse += "id=" + movieId + "&title=" + movieTitle + "&add=" + movieAdd;

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: urlToUse, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});