### Description
In the newest commit, you can see a folder called *LearnLeague* and a Python file called *remote_server.py*: 
* *LearnLeague* is the main part of our project, developing in Android Emulator. 
* *remote_server.py* is a Python server providing an API to operate the remote database (in the development phase, it is actually located on developers' local devices).
## Dependencies (Highly important)
Please download the things below before launching this project:
* MySQL server. You can download the community from the official website: https://dev.mysql.com/downloads/mysql/. After that you can download the server from the community easily.
* Flask and MySQL connector for Python. Run this code in your terminal: *pip install flask mysql-connector-python*.
**Note: Remember to add MySQL's bin path to your Environment Variable!!!**

After you have downloaded the two dependencies, please do:
1. Open the MySQL CML command line client (You can search it on 	your start bar or type *mysql -u root -p* in your termial) and type: *create database learn_league_db;* to create our simulated remote database.
2. Open `my.cnf` (Linux/Mac) or `my.ini` (Windows), modify (if any) or add: *bind-address = 0.0.0.0*
3. Open `remote_server.py`, change `user` and `password` to your own user name and password of MySQL service.

**Now, whenever you want to interact with the database (like insert a record), you should run the server first by typing:** *python remote_server.py* 

### Check your configuration

Having completed the configuration above, you can check your configuration by:
1. Open MySQL command line, and type: *Use learn_league_db;*  *create table testtable (message varchar(30) primary key);* so you get an empty table.
2. Launch the remote server `remote_server.py` and the project `LearnLeague` on your Android emulator. You will see that there are 4 buttons on the navigation bar: select the 2nd one, which is `Ranking` session. You will see a `Test Connection` bar at the center of the screen.  If everything implement correctly, after clicking the button, you can see that there is a welcome message the empty table `testtable` by typing *select \* from testtable*.
