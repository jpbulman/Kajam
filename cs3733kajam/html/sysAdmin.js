function checkInputDaysOld(){
    var val = parseInt(document.getElementById("daysOldTF").value);

    if(isNaN(val)){
        alert("Please enter a valid N value for the number of days old field");
    }

}

function checkInputHoursOld(){
    var val = parseInt(document.getElementById("hoursOldTF").value);


    if(isNaN(val)){
        alert("Please enter a valid N value for the number of hours old field");
    }

}

// document.getElementById("delButton").disabled = true;