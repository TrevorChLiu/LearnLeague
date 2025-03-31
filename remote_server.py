from flask import Flask, request, jsonify, send_file
import mysql.connector
import sys
import base64
from io import BytesIO
import imghdr
import hashlib

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
            hashed_password CHAR(64) NOT NULL,
            username VARCHAR(20),
            email VARCHAR(45) DEFAULT '',
            avatar MEDIUMBLOB,
            avatar_version int DEFAULT 0
        )
        """)
        conx.commit()
        response = "Table 'users' created."
    else:
        response = "Table 'users' exists."
    return response

def check_table_follow(conx, cursor):
    # Check table existence
    cursor.execute("SHOW TABLES LIKE 'follows'")
    
    # If the table doesn't exist, create it
    if not cursor.fetchone():
        cursor.execute("""
        CREATE TABLE follows (
            follower_id VARCHAR(20) NOT NULL,
            followee_id VARCHAR(20) NOT NULL,
            PRIMARY KEY (follower_id, followee_id)
            )
        """)
        response = "Table 'follows' created."
    else:
        response = "Table 'follows' exists."
    conx.commit()
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
    avatar_version = data.get("avatarversion")

    conx = get_db_connection()
    cursor = conx.cursor()
    
    cursor.execute("""
        UPDATE users 
        SET hashed_password = %s, username = %s, email = %s, avatar_version = %s
        WHERE user_id = %s
    """, (hashed_password, username, email, avatar_version, user_id))
    conx.commit()
    rows_affected = cursor.rowcount
    cursor.close()
    conx.close()
    return f"User information updated, {rows_affected} rows are affected."    

    

# Obtain a user's information
@app.route('/get_user', methods=['POST'])
def get_user():
    data = request.json
    user_id = data.get("userid")

    conx = get_db_connection()
    cursor = conx.cursor()
    try:
        cursor.execute("""
            SELECT user_id, hashed_password, username, email, avatar_version
            FROM users
            WHERE user_id = %s
        """, (user_id,))
        
        user = cursor.fetchone()

        if user:
            user_info = {
                'userid': user[0],
                'hashedpassword': user[1],
                'username': user[2],
                'email': user[3],
                'avatarversion': user[4]
            }
            
            return jsonify(user_info), 200
        else:
            return jsonify({
                f'message': "404: User {user[0]} Not Found!"
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



@app.route('/get_avatar/<user_id>', methods=['GET'])
def get_avatar(user_id):
    conx = get_db_connection()
    cursor = conx.cursor()
    try:
        cursor.execute("""SELECT avatar FROM users WHERE user_id = %s""", (user_id,))
        user = cursor.fetchone()
        if user and user[0]:
            image_type = imghdr.what(None, user[0])
            if not image_type:
                return jsonify({'message': 'Unsupported image format'}), 600
            
            return send_file(BytesIO(user[0]), mimetype=f'image/{image_type}', as_attachment=False)
            
        else:
            return "Avatar not found!", 404
    except mysql.connector.Error as err:
        return 'Failed to retrieve avatar!', 500
    finally:
        cursor.close()
        conx.close()


@app.route('/update_avatar/<user_id>', methods=['PUT'])
def update_avatar(user_id):
    try:
        # Get the raw binary data from the request body
        avatar_data = request.data  # Receives the bytes directly

        if not avatar_data:
            return jsonify({'message': 'Avatar not provided!'}), 400

        # Update the avatar in the database
        conx = get_db_connection()
        cursor = conx.cursor()

        cursor.execute("""
            UPDATE users 
            SET avatar = %s 
            WHERE user_id = %s
        """, (avatar_data, user_id))

        conx.commit()
        rows_affected = cursor.rowcount

        cursor.close()
        conx.close()

        if rows_affected > 0:
            return jsonify({'message': 'Avatar updated successfully'}), 200
        else:
            return jsonify({'message': 'User not found'}), 404

    except Exception as e:
        return jsonify({'message': f'Error updating avatar: {str(e)}'}), 500



def sha256_hash(password: str) -> str:
    hashed_bytes = hashlib.sha256(password.encode("utf-8")).digest()
    return hashed_bytes.hex()  

from PIL import Image
from io import BytesIO

def compress_image(image_data: bytes, quality=20) -> bytearray:
    try:
        img = Image.open(BytesIO(image_data))

        buffer = BytesIO()
        img.save(buffer, format=img.format, quality=quality)

        return bytearray(buffer.getvalue())

    except Exception as e:
        print(f"Error compressing image: {e}")
        return bytearray()

if __name__ == '__main__':
    if len(sys.argv) == 1:
        app.run(host="0.0.0.0", port=5000, debug=True)
    else: 
        if "reset" in sys.argv:
            drop_all_tables()
        if "demo" in sys.argv:
            users = ["Alan", "Bob", "Charlie", "Delta", "Eve","Fiona", "Trev", "Somebody"]
            idx = 100;

            conx = get_db_connection()
            cursor = conx.cursor()

            initialize_db()

            try:
                for user in users:
                    avatar_path = f"demo_images/{user}.jpg" 
                    try:
                        with open(avatar_path, "rb") as fp:
                            avatar_byte = fp.read()
                            # avatar_byte = compress_image(avatar_byte)
                    except FileNotFoundError:
                        avatar_byte = None  

                    cursor.execute("INSERT INTO users (user_id, hashed_password, username, email, avatar) VALUES (%s, %s, %s, %s, %s)", 
                                (user, user + "Password", user + str(idx), user + "@gmail.com", avatar_byte))
                    
                    conx.commit()
                    print("Demo created")
            except mysql.connector.Error as err:
                print("Failed to create demo: " + str(err))
            finally:
                cursor.close()
                conx.close()


