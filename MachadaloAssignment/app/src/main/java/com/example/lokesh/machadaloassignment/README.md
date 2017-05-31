

## link apks ##
link to the signed apk, currently tested on emulators and coolpad note3 lite, and vivo.
  - mediafire : http://www.mediafire.com/file/kjlrct1n0y81vru/assignment.apk
  - dropbox : https://www.dropbox.com/s/tvhrv1d88ukg41n/assignment.apk?dl=0
 
   min Sdk verion is 4.03 anf target api 25 of android 
  tested on android 7.0,6.0 and 5.01 versions
  regrading the apk there are some isuues with the layout when the devices resolution goes low
    for best experiance any device with 
 

## STRUCTURE ##
1. StudentList (Main Activity) class it implements LoaderManager.Loadercallback<Cursor> for CursorLoader to 
  update the listView.
2. In data folder the classes dealing with dtabse are stored 
  - Studentcontract defines the schema of the SQLite Database
  - StudentDbhelper which extends the SQliteOpenHelper for the CURD operation in the database
  - StudentProvider this is our custom ContentProvider class for abstracting the database layer
 
3. StudentCursorAdapter is the CustomCursorAdapter used for ListView and LoaderManager.


## About App ##
Brief structure of the project and its components :-
The Assignment main points implemneted as :
  1. TO Display name, age, gender, Marks in Hindi out of 100 , Marks in English out of 100,
      Average marks and total marks of 10 students -> ##### A ListView with CursorLoader #####
  2. Marks should be randomly chosen not hardcoded
      - the app at first inserst  10 random student data as it executes for first time. the student name is name+number all other
          attributes are randomly genrated from Random class
  3.User should be able to edit marks in Hindi or marks in English but not average or total marks
     - The user enters Edit mode as he clicks on the Listview in which all fields are editable are upadted as user wishes
        but Total and Average are computed before insertinga and updating data, user can edit name, age , marks.
     - there is another way by which user can add dta in the dtabase by using the floating ADD Butoon in main activity
       here there will be blank form for all entries and user can add the entry into the data base 
     - As soon user clicks Save button or select save option he comes back at main activity and new adta is displayed.
  4. Implementing features :
      (i) Sorting of above data by gender, or marks in Hindi or marks in English
          - in main activity the user can go to settings from option menu, it uses Sharedprefrence to remember the Sorting Prefrence order 
             of the Listview and user can change prefrnce from the settings menu then prefrence activity, sort by name.marks in hindi                  , english and Gender.
      
     (ii) Display student with highest average marks in two subjects:
        - in the main activity just above Listview the student name and marks are dispalyed
     (iii) Delete the data of a particular student
         - can delete from tapping on ListView then clicking delete button ot selection option delete
     (iv) As any attribute is changed (which can be edited name age marks) its diplayed
     (v) The name of subject highest provided just above list view and below highest avg.
   
     
