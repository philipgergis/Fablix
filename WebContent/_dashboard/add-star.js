let login_form = $("#add_star_form");

function handleLoginResult(resultDataString) {
    // If login succeeds, it will redirect the user to movie-list.html
    // console.log(resultDataString);
    // console.log(typeof resultDataString);

    let message = jQuery("#message");
    if (resultDataString["success"]) {
        message.text("New Star Id: " + resultDataString["id"]);
    } else {
        console.log("show error message");
        message.text( "Could not add star\n"+resultDataString["errorMessage"]) ;
    }
    let form = jQuery("#add_star_form");
    form.css("display", "none");
}

function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/add-star", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}
login_form.submit(submitLoginForm);


function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    let homeHead = jQuery("#home-link");
    homeHead.append("<a href = '" + "https://localhost:8080/cs122b_fall_team_20_war/" + resultData[0]["home_url"] + "'>Home</a>");
}

let urlToUse = "api/add-star";

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: urlToUse, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});