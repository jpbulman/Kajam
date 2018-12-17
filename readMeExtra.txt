Kajam G.3 Iteration 3

Below are the URLs for the landing page, organizer page, participant page, and sysadmin page. 

Landing Page URL (to create a schedule):
https://cs3733kajam2.s3.us-east-2.amazonaws.com/landing.html?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20181213T232655Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604796&X-Amz-Credential=AKIAIM2JGQDGSDVYUAOA%2F20181213%2Fus-east-2%2Fs3%2Faws4_request&X-Amz-Signature=cc31b598c798084fd36759dab03ab008659a1ca22a5217e0d359bf13e977f5a4


Organizer Page URL:
https://cs3733kajam2.s3.us-east-2.amazonaws.com/organizerView.html?id=<FILL_IN _SCHEDULE_ID>&secretCode=<FILL_IN_SECRET_CODE>

Participant Page URL:
https://cs3733kajam2.s3.us-east-2.amazonaws.com/participantView.html?id=<FILL_IN _SCHEDULE_ID>

Admin Page URL:
https://cs3733kajam2.s3.us-east-2.amazonaws.com/sysAdminView.html?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20181213T232819Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604796&X-Amz-Credential=AKIAIM2JGQDGSDVYUAOA%2F20181213%2Fus-east-2%2Fs3%2Faws4_request&X-Amz-Signature=1fdc1d4a1b54a05a0d75c190bc55ddd6d7cdd6777ef9231ab9d38b0f13a904c3


NOTES AND INSTRUCTIONS:

Landing Page:

NOTE: Our scheduler program uses a separate landing page (landing.html) for loading into and creating schedules as an organizer. It is used to get to the organizer page. This has been approved by professor Heineman.

To create a schedule: Go to landing page, fill in fields on right side of screen and click “Create a Schedule”. This will generate a pop up message after a few seconds with the ID and secret code of the schedule and then change to the organizer view, which will have the ID in the url. The organizer view will be open to the first week of the schedule. 

To access an existing schedule in organizer page: enter the schedule ID and secret code of the schedule and click the “Go” button. The schedule ID and secret code of the schedule come from the pop up message that displayed after creating the schedule initially. Additionally, the organizer page for a schedule can be accessed using the URL for the organizer page above and filling in your schedule ID and secret code.

The easter egg: If you type ‘Klotski’ into the name input field while making a new schedule it will open up a javascript coded version of the klotski puzzle for your enjoyment. 
How to play: puzzle piece selection is based on mouse click events and movement is controlled by buttons arrayed along the bottom of the page.




Organizer Page:

NOTE: we do not have the Organizer enter the secret code on the Organizer page to perform any actions, because they already had to verify that they were the organizer of that schedule by entering the schedule id and their secret code on the separate landing page in order to get to the organizer view of the schedule.

Opening / Closing time slot: To close or open a time slot as an organizer, click the time slot that you wish to change. This will update the database and the page to reflect the change.

Delete meeting: To delete an existing meeting, click the yellow meeting time slot and answer yes to the dialog box asking if you would like to delete a meeting. After a few seconds, an alert will display saying that it was performed successfully and then the page will updated.

Change the viewed week: Click the yellow left or right arrows in the top row of the schedule table to change the week being viewed.

Edit Schedule: The name, start date, and end date of the schedule can be modified using the text fields for name, start date, and end date and clicking the “Set” button. The new start date must be before the current start date and the new end date must be after the current end date. Fields that kept blank when the “Set” button is clicked are not changed.

Delete Schedule: To delete a schedule, click the red “Delete this schedule” button at the bottom of the page. This will take a few seconds and then show a pop up and return to the landing page.

Set availability for multiple time slots: There is a pull down menu on the side of the screen for choosing the state (available / unavailable) with additional inputs below for choosing which times and dates to select. There are two text fields below it for selecting the start and end times (NOTE: please follow the format exactly as shown: <HH:MM>. ex) 2:00 would be 02:00). There is a pull down menu for selecting specific days of the week or all days of the week. Alternatively, the user can input a specific date instead of just days of the week. The “Set” button is then used to set availabilities. It is likely that the page will need to refreshed for these changes to appear.



Participant Page:

Accessing participant view for schedule: Once a schedule is created, the organizer can click the “Copy participant link” on the organizer page to copy a link for the participants to the clipboard. Additionally, you can use the URL format for participant view shown at the top of this file to reach the participant view for that given schedule.

Change the viewed week: Click the yellow left or right arrows in the top row of the schedule table to change the week being viewed.

Creating meeting: Click an open (green) time slot. A text field and button will appear near the top of the schedule table. Enter the name of the meeting in the text field and click the “Go” button.

Deleting meeting: Click a time slot where it is stated there is a meeting (this slot will have no background color). A textfield and button will appear near the top of the schedule table. Enter the secret code in the text field and click the “Delete meeting” button.

Search for open time slots: There are multiple pull down menus for searching for open time slots. There menus for year, month, day, day of week, and start time. Click the “Go” button to search for time slots using the search criteria. If there are time slots that match the search criteria, they will show up in the container below. To schedule a meeting during one of the time slots, click on the time slot, fill in the text field with the name of the meeting and click the “Go” button.


System Administrator Page:

Retrieve old schedules: This retrieves the schedules that are at least n days old, where n is the number of days which is inputted into the first text field on the page. Click the “Go” button beneath the field to display the schedules in the container below. If no schedules were created during that time period, the container will be blank.

Delete old schedules: This deletes the schedules that are at least n days old, where n is the number of days which is inputted into the first text field on the page. Click the “Delete” button to delete the schedules.

Retrieve schedules within past n hours: This retrieves the schedules that were created within the past n hours where n is the number of hours which is inputted into the second text field on the page. Click the second “Go” button beneath the field to display the schedules in the container below. If no schedules were created during that time period, the container will be blank.
