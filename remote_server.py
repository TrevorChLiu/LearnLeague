from flask import Flask, request, jsonify
import mysql.connector
import sys
import base64

app = Flask(__name__)

# Database connection
def get_db_connection():
    return mysql.connector.connect(
        host="localhost", 
        user="Trev",    # Change to yours
        password="20031231Lch.",    # Change to yours
        database="learn_league_db",
        consume_results=True  # Helps with large data retrieval
    )

# Create all necessary tables
@app.route("/initialize_db", methods=["post"])
def initialize_db():
    conx = get_db_connection()
    cursor = conx.cursor()
    responses = []

    responses.append(check_table_user(conx, cursor))
    
    cursor.close()
    conx.close()

    return "; ".join(responses)

def check_table_user(conx, cursor):
    # Check table existence
    cursor.execute("SHOW TABLES LIKE 'users'")
    
    # If the table doesn't exist, create it
    if not cursor.fetchone():
        cursor.execute("""
        CREATE TABLE users (
            user_id VARCHAR(20) NOT NULL PRIMARY KEY,
            hashed_password INT NOT NULL,
            username VARCHAR(20),
            email VARCHAR(45) DEFAULT '',
            avatar BLOB
        )
        """)
        conx.commit()
        response = "Table 'users' created."
    else:
        response = "Table 'users' exists."
    return response


@app.route('/create_user', methods=['POST'])
def create_user():
    data = request.json
    user_id = data.get("userid")
    hashed_password = data.get("hashedpassword")

    conx = get_db_connection()
    cursor = conx.cursor()

    try:
        cursor.execute("INSERT INTO users (user_id, hashed_password, username) VALUES (%s, %s, %s)", 
                       (user_id, hashed_password, user_id))
        conx.commit()
        return "User created"
    except mysql.connector.Error as err:
        return "Failed to create user: " + str(err)
    finally:
        cursor.close()
        conx.close()

# Update an user's data
@app.route('/update_user', methods=['POST'])
def update_user():
    data = request.json
    user_id = data.get("userid")
    hashed_password = data.get("hashedpassword")
    username = data.get("username")
    email = data.get("email")
    avatar = data.get("avatar")

    conx = get_db_connection()
    cursor = conx.cursor()
    try:
        if avatar is None:  # If no avatar is provided (None sent from the client)
            cursor.execute("""
                UPDATE users 
                SET hashed_password = %s, username = %s, email = %s, avatar = NULL 
                WHERE user_id = %s
            """, (hashed_password, username, email, user_id))
        else:
            print("original: ", len(avatar))
            print("original bytes:", len(base64.b64decode(avatar)))
            cursor.execute("""
                UPDATE users 
                SET hashed_password = %s, username = %s, email = %s, avatar = %s 
                WHERE user_id = %s
            """, (hashed_password, username, email, base64.b64decode(avatar), user_id))
        conx.commit()
        rows_affected = cursor.rowcount
        return f"User information updated, {rows_affected} rows are affected."
    except mysql.connector.Error as err:
        return "Failed to update user: " + str(err)
    finally:
        cursor.close()
        conx.close()

# Obtain a user's information
@app.route('/get_user', methods=['POST'])
def get_user():
    data = request.json
    user_id = data.get("userid")

    conx = get_db_connection()
    cursor = conx.cursor()
    try:
        cursor.execute("""
            SELECT user_id, hashed_password, username, email, avatar
            FROM users
            WHERE user_id = %s
        """, (user_id,))
        
        user = cursor.fetchone()[:4]

        if user:


            user_info = {
                'userid': user[0],
                'hashedpassword': user[1],
                'username': user[2],
                'email': user[3],
            }
            print("Original: ", user[2])
            print("Non Jsonify: ", user_info.get("username"))
            return jsonify(user_info), 200
        else:
            return jsonify({
                'message': "404: User Not Found!"
            }), 404
    except mysql.connector.Error as err:
        return jsonify({
            "message": "500: Failed to read user!"
        }), 500
    finally:
        cursor.close()
        conx.close()


# Drop all tables from the db
def drop_all_tables():
    conx = get_db_connection()
    cursor = conx.cursor()

    try:
        # Disable foreign key checks
        cursor.execute("SET FOREIGN_KEY_CHECKS = 0;")

        # Get all table names
        cursor.execute("SHOW TABLES;")
        tables = cursor.fetchall()

        for table in tables:
            print(f"Dropping table: {table[0]}")
            cursor.execute(f"DROP TABLE {table[0]};")

        # Enable foreign key checks
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1;")

        conx.commit()
        print("Dropped all tables successfully.")

    except mysql.connector.Error as err:
        print("Fail to drop tables:", err)

    finally:
        cursor.close()
        conx.close()

if __name__ == '__main__':
    if len(sys.argv) == 1:
        app.run(host="0.0.0.0", port=5000, debug=True)
    elif "reset" in sys.argv:
        drop_all_tables()
