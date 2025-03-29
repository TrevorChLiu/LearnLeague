from flask import Flask, request
import mysql.connector
import sys

app = Flask(__name__)

# Database connection
def get_db_connection():
    return mysql.connector.connect(
        host="localhost", 
        user="Trev",    # Change to yours
        password="20031231Lch.",    # Change to yours
        database="learn_league_db"
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
            user_id VARCHAR(50) NOT NULL PRIMARY KEY,
            hashed_password INT NOT NULL
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

    conn = get_db_connection()
    cursor = conn.cursor()

    try:
        cursor.execute("INSERT INTO users (user_id, hashed_password) VALUES (%s, %s)", 
                       (user_id, hashed_password))
        conn.commit()
        return "User created"
    except mysql.connector.Error as err:
        return "Failed to create user: " + str(err)
    finally:
        cursor.close()
        conn.close()

# Test connection
@app.route('/test_conn', methods=['POST'])
def test_conn():
    data = request.json
    msg = data.get("message")

    conn = get_db_connection()
    cursor = conn.cursor()

    try:
        cursor.execute("INSERT INTO testtable (message) VALUES (%s)", 
                       (msg, ))
        conn.commit()
        return "Test Passes!"
    except mysql.connector.Error as err:
        return "Test fails!"
    finally:
        cursor.close()
        conn.close()

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
