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

    var dayStartHour = parseInt(json["startTime"]["hour"]);
    var dayEndHour = parseInt(json["endTime"]["hour"]);

    popTable(dayStartHour,dayEndHour,duration,scheduleID)
}

function on(){
    document.getElementById("overlayDiv").style.display = "block"
}

function off(){
    document.getElementById("overlayDiv").style.display = "none"
}

var currTSID = ""
function processMeetingInput(){
    var input = "-"

    var docInput = document.getElementById("meetingTextInput").value;
    
    var makeMeetingReq = new XMLHttpRequest();
    var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/meeting"
    makeMeetingReq.open("POST",url,true);

    var sender = {}
    sender["timeSlotID"] = currTSID;
    sender["name"] = docInput;

    makeMeetingReq.send(JSON.stringify(sender))
    console.log(JSON.stringify(sender))

    makeMeetingReq.onloadend = function(){
        if(makeMeetingReq.readyState == XMLHttpRequest.DONE){
            console.log(makeMeetingReq.responseText)
            alert("This is your secret code for the meeting, make sure you remember it!"+makeMeetingReq.responseText)
        }
    }

    off();
}

function deleteAMeeting(){
    var secretCode = document.getElementById("deleteAMeetingCode");

    var delMeetingReq = new XMLHttpRequest();
    var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/meeting";
    delMeetingReq.open("DELETE",url,true);

    var sender = {}
    sender[""] = secretCode;

}

function getWeekDay(day){
    return day.toString().substring(0,3);
}

function fillInDate(dayOfWeek,actualDate){
    if(dayOfWeek=="Mon"){
        document.getElementById(dayOfWeek).innerHTML = '<button onclick="getPrevWeek()" type="button"><</button> <a id="MonDate">'+actualDate.toDateString()+'</a>';
    }
    else if(dayOfWeek.toString()=="Fri"){
        document.getElementById(dayOfWeek).innerHTML = '<a id="FriDate">'+actualDate.toDateString()+'</a>'+' <button onclick="getNextWeek()" type="button">></button>'; 
    }
    else{
        document.getElementById(dayOfWeek).innerHTML = '<a id="'+getWeekDay(actualDate)+'Date">'+actualDate.toDateString()
    }
}

function fillInWeek(mon){
    while(getWeekDay(mon.toDateString())!="Sat"){
        fillInDate(getWeekDay(mon.toDateString()),mon);
        mon.setDate(mon.getDate()+1);
    }
}

function getCurrColDateInfo(column){
    var id = "";
    switch(column){
        case 1:id="Mon";break;
        case 2:id="Tue";break;
        case 3:id="Wed";break;
        case 4:id="Thu";break;
        case 5:id="Fri";break;
    }
    var x = document.getElementById(id+"Date").innerHTML;
    var d = new Date(x);
    
    var information = {};
    information["year"] = d.getFullYear();
    information["month"] = d.getMonth();
    information["day"] = d.getDate();

    return information;
}

function getMonday(currDate){
    if(getWeekDay(currDate.toDateString())=="Sat" || getWeekDay(currDate.toDateString())=="Sun"){
        while(getWeekDay(currDate.toDateString())!="Mon"){
            currDate.setDate(currDate.getDate()+1);
        }
    }else{
        while(getWeekDay(currDate.toDateString())!="Mon"){
            currDate.setDate(currDate.getDate()-1);
        }
    }
    return currDate;
}

function popTable(dayStartHour,dayEndHour,duration,scheduleID){

    console.log(dayStartHour,dayEndHour,duration,scheduleID)

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

        // refreshTable();

        // Populate rest of the row
        for(var a=1;a<=5;a++){
            var cell = row.insertCell(a);
            if(a==1 && i==1){

                var dateInformation = getCurrColDateInfo(a);

                var tsvReq = new XMLHttpRequest();
                var posterUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleView";
                tsvReq.open("POST",posterUrl,true);
            
                sender = {}
                sender["arg1"] = scheduleID.toString();
                sender["arg2"] = dateInformation["year"].toString();
                sender["arg3"] = (dateInformation["month"]+1).toString();
                sender["arg4"] = dateInformation["day"].toString();

                console.log(JSON.stringify(sender));

                tsvReq.send(JSON.stringify(sender));

                tsvReq.onloadend = function(){
                    if(tsvReq.readyState==XMLHttpRequest.DONE){
                        var body = JSON.parse(JSON.parse(tsvReq.responseText)["body"]);
                        var ts = body["ts"]

                        var row=1;
                        var col=1;

                        var table = document.getElementById("scheduleTable")
                        console.log(ts)

                        for(var k=0;k<ts.length;k++){
                            var currentCell = table.rows[row].cells[col];
                            currentCell.setAttribute("data-year",ts[k]["date"]["year"])
                            currentCell.setAttribute("data-month",ts[k]["date"]["month"])
                            currentCell.setAttribute("data-dayOfMonth",ts[k]["date"]["day"])
                            currentCell.setAttribute("data-hour",ts[k]["startTime"]["hour"])
                            currentCell.setAttribute("data-minute",ts[k]["startTime"]["minute"])
                            currentCell.setAttribute("data-isFree",ts[k]["isFree"])
                            currentCell.setAttribute("data-id",ts[k]["id"])

                            if(ts[k]["isFree"] && ts[k]["meeting"]["secretCode"]==0){
                                currentCell.innerHTML="Open";
                                currentCell.classList.add("openTS")
                                currentCell.setAttribute("data-isMeeting",false)
                            }
                            else if(ts[k]["meeting"]["secretCode"]==0){
                                currentCell.innerHTML="Closed"
                                currentCell.classList.add("closedTS");
                            }
                            else{
                                currentCell.innerHTML="Booked Meeting"
                            }

                            currentCell.onclick = function (){
                                on();
                                currTSID = this.getAttribute("data-id");
                            }
                            // console.log(row,col)
                            if(row%(ts.length/5)==0){
                                row=0;
                                col+=1;
                            }
                            row++;
                        }

                    }
                }
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
}





var url = window.location.href;
var setOfParams = "";

for(var i=0;i<url.length;i++){
    if(url.substring(i,i+1)==="?"){
        i++;
        for(var j=i;j<url.length;j++){
            setOfParams+=url.substring(j,j+1);
        }
    }
}

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

getUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleid/participant"
xhr = new XMLHttpRequest();
xhr.open("POST",getUrl,true);

sender = {}
theScheduleID = urlParameters["id"];
sender["arg1"] = urlParameters["id"];
sender = JSON.stringify(sender);
console.log(sender)

xhr.send(sender);

xhr.onloadend = function(){

    if(xhr.readyState == XMLHttpRequest.DONE){
        console.log("Got back schedule information:"+xhr.responseText);
        getBackParameters = JSON.parse(JSON.parse(xhr.responseText)["body"]);


        if(getBackParameters["httpCode"]==400){
            // window.location.href = "Error Pages/General400.html"
        }

        console.log(getBackParameters)

        updateView(getBackParameters);
    }
    else{
        console.log("POST request failed, either a connectivity issue or a problem with the server");
    }

};