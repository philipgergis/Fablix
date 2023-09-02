let payment_form = $("#payment_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */

function handleStarResult(resultData) {

    let cartTableElement = jQuery("#cart_table_body")
    let total = 0;
    for (let i = 2; i < resultData.length; i++)
    {
        total += parseFloat((Math.round(parseInt(resultData[i]["movie_quantity"]) * parseFloat(resultData[i]["movie_cost"]) * 100)/100).toFixed(2));
        let rowHTML = "<tr>";
        rowHTML += "<th>" + resultData[i]["sale_id"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_quantity"] + "</th>";
        rowHTML += "<th>" + parseFloat(resultData[i]["movie_cost"]).toFixed(2) + "</th>";
        rowHTML += "<th>" + (Math.round(parseInt(resultData[i]["movie_quantity"]) * parseFloat(resultData[i]["movie_cost"]) * 100)/100).toFixed(2) + "</th></tr>";
        cartTableElement.append(rowHTML);
    }
    cartTableElement.append("<tr><th></th><th></th><th></th><th></th><th>Total: " + total.toFixed(2) + " </th></tr>");


    let homeHead = jQuery("#home-link");
    homeHead.append("<a href = '" + resultData[0]["home_url"] + "'>Home</a>")

    // let total = jQuery("#payment_total");
    // total.append("Total: " + parseFloat(resultData[1]["total"]).toFixed(2));
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let urlToUse = "api/confirmation";

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: urlToUse, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

