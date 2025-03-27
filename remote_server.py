from flask import Flask, request
import mysql.connector

app = Flask(__name__)

# Database connection
def get_db_connection():
    return mysql.connector.connect(
        host="localhost", 
        user="Trev",    # Change to yours
        password="20031231Lch.",    # Change to yours
        database="learn_league_db"
    )

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

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000, debug=True)
