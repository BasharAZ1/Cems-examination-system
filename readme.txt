Thank you for using CEMS system.

Precondition for using the system:
1. In C Drive, open two new folders: CEMS_server, CEMS_client.
2. In CEMS_server, open a new folder: External. In External folder put all the csv files that came with the project.
3. When client receives files from the server, they are saved under C://CEMS_client
Further details: (*).
4. The password for MySQL should be Aa123456
5. in order to see the pictures you have to change the path in fxml files to ..\src\resources


(*) Thank you for using CEMS server. Here are a few things you need to know:

Preconditions for using the system:
1. In MYSQL load our sceme "cems".
2. Right click on your connection in MYSQL, in the "Advanced" tab under "Other" add the flag: OPT_LOCAL_INFILE=1
3. Restart MYSQL
4. If that doesn't work, in the MYSQL do the query: SET GLOBAL local_infile = true;
5. In the first use of this system, our server needs to import users, courses and subjects information 
from an external system.
To do so, you must put their files in the filepath: C://CEMS_server//External
The files are: person.csv, course.csv, subject.csv

3. After that you can click the "import" button in the server's window (you only need to do this once).


Additional details:
- In the main table of the server you can see all the users that are logged currently to the system.
When a user logs out, you can see his status immediately.

- Under the table, you can see the amount of users of each type (teacher, student, principal) that are currently logged in.