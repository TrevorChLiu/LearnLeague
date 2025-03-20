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
    app.run(host="0.0.0.0", port=5000)
