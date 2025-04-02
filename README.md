### Description
You can see a folder called *LearnLeague* and a Python file called *remote_server.py*: 
* `LearnLeague` is the main part of our project, developed in Android Emulator. 
* `remote_server.py` is a Python server providing an API to manipulate data in the remote database (in the development phase, it is actually located on developers' local devices).
## Dependencies (Highly important)
Please download following dependencies before launching this project:
* MySQL server. You can download the community from the official website: https://dev.mysql.com/downloads/mysql/. After that you can download the server from the community easily.
* Flask, MySQL connector, and PIL for Python. Run this code in your terminal: `pip install flask mysql-connector-python PIL`.

After two dependencies are downloaded:
1. Open the MySQL CML command line client and type: `create database learn_league_db;` to create our simulated remote database.
2. Open `my.cnf` (Linux/Mac) or `my.ini` (Windows), modify (if any) or add: *bind-address = 0.0.0.0*
3. Open `remote_server.py`, change `user` and `password` to your own user name and password of MySQL service.

**Before launchinging the project, please run the database server by typing: `python remote_server.py` in project's root directory.**


## Database and project update

Sometimes the table in the database might be outdated after project files update. In this case, run `python remote_server.py reset` in the home directory to reset the database.