function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

// Populate the star table
// Find the empty table body by id "star_table_body"

    let homeHead = jQuery("#home-link");
    homeHead.append("<a href = '" + "https://localhost:8080/cs122b_fall_team_20_war/" +  resultData[0]["home_url"] + "'>Home</a>");

    let docs = jQuery("#docs_body");


    let table = resultData[1]["table"];
    let addTable = "<h2>" + table + "</h2>" + "<table class=\"table table-striped\"><thead><tr><th>Attribute</th><th>Type</th></tr></thead><tbody>";
    for (let i = 1; i < resultData.length; i++)
    {
        let newTable = resultData[i]["table"];
        if (newTable != table)
        {
            addTable += "</tbody></table><br><br>";
            docs.append(addTable);
            table = resultData[i]["table"];
            addTable = "<h2>" + table + "</h2>" + "<table class=\"table table-striped\"><thead><tr><th>Attribute</th><th>Type</th></tr></thead><tbody>";
        }
        addTable += "<tr>" +
            "<th>" + resultData[i]["column"] + "</th>" +
            "<th>" + resultData[i]["type"] + "(" + resultData[i]["size"]   + ")" + "</th></tr>"
    }
    addTable += "</tbody></table><br><br>";
    docs.append(addTable);

}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/meta-data", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

