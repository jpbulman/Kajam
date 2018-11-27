function goAction(){
    var scheduleVal = document.getElementById("scheduleID").value;
    var organizerVal = document.getElementById("organizerID").value;

    if(scheduleVal==""||organizerVal==""){
        alert("Please fill in the required fields");
    }

}

function createNewSchedule(){

    var scheduleName = document.getElementById("scheduleName").value;
    var lenMeetings = document.getElementById("lengthOfMeetings").value;
    var startDay = document.getElementById("startDay").value;
    var endDay = document.getElementById("endDay").value;
    var startHour = document.getElementById("startHour").value;
    var endHour = document.getElementById("endHour").value;

    if(scheduleName==""||lenMeetings==""||startDay==""||endDay==""||startHour==""||endHour==""){
        alert("Please fill in all of the required fields");
    }
    else{

        //Start 'Start Day' section
        if(startDay.length < 10){
            alert("Error, incorrect start day format");
        }
        var startDayMonth = parseInt(startDay.substring(0,2));
        var startDayDay = parseInt(startDay.substring(3,5));
        var startDayYear = parseInt(startDay.substring(6,10));

        if(isNaN(startDayMonth) || startDayMonth < 0 || startDayMonth > 12){
            alert("Please enter a valid month for your start day (00-12)");
        }

        //Needs to check for specific end days of months in the future
        if(isNaN(startDayDay) || startDayDay < 0 || startDayDay > 31){
            alert("Please enter a valid day for your start day (00-31)");
        }

        if(isNaN(startDayYear) || startDayYear < 0){
            alert("Please enter a valid year for your start day ( > 0000)");
        }
        //End 'Start Day' section


        //Start 'End Day' section
        if(endDay.length < 10){
            alert("Error, incorrect end day format");
        }
        var endDayMonth = parseInt(endDay.substring(0,2));
        var endDayDay = parseInt(endDay.substring(3,5));
        var endDayYear = parseInt(endDay.substring(6,10));

        if(isNaN(endDayMonth) || endDayMonth < 0 || endDayMonth > 12){
            alert("Please enter a valid month for your end day (00-12)");
        }

        //Needs to check for specific end days of months in the future
        if(isNaN(endDayDay) || endDayDay < 0 || endDayDay > 31){
            alert("Please enter a valid day for your end day (00-31)");
        }

        if(isNaN(endDayYear) || endDayYear < 0){
            alert("Please enter a valid year for your end day ( > 0000)");
        }
        //End 'End Day' section 



        //'Start Hour' section
        if(isNaN(startHour) || startHour < 0 || startHour > 23){
            alert("Please enter a valid daily start hour (00-23)");
        }



        //'End Hour' section
        if(isNaN(endHour)){
            alert("Please enter a valid daily end hour (00-23)");
        }


        if(endHour<=startHour){
            alert("Error: end hour must be greater than the start hour");
        }

    }

}