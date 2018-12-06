var url = window.location.href;
var setOfParams = "";
var q = "";

var theScheduleID = "";

function getWeekDay(day){
    return day.substring(0,3);
}

function fillInDate(dayOfWeek,actualDate){
    if(dayOfWeek=="Mon"){
        document.getElementById(dayOfWeek).innerHTML = '<button onclick="getPrevWeek()" type="button"><</button> <a id="monDate">'+actualDate.toDateString()+'</a>';
    }
    else if(dayOfWeek.toString()=="Fri"){
        document.getElementById(dayOfWeek).innerHTML = actualDate.toDateString()+' <button onclick="getNextWeek()" type="button">></button>'; 
    }
    else{
        document.getElementById(dayOfWeek).innerHTML = actualDate.toDateString()
    }
}

function fillInWeek(mon){
    while(getWeekDay(mon.toDateString())!="Sat"){
        fillInDate(getWeekDay(mon.toDateString()),mon);
        mon.setDate(mon.getDate()+1);
    }
}

function getMonday(currDate){
    while(getWeekDay(currDate.toDateString())!="Mon"){
        currDate.setDate(currDate.getDate()-1);
    }
    return currDate;
}

function getNextWeek(){
    var mon = document.getElementById("monDate").innerHTML;
    var d = new Date(mon);
    d.setDate(d.getDate()+7);
    fillInWeek(d);
}

function getPrevWeek(){
    var mon = document.getElementById("monDate").innerHTML;
    var d = new Date(mon);
    d.setDate(d.getDate()-7);
    fillInWeek(d);
}

function getTimeSlotView(schId,year,month,day,startHour,startMinute){
    var tsvReq = new XMLHttpRequest();
    var posterUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleView/timeslotview";
    tsvReq.open("POST",posterUrl,true);

    sender = {}
    sender["arg1"] = schId.toString();
    sender["arg2"] = year.toString();
    sender["arg3"] = month.toString();
    sender["arg4"] = day.toString();
    sender["arg5"] = startHour.toString();
    sender["arg6"] = startMinute.toString();

    tsvReq.send(JSON.stringify(sender));
    console.log(JSON.stringify(sender));

    tsvReq.onloadend = function(){

        if(tsvReq.readyState == XMLHttpRequest.DONE){
            console.log("getTimeSlotView invoked. Returned:");
            var parsedInfo = JSON.parse(tsvReq.responseText);
            console.log(parsedInfo["body"]);
        }
        else{
            console.log("ERROR: Could not get time slot view properly");
        }

    };

}

function deleteSchedule(){
    console.log("Started delete request");
    var deleteRequest = new XMLHttpRequest();
    var deleteURL = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/schedule";
    deleteRequest.open("DELETE",deleteURL,true);

    var sendMaterial = {};
    sendMaterial["arg1"] = theScheduleID;

    var json = JSON.stringify(sendMaterial);
    console.log(json);

    deleteRequest.send(json);

    deleteRequest.onloadend = function(){
        if(deleteRequest.readyState == XMLHttpRequest.DONE){
            alert("Schedule has been deleted, thank you for using scheduler!");
            window.location.href = "landing.html";
            console.log("Schedule has been deleted");
        }
        else{
            console.log("ERROR: Could not delete schedule properly");
        }
    }

}

function updateView(json){
    document.getElementById("schName").innerHTML = json["name"];

    var scheduleID = json["id"];

    var startMonth = parseInt(json["startDate"]["month"]);
    var startDay = parseInt(json["startDate"]["day"]);
    var startYear = parseInt(json["startDate"]["year"]);

    var duration = parseInt(json["meetingDuration"]);

    var startingDate = new Date(startMonth+" "+startDay+" "+startYear);

    var mondayOfWeek = getMonday(startingDate);
    fillInWeek(mondayOfWeek);

    //This section populates the table with cells
    var dayStartHour = parseInt(json["startTime"]["hour"]);
    var dayEndHour = parseInt(json["endTime"]["hour"]);

    var currHour,currMinute, i;
    for(currHour = dayStartHour, currMinute = 0, i=1;currHour<dayEndHour;i++){
        
        var row = document.getElementById("scheduleTable").insertRow(i);
        var head = row.insertCell(0);

        currMinuteStr = (currMinute==0)? "00":currMinute;

        enderHour = currHour;
        enderMinute = (currMinute+duration);

        if(enderMinute==60){
            enderMinute="00";
            enderHour++;
        }

        head.innerHTML = currHour+":"+currMinuteStr+" - "+enderHour+":"+enderMinute;

        // Populate rest of the row
        for(var a=1;a<=5;a++){
            var cell = row.insertCell(a);
            if(a==1 && i==1){
                cell.classList.add("openTS");
                cell.innerHTML = "Open";;
                cell.onclick = function(){
                    if(this.innerHTML=="Open"){
                        this.classList.remove("openTS");
                        this.classList.add("closedTS");
                        this.innerHTML = "Closed";
                    }
                    else if(this.innerHTML=="Closed"){
                        this.classList.remove("closedTS");
                        this.classList.add("openTS");
                        this.innerHTML = "Open";
                    }
                    else{
                        //Alert to confirm to cancel a meeting
                    }
                }
            }
            else{

            }
        }

        if(currMinute+duration == 60){
            currMinute = 0;
            currHour++;
        }
        else{
            currMinute+=duration;
        }
    }

    // console.log(document.getElementById("11").innerHTML)    
    getTimeSlotView(scheduleID,startYear,startMonth,startDay,dayStartHour,"00");
}

for(var i=0;i<url.length;i++){
    if(url.substring(i,i+1)==="?"){
        i++;
        for(var j=i;j<url.length;j++){
            setOfParams+=url.substring(j,j+1);
        }
    }
}

q=setOfParams;

urlParameters = {}

for(var k=0;k<setOfParams.length;k++){
    keyName = ""

    while(setOfParams.substring(k,k+1) != "="){
        keyName+=setOfParams.substring(k,k+1);
        k++;
    }
    k++;

    if(k==setOfParams.length){
        break;
    }

    value = ""

    while(setOfParams.substring(k,k+1)!="&"){
        value+=setOfParams.substring(k,k+1);
        k++;
        if(k==setOfParams.length){
            break;
        }
    }

    urlParameters[keyName]=value;
}

getBackParameters = {}

getUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleid"
xhr = new XMLHttpRequest();
xhr.open("POST",getUrl,true);

sender = {}
theScheduleID = urlParameters["id"];
sender["arg1"] = urlParameters["id"];
sender["arg2"] = urlParameters["secretCode"];
sender = JSON.stringify(sender);

xhr.send(sender);

xhr.onloadend = function(){

    if(xhr.readyState == XMLHttpRequest.DONE){
        console.log("Got back schedule information:"+xhr.responseText);
        getBackParameters = JSON.parse(JSON.parse(xhr.responseText)["body"]);
        updateView(getBackParameters);
    }
    else{
        console.log("POST request failed, either a connectiivity issue or a problem with the server");
    }

};