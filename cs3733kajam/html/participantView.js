var globalSchStartHour = 0;
var globalSchEndHour = 0;
var globalDuration = 0;
var globalSchID = "";

function updateView(json){
    document.getElementById("schName").innerHTML = json["name"]+' <i class="fas fa-calendar-alt"></i>';

    var scheduleID = json["id"];
    document.getElementById("scheduleTable").setAttribute("data-schID",scheduleID)

    var startMonth = parseInt(json["startDate"]["month"]);
    var startDay = parseInt(json["startDate"]["day"]);
    var startYear = parseInt(json["startDate"]["year"]);

    var duration = parseInt(json["meetingDuration"]);
    document.getElementById("scheduleTable").setAttribute("data-duration",duration)

    var startingDate = new Date(startMonth+" "+startDay+" "+startYear);

    var mondayOfWeek = getMonday(startingDate);
    fillInWeek(mondayOfWeek);

    var dayStartHour = parseInt(json["startTime"]["hour"]);
    document.getElementById("scheduleTable").setAttribute("data-dailyStartHour",dayStartHour)
    var dayEndHour = parseInt(json["endTime"]["hour"]);
    document.getElementById("scheduleTable").setAttribute("data-dailyEndHour",dayEndHour)

    globalSchID = scheduleID;
    globalSchStartHour = dayStartHour;
    globalDuration = duration;
    globalSchEndHour = dayEndHour;

    popTable(dayStartHour,dayEndHour,duration,scheduleID)
}

function clearTable(){
    for(var row=1;row<document.getElementById("scheduleTable").rows.length;row++){
        for(var col=1;col<document.getElementById("scheduleTable").rows[row].cells.length;col++){
        var currentCell = document.getElementById("scheduleTable").rows[row].cells[col];
        currentCell.innerHTML = "";
        currentCell.classList.remove("openTS")
        currentCell.classList.remove("closedTS")
        currentCell.classList.remove("bookedTS")   
        }
    }

}

function giveCellsAttr(){
    var table = document.getElementById("scheduleTable")

    var duration = parseInt(document.getElementById("scheduleTable").getAttribute("data-duration"))

    var currHour = parseInt(document.getElementById("scheduleTable").getAttribute("data-dailyStartHour"))
    var currMinute = 0

    var currEndHour = currHour;
    var currEndMinute = currMinute+duration

    var currDate = new Date(document.getElementById("scheduleTable").getAttribute("data-monday"));

    if(currEndMinute==60){
        currEndHour+=1;
        currEndMinute=0
    }

    var currMonth = currDate.getMonth()+1
    var currDay = currDate.getDate()
    var currYear = currDate.getFullYear()

    for(var i=1;i<6;i++){
        for(var j=1;j<table.rows.length;j++){
            var currentCell = table.rows[j].cells[i];
            // console.log(currentCell)
            currentCell.setAttribute("data-year",currYear)
            currentCell.setAttribute("data-month",currMonth)
            currentCell.setAttribute("data-dayOfMonth",currDay)
            currentCell.setAttribute("data-hour",currHour)
            currentCell.setAttribute("data-minute",currMinute)
            currentCell.setAttribute("data-endHour",currEndHour)
            currentCell.setAttribute("data-endMinute",currEndMinute)

            currMinute+=duration
            currEndMinute+=duration
            if(currMinute==60){
                currMinute=0;
                currHour+=1;
            }
            if(currEndMinute==60){
                currEndMinute=0;
                currEndHour+=1;
            }
        }
        currDate.setDate(currDate.getDate()+1)
        currMonth = currDate.getMonth()+1
        currDay = currDate.getDate()
        currYear = currDate.getFullYear()

        currHour = parseInt(document.getElementById("scheduleTable").getAttribute("data-dailyStartHour"))
        currMinute = 0
    
        currEndHour = currHour;
        currEndMinute = currMinute+duration;

        if(currEndMinute==60){
            currEndHour+=1;
            currEndMinute=0
        }
    }
}

function refreshTable(){
    clearTable()
    var dateInformation = getCurrColDateInfo(1);
    // console.log(dateInformation)

    var tsvReq = new XMLHttpRequest();
    var posterUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleView";
    tsvReq.open("POST",posterUrl,true);

    sender = {}
    sender["arg1"] = globalSchID.toString();
    sender["arg2"] = dateInformation["year"].toString();
    sender["arg3"] = (dateInformation["month"]+1).toString();
    sender["arg4"] = dateInformation["day"].toString();

    // console.log(JSON.stringify(sender));

    tsvReq.send(JSON.stringify(sender));

    tsvReq.onloadend = function(){
        if(tsvReq.readyState==XMLHttpRequest.DONE){
            document.getElementById("loading").style.visibility = "hidden";
            var body = JSON.parse(JSON.parse(tsvReq.responseText)["body"]);
            var ts = body["ts"]
            // console.log(ts)

            var row=1;
            var col=1;

            var table = document.getElementById("scheduleTable")

            var lowYear = 9999;
            var highYear = 0000;

            for(var k=0;k<ts.length;k++){
                var currentCell = table.rows[row].cells[col];
                
                if(parseInt(ts[k]["date"]["year"])<lowYear){
                    lowYear = parseInt(ts[k]["date"]["year"])
                }
                if(parseInt(ts[k]["date"]["year"])>highYear){
                    highYear=parseInt(ts[k]["date"]["year"])
                }

                giveCellsAttr()

                for(var searchRow=1;searchRow<table.rows.length;searchRow++){
                    for(var searchColumn=1;searchColumn<table.rows[searchRow].cells.length;searchColumn++){
                        var cc = table.rows[searchRow].cells[searchColumn];
                        if(cc.getAttribute("data-year")==ts[k]["date"]["year"]&&
                            cc.getAttribute("data-month")==ts[k]["date"]["month"]&&
                            cc.getAttribute("data-dayOfMonth")==ts[k]["date"]["day"]&&
                            cc.getAttribute("data-hour")==ts[k]["startTime"]["hour"]&&
                            cc.getAttribute("data-minute")==ts[k]["startTime"]["minute"]&&
                            cc.getAttribute("data-endHour")==ts[k]["endTime"]["hour"]&&
                            cc.getAttribute("data-endMinute")==ts[k]["endTime"]["minute"]){
                            currentCell = cc;
                        }
                    }
                }
                
                currentCell.setAttribute("data-isFree",ts[k]["isFree"])
                currentCell.setAttribute("data-id",ts[k]["id"])

                if(ts[k]["isFree"] && ts[k]["meeting"]["secretCode"]==0){
                    currentCell.innerHTML="Open<br>(Click to book)";
                    currentCell.classList.add("openTS")
                    currentCell.setAttribute("data-isOpenEditable",true)
                }
                else if(ts[k]["meeting"]["secretCode"]==0){
                    currentCell.innerHTML="Closed"
                    currentCell.classList.add("closedTS");
                }
                else{
                    currentCell.innerHTML="Booked Meeting<br>(Click to delete)"
                    currentCell.classList.add("bookedTS")
                    currentCell.setAttribute("data-isDeleteEditable",true)
                }

                currentCell.onclick = function (){
                    if(this.getAttribute("data-isOpenEditable")){
                        on();
                        currTSID = this.getAttribute("data-id");
                        var endStr = (this.getAttribute("data-endMinute")=='0')? "00":this.getAttribute("data-endMinute");
                        var startStr = (this.getAttribute("data-minute")=='0')? "00":this.getAttribute("data-minute");
                        currTSString = this.getAttribute("data-month")+"/"+this.getAttribute("data-dayOfMonth")+"/"+this.getAttribute("data-year")+" from "+this.getAttribute("data-hour")+":"+startStr+" until "+this.getAttribute("data-endHour")+":"+endStr;
                        //
                    }
                    if(this.getAttribute("data-isDeleteEditable")){
                        onDelete();
                        currentDeleteAMeetingTSID = this.getAttribute("data-id")
                    }
                }
                // console.log(row,col)
                if(row%(table.rows.length-1)==0){
                    row=0;
                    col+=1;
                }
                row++;
            }

            populateYearDD(lowYear,highYear)

        }
    }
}

function getNextWeek(){
    clearYears()
    var mon = document.getElementById("MonDate").innerHTML;
    var d = new Date(mon);
    d.setDate(d.getDate()+7);
    fillInWeek(d);
    refreshTable();
}

function getPrevWeek(){
    clearYears()
    var mon = document.getElementById("MonDate").innerHTML;
    var d = new Date(mon);
    d.setDate(d.getDate()-7);
    fillInWeek(d);
    refreshTable()
}

function on(){
    document.getElementById("overlayDiv").style.display = "block"
    document.getElementById("meetingTextInput").select()
    document.body.scrollTop = document.documentElement.scrollTop = 0;
}

function off(){
    document.getElementById("overlayDiv").style.display = "none"
}

var currTSID = ""
var currTSString = ""
var mre = ""
function processMeetingInput(){

    var cap = grecaptcha.getResponse()
    if(cap!=""){
        document.getElementById("bookingMeeting").style.visibility = "visible";
        var input = "-"

        var docInput = document.getElementById("meetingTextInput").value;
        mre = docInput
        
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
                document.getElementById("bookingMeeting").style.visibility = "hidden";
                console.log(JSON.stringify(makeMeetingReq.responseText))
                var secretyCode = JSON.parse(makeMeetingReq.responseText)["secretCode"]
                refreshTable()
                alert("This is your secret code for the meeting, make sure you remember it! "+secretyCode)

                if(document.getElementById("emailCheck").checked){
                    sendEmail(mre,secretyCode);
                }

            }
        }

        off();
    }
    else{
        alert("Please click the captcha")
    }
}

function onDelete(){
    document.getElementById("deleteOverlayDiv").style.display = "block";
    var secretCode = document.getElementById("deleteAMeetingCode").select()
    document.body.scrollTop = document.documentElement.scrollTop = 0;
}

function offDelete(){
    document.getElementById("deleteOverlayDiv").style.display = "none";
}

var currentDeleteAMeetingTSID = "";
function deleteAMeeting(){
    var secretCode = document.getElementById("deleteAMeetingCode").value;

    var delMeetingReq = new XMLHttpRequest();
    var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/meeting";
    delMeetingReq.open("DELETE",url,true);

    var sender = {}
    sender["timeSlotID"] = currentDeleteAMeetingTSID;
    sender["secretCode"] = secretCode;
    console.log(sender)
    
    delMeetingReq.send(JSON.stringify(sender))

    delMeetingReq.onloadend = function(){
        if(delMeetingReq.readyState==XMLHttpRequest.DONE){
            console.log(delMeetingReq.responseText)
            alert("Meeting deleted!")
            refreshTable()
        }
    }

    offDelete()
}

function getWeekDay(day){
    return day.toString().substring(0,3);
}

function fillInDate(dayOfWeek,actualDate){
    if(dayOfWeek=="Mon"){
        document.getElementById(dayOfWeek).innerHTML = '<button class="weekButton" onclick="getPrevWeek()" type="button"><i class="fas fa-chevron-circle-left"></i></button> <a id="MonDate">'+actualDate.toDateString()+'</a>';
    }
    else if(dayOfWeek.toString()=="Fri"){
        document.getElementById(dayOfWeek).innerHTML = '<a id="FriDate">'+actualDate.toDateString()+'</a>'+' <button class="weekButton" onclick="getNextWeek()" type="button"><i class="fas fa-chevron-circle-right"></i></button>'; 
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
    }
    else{
        while(getWeekDay(currDate.toDateString())!="Mon"){
            currDate.setDate(currDate.getDate()-1);
        }
    }
    document.getElementById("scheduleTable").setAttribute("data-monday",currDate.toDateString())
    return currDate;
}

function popTable(dayStartHour,dayEndHour,duration,scheduleID){

    document.getElementById("loading").style.visibility = "visible";
    console.log(dayStartHour,dayEndHour,duration,scheduleID)

    populateStartTimeDD()
    populateMonthDD()
    populateDOMDD()
    populateDOW()

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
            // refreshTable()
            // if(a==1 && i==1){

            //     var dateInformation = getCurrColDateInfo(a);

            //     var tsvReq = new XMLHttpRequest();
            //     var posterUrl = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/scheduleView";
            //     tsvReq.open("POST",posterUrl,true);
            
            //     sender = {}
            //     sender["arg1"] = scheduleID.toString();
            //     sender["arg2"] = dateInformation["year"].toString();
            //     sender["arg3"] = (dateInformation["month"]+1).toString();
            //     sender["arg4"] = dateInformation["day"].toString();

            //     console.log(JSON.stringify(sender));

            //     tsvReq.send(JSON.stringify(sender));

            //     tsvReq.onloadend = function(){
            //         if(tsvReq.readyState==XMLHttpRequest.DONE){
            //             document.getElementById("loading").style.visibility = "hidden";
            //             var body = JSON.parse(JSON.parse(tsvReq.responseText)["body"]);
            //             var ts = body["ts"]

            //             var row=1;
            //             var col=1;

            //             var table = document.getElementById("scheduleTable")
            //             console.log(ts)

            //             for(var k=0;k<ts.length;k++){
            //                 var currentCell = table.rows[row].cells[col];
            //                 currentCell.setAttribute("data-year",ts[k]["date"]["year"])
            //                 currentCell.setAttribute("data-month",ts[k]["date"]["month"])
            //                 currentCell.setAttribute("data-dayOfMonth",ts[k]["date"]["day"])
            //                 currentCell.setAttribute("data-hour",ts[k]["startTime"]["hour"])
            //                 currentCell.setAttribute("data-minute",ts[k]["startTime"]["minute"])
            //                 currentCell.setAttribute("data-endHour",ts[k]["endTime"]["hour"])
            //                 currentCell.setAttribute("data-endMinute",ts[k]["endTime"]["minute"])
            //                 currentCell.setAttribute("data-isFree",ts[k]["isFree"])
            //                 currentCell.setAttribute("data-id",ts[k]["id"])

            //                 if(ts[k]["isFree"] && ts[k]["meeting"]["secretCode"]==0){
            //                     currentCell.innerHTML="Open<br>(Click to book)";
            //                     currentCell.classList.add("openTS")
            //                     currentCell.setAttribute("data-isOpenEditable",true)
            //                 }
            //                 else if(ts[k]["meeting"]["secretCode"]==0){
            //                     currentCell.innerHTML="Closed"
            //                     currentCell.classList.add("closedTS");
            //                 }
            //                 else{
            //                     currentCell.innerHTML="Booked Meeting<br>(Click to delete)"
            //                     currentCell.classList.add("bookedTS")
            //                     currentCell.setAttribute("data-isDeleteEditable",true)
            //                 }

            //                 currentCell.onclick = function (){
            //                     if(this.getAttribute("data-isOpenEditable")){
            //                         on();
            //                         currTSID = this.getAttribute("data-id");
            //                         var endStr = (this.getAttribute("data-endMinute")=='0')? "00":this.getAttribute("data-endMinute");
            //                         var startStr = (this.getAttribute("data-minute")=='0')? "00":this.getAttribute("data-minute");
            //                         currTSString = this.getAttribute("data-month")+"/"+this.getAttribute("data-dayOfMonth")+"/"+this.getAttribute("data-year")+" from "+this.getAttribute("data-hour")+":"+startStr+" until "+this.getAttribute("data-endHour")+":"+endStr
            //                     }
            //                     if(this.getAttribute("data-isDeleteEditable")){
            //                         onDelete();
            //                         currentDeleteAMeetingTSID = this.getAttribute("data-id")
            //                     }
            //                 }
            //                 // console.log(row,col)
            //                 if(row%(ts.length/5)==0){
            //                     row=0;
            //                     col+=1;
            //                 }
            //                 row++;
            //             }

            //         }
            //     }
            // }
        }

        if(currMinute+duration == 60){
            currMinute = 0;
            currHour++;
        }
        else{
            currMinute+=duration;
        }
    }
    refreshTable()
}

function removeFilterChilderen(){
    var div = document.getElementById("filteredResults");
    while(div.firstChild){
        div.removeChild(div.firstChild)
    }
}

function filter(){
    removeFilterChilderen()
    var url = "https://f1a5ytx922.execute-api.us-east-2.amazonaws.com/Beta/filtertimeslots"
    //schid, month(1-12),year,dow(1-5),dom(1-31),h,m

    var filReq = new XMLHttpRequest();
    filReq.open("POST",url,true);

    var sendInformation = {}
    sendInformation["id"] = document.getElementById("scheduleTable").getAttribute("data-schID")
    sendInformation["month"] = monthToInt(document.getElementById("monthDropDown").value).toString()
    sendInformation["year"] = (document.getElementById("yearDD").value.toString()=="Year" || document.getElementById("yearDD").value.toString()=="None")? "" : document.getElementById("yearDD").value.toString()
    sendInformation["dayOfWeek"] = dowToInt(document.getElementById("DOWDD").value).toString()
    sendInformation["day"] = (document.getElementById("DOMDD").value=="Day of month"||document.getElementById("DOMDD").value=="None")? "":document.getElementById("DOMDD").value;
    sendInformation["hour"] = getSelectedHour()
    sendInformation["minute"] = getSelectedMinute()

    console.log(JSON.stringify(sendInformation))
    filReq.send(JSON.stringify(sendInformation))

    filReq.onloadend = function(){
        if(filReq.readyState==XMLHttpRequest.DONE){
            // console.log(filReq.responseText)

            var responses = JSON.parse(filReq.responseText)["timeSlots"]

            console.log(responses)

            for(j in responses){
                var i = responses[j]
                var startMinStr = (i["startTime"]["minute"]=="0")? "00":i["startTime"]["minute"];
                var endMinStr = (i["endTime"]["minute"]=="0")? "00":i["endTime"]["minute"];
                var entryText = i["date"]["month"]+"/"+i["date"]["day"]+"/"+i["date"]["year"]+" from "+i["startTime"]["hour"]+":"+startMinStr+" to "+i["endTime"]["hour"]+":"+endMinStr;
                var button = document.createElement("BUTTON");
                var text = document.createTextNode(entryText);
                button.appendChild(text)
                button.classList.add("filteredResultsEntry");
                button.setAttribute("TSID",i["id"])
                button.onclick = function(){
                    currTSID = this.getAttribute("TSID")
                    on()
                }
                var div = document.createElement("div")
                div.appendChild(button);
                document.getElementById("filteredResults").appendChild(div);
            }

        }
    }

}



// document.getElementById("bookingMeeting").style.visibility = "hidden";

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

function getSelectedHour(){
    var val = document.getElementById("startTimeDropDown").value;
    var ret = ""

    for(var i=0;i<val.length;i++){
        var curr = val.substring(i,i+1)
        if(curr==":"){
            return ret;
        }
        else{
            ret+=curr;
        }
    }

    return ""
}

function getSelectedMinute(){
    var val = document.getElementById("startTimeDropDown").value;
    var ret = ""

    for(var i=0;i<val.length;i++){
        var curr = val.substring(i,i+1)
        if(curr==":"){
            return val.substring(i+1,val.length)
        }
    }

    return ""
}

function populateStartTimeDD(){
    var dropDown = document.getElementById("startTimeDropDown");

    var DSH = parseInt(document.getElementById("scheduleTable").getAttribute("data-dailyStartHour"))
    var DEH = parseInt(document.getElementById("scheduleTable").getAttribute("data-dailyEndHour"))
    var dur = parseInt(document.getElementById("scheduleTable").getAttribute("data-duration"));
    var currMinute = 0;
    while(DSH<DEH){

        var option = document.createElement("option")
        
        var currMinStr = currMinute;
        if(currMinute==0){
            currMinStr="00"
        }

        option.text=DSH+":"+currMinStr;
        dropDown.add(option)

        currMinute+=dur;
        if(currMinute==60){
            DSH++;
            currMinute=0;
        }
    }

}

function monthToInt(i){
    switch(i){
        case "January":return 1;
        case "February":return 2;
        case "March":return 3;
        case "April":return 4;
        case "May":return 5;
        case "June":return 6;
        case "July":return 7;
        case "August":return 8;
        case "September":return 9;
        case "October":return 10;
        case "November":return 11;
        case "December":return 12;
    }
    return ""
}

function populateMonthDD(){
    var dropDown = document.getElementById("monthDropDown")

    for(var i=1;i<=12;i++){
        var text = ""
        switch(i){
            case 1:text="January";break;
            case 2:text="February";break;
            case 3:text="March";break;
            case 4:text="April";break;
            case 5:text="May";break;
            case 6:text="June";break;
            case 7:text="July";break;
            case 8:text="August";break;
            case 9:text="September";break;
            case 10:text="October";break;
            case 11:text="November";break;
            case 12:text="December";break;
        }
        var option = document.createElement("option")
        option.text=text;
        dropDown.add(option)
    }

}

function populateDOMDD(){
    var dropDown = document.getElementById("DOMDD");

    for(var i=1;i<=31;i++){
        var option = document.createElement("option")
        option.text=i;
        dropDown.add(option)
    }

}

function populateYearDD(ly,hy){
    while(ly<=hy){
        var option = document.createElement("option")
        option.text=ly;

        document.getElementById("yearDD").add(option)

        ly++;
    }
}

function clearYears(){
    var dd = document.getElementById("yearDD");
    for(var i=0;i<dd.options.length;i++){
        dd.options[i] = null;
    }
}

function dowToInt(i){
    switch(i){
        case "Monday":return 1;
        case "Tuesday":return 2;
        case "Wednesday":return 3;
        case "Thursday":return 4;
        case "Friday":return 5;
    }
    return ""
}

function populateDOW(){
    var dropDown = document.getElementById("DOWDD")
    for(var i=1;i<=5;i++){
        var text="day";
        switch(i){
            case 1:text="Monday";break;
            case 2:text="Tuesday";break;
            case 3:text="Wednesday";break;
            case 4:text="Thursday";break;
            case 5:text="Friday";break;
        }
        var option = document.createElement("option")
        option.text = text;
        dropDown.add(option)
    }
}





function sendEmail(email,sc){
    var template_params = {
        "send_to": email,
        "reply_to": "reply_to_value",
        "from_name": "your organizer",
        "to_name": "user",
        "message_html": "You've got a meeting on "+currTSString+". The secret code for this meeting is: "+sc+". You can view it here: "+window.location.href
     }
     
     var service_id = "default_service";
     var template_id = "template_YFRkJzbx";
     console.log(email,sc,template_params["message_html"])
    //  document.location.reload(true)
    refreshTable()
     emailjs.send(service_id,template_id,template_params);
}