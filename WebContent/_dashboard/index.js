function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

// Populate the star table
// Find the empty table body by id "star_table_body"

    let homeHead = jQuery("#home-link");
   // homeHead.append("<a href = '" + resultData[0]["home_url"] + "'>Home</a>")
    homeHead.append("<a href = '" + "https://localhost:8080/cs122b_fall_team_20_war/" +  resultData[0]["home_url"] + "'>Home</a>");
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/_dashboard-home-page", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

