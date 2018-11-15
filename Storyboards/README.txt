Group: Kajam
Andrew Markoski
Brittany Goldstein
Alex Spielman
John Bulman

The following notes are annotations for the storyboard documents (included in this same directory)

----------------------
loginMarkedUp.pdf
----------------------
1. A textbox for a participant to enter a schedule ID. If a valid ID is given and the go button is pressed, the page will then be redirected to the corresponding participant view for the given schedule. If it is invalid, the box will be highlighted red and will have a small pop up saying 'Invalid ID'

2. A textbox for an organizer to enter their secret ID. If the organizer ID is valid and the go button is pressed, the page will be redirected to the corresponding organizer view for their schedule. If it is invalid, the box will be highlighted red and will have a small pop up saying 'Invalid ID' 

3. The 'Go' button can be clicked once one of the text fields is filled with an ID. If none or both of the fields are filled, then the button will be greyed out and unclickabel until the user fixes the inputs.

4. The 'Create A New Schedule' button allows a user to become an organizer and to create a schedule. This will redirect them to an organizer view with a blank schedule that they can start to fill in.

-----------------------
participantMarkedUp.pdf
-----------------------
1. This is a text box that will show the corresponding meeting to a secret code when entered in the field above.

2. The cancel button will allow a participant to cancel the selected meeting. The button will not be clickable until the participant has entered a secret code. Once clicked, there will be a small window that pops up asking the user to confim the cancellation.

3. The filter bar is used to see all of the open time slots. The user can type in a specific day into this search bar, or can use the filters below to get results.

4. This is the filter by month dropdown menu. It will bring down a list of all of the months where there are open time slots.

5. This is the filter by year dropdown menu. It will bring down a list of all of the years where there are open time slots.

6. This is the filter by day of the week dropdown menu. It will bring down a list of all of the days of the week where there are open time slots.

7. This is the filter by day of the month dropdown menu. It will bring down a list of all of the days of the month where there are open time slots.

8. The 'Go' button, once clicked, will take the given filters and find all of the possible open time slots and will put them into the results table.

9. The results table shows all of the results given the filters. It is only filled once the 'Go' button (#8) is pressed. It is scrollable with the mouse if the table has too many entries.

10. This is where the user will get their own specific secret code so that they can make edits when they come back to the schedule.

11. The 'Open' buttons will allow participants to register for a time slot. Once the participant clicks the button, a small text field will come up and their strong can be entered and saved there. If the enter key is hit, the slot will be changed from 'Open' to reserved to the person that claimed it. 

12. Here, the user will enter their individual secret code in order to retrieve their meeting.

13. This arrow button will process the secret code and will send a request to get the corresponding meeting.


---------------------
organizerMarkedUp.pdf
---------------------
1. The schedule name can be edited in this text field. It will update the corresponding title/label at the top (also marked with a 1).

2. A dropdown menu showing 10,15,20,30, and 60 will appear when clicked. The organizer can select one and that will be the length of all of their meetings.

3. This is the input text field for the start day of the organizer's schedule. It can modified to be an earlier day at anytime. The input is just as the label suggests, month in two digit format/day/etc.

4. This is the input text field for the end day of the organizer's schedule. It can modified to be a later day at anytime. The input is just as the label suggests, month in two digit format/day/etc.

5. The organizer will input their daily start time here in the form of HH/AM|PM

6. The organizer will input their daily end time here in the form of HH/AM|PM

7. This is a dropdown menu where the organizer will set their availability or unavailability for a given time range. The options in the menu are available, and unavailable.

8. This is the from time that must be entered in the form of HH:MM. This will set the availability or unavailability from this given time to a given end time.

9. This is the end time that must be entered in the form of HH:MM. This will set the availability or unavailability from a given time to this end time.

10. The organizer can select a range of options in this dropdown menu. Options are weekdays (Monday's, Tuesday's, etc.), all weekdays, or a specific day (9/13/2018).

11. The 'set' button will be clickable once the organizer enters their secret code. Then, once clicked, it will send a request to the Scheduler to make all of the changes that were filled out in the fields.

12. This is where the organizer gets their secret code to log back in later.

13. The orange boxes represent a time slot that has been taken by another user. Their name will appear in the box.

14. A green 'Open' button can be clicked and will be changed to a red 'Closed' button. This represents that the time slot is currently open, but when clicked, it will change and update the schedule to make the slot closed.

15. A red 'Closed' button can be clicked and will be changed to a green 'Open' button. This represents that the time slot is currently closed, but when clicked, it will change and update the schedule to make the slot open. 

16. Once the organizer wants to make changes, they must enter their secret code. Once they do that, the set button (#10) will be clickable and they will be able to modify the schedule.

--------------------
sysAdminMarkedUp.pdf
--------------------
1. This is a text field where the SA will enter a positive integer representing the number of days old they want their meetings to be in the results box.

2. Once the SA has entered a number, they will hit this 'Go' button, which will then send the request to the system and post the results to #3

3. The results box will show the meetings that are N days old once requested with the Go button (#2)

4. The SA can hit this delete button to meetings that are in the results box

5. This is a textbox where a number, N, willl be entered that represents the number of hours old a meeting has to be to appear in the results box.

6. Upon clicking, this 'Go' button will process the number in the text field and will make a request to the system for an update in the results box

7. This results box will show the meetings that are N hours old once #6 is pressed.
