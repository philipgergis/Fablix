function handleStarResult(resultData) {

    let homeHead = jQuery("#home-link");
    homeHead.append("<a href = '" + resultData[0]["home_url"] + "'>Home</a>");
}

jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/search", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");

    if(sessionStorage.getItem(query) != null)
    {
        console.log("Using cache");
        handleLookupAjaxSuccess(sessionStorage.getItem(query), query, doneCallback);
    }
    else
    {
        console.log("Using ajax request");
        // TODO: if you want to check past query results first, you can do it here

        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/auto?searchTitle=" + escape(query),
            "success": function(data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function(errorData) {
                console.log("lookup ajax error");
                console.log(errorData);
            }
        })
    }

}

function handleLookupAjaxSuccess(data, query, doneCallback) {

    // parse the string into JSON
    // var jsonData = JSON.parse(data);

    // TODO: if you want to cache the result into a global variable you can do it here
    if(sessionStorage.getItem(query) == null)
    {
        sessionStorage.setItem(query, JSON.stringify(data));
        console.log("Suggestion List:\n" + JSON.stringify(data));
        doneCallback( { suggestions: data } );
    }
    else
    {
        let newData = JSON.parse(data);
        console.log("Suggestion List:\n" + data);
        doneCallback( { suggestions: newData } );
    }

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation

}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["heroID"]);
    window.location.replace("single-movie.html?id=" + suggestion["data"]["id"]);
}

$('#searchTitle').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3,
});

