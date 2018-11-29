function goAction(){
    var scheduleVal = document.getElementById("scheduleID").value;
    var organizerVal = document.getElementById("organizerID").value;

    if(scheduleVal==""||organizerVal==""){
        alert("Please fill in the required fields");
    }

}

var createScheduleURL = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/schedule   "
// var createScheduleURL = "";
function createNewSchedule(){

    var scheduleName = document.getElementById("scheduleName").value;
    var lenMeetings = parseInt(document.getElementById("lengthOfMeetings").value.substring(0,2));
    var startDay = document.getElementById("startDay").value;
    var endDay = document.getElementById("endDay").value;
    var startHour = parseInt(document.getElementById("startHour").value);
    var endHour = parseInt(document.getElementById("endHour").value);

    if(scheduleName==""||lenMeetings==""||startDay==""||endDay==""||startHour==""||endHour==""){
        alert("Please fill in all of the required fields");
        return;
    }
    else{

        //Start 'Start Day' section
        if(startDay.length < 10){
            alert("Error, incorrect start day format");
            return;
        }
        var startDayMonth = parseInt(startDay.substring(0,2));
        var startDayDay = parseInt(startDay.substring(3,5));
        var startDayYear = parseInt(startDay.substring(6,10));

        if(isNaN(startDayMonth) || startDayMonth < 0 || startDayMonth > 12){
            alert("Please enter a valid month for your start day (00-12)");
            return;
        }

        //Needs to check for specific end days of months in the future
        if(isNaN(startDayDay) || startDayDay < 0 || startDayDay > 31){
            alert("Please enter a valid day for your start day (00-31)");
            return;
        }

        if(isNaN(startDayYear) || startDayYear < 0){
            alert("Please enter a valid year for your start day ( > 0000)");
            return;
        }
        //End 'Start Day' section


        //Start 'End Day' section
        if(endDay.length < 10){
            alert("Error, incorrect end day format");
            return;
        }
        var endDayMonth = parseInt(endDay.substring(0,2));
        var endDayDay = parseInt(endDay.substring(3,5));
        var endDayYear = parseInt(endDay.substring(6,10));

        if(isNaN(endDayMonth) || endDayMonth < 0 || endDayMonth > 12){
            alert("Please enter a valid month for your end day (00-12)");
            return;
        }

        //Needs to check for specific end days of months in the future
        if(isNaN(endDayDay) || endDayDay < 0 || endDayDay > 31){
            alert("Please enter a valid day for your end day (00-31)");
            return;
        }

        if(isNaN(endDayYear) || endDayYear < 0){
            alert("Please enter a valid year for your end day ( > 0000)");
            return;
        }
        //End 'End Day' section 



        //'Start Hour' section
        if(isNaN(startHour) || startHour < 0 || startHour > 23){
            alert("Please enter a valid daily start hour (00-23)");
            return;
        }



        //'End Hour' section
        if(isNaN(endHour)){
            alert("Please enter a valid daily end hour (00-23)");
            return;
        }


        if(endHour<=startHour){
            alert("Error: end hour must be greater than the start hour");
            return;
        }

        scheduleData = {}
        //String name, int startHour, int endHour, int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay, int duration
        scheduleData["arg1"] = scheduleName;
        scheduleData["arg2"] = startHour;
        scheduleData["arg3"] = endHour;
        scheduleData["arg4"] = startDayYear;
        scheduleData["arg5"] = startDayMonth;
        scheduleData["arg6"] = startDayDay;
        scheduleData["arg7"] = endDayYear;
        scheduleData["arg8"] = endDayMonth;
        scheduleData["arg9"] = endDayDay;
        scheduleData["arg10"] = lenMeetings;

        var jsonData = JSON.stringify(scheduleData);
        console.log(jsonData);

        var xhr = new XMLHttpRequest();
        xhr.open("POST", createScheduleURL, true);

        xhr.send(jsonData);

        xhr.onloadend = function () {
            console.log(xhr);
            console.log(xhr.request);
            if (xhr.readyState == XMLHttpRequest.DONE) {
              console.log ("XHR:" + xhr.responseText);
            } 
            else {
            
            }
        };
    }

}