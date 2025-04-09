from flask import Flask, request, jsonify, send_file
import mysql.connector
import sys
from io import BytesIO
from PIL import Image
import hashlib
import time
import json
from datetime import datetime

app = Flask(__name__)

# Database connection
def get_db_connection():
    return mysql.connector.connect(
        host="localhost", 
        user="Trev",    # Change to yours
        password="20031231Lch.",    # Change to yours
        database="learn_league_db",
        consume_results=True  # Automatically clean mysql buffer
    )

# Create all necessary tables
def initialize_db():
    conx = get_db_connection()
    cursor = conx.cursor()
    responses = []

    responses.append(check_table_user(conx, cursor))
    responses.append(check_table_follows(conx, cursor))
    responses.append(check_table_study_records(conx, cursor))
    
    cursor.close()
    conx.close()

    print("; ".join(responses))

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
            avatar LONGBLOB,
            avatar_version BIGINT DEFAULT %s
        )
        """, (int(time.time()),))
        conx.commit()
        response = "Table 'users' created."
    else:
        response = "Table 'users' exists."
    return response

def check_table_follows(conx, cursor):
    # Check table existence
    cursor.execute("SHOW TABLES LIKE 'follows'")
    
    # If the table doesn't exist, create it
    if not cursor.fetchone():
        cursor.execute("""
        CREATE TABLE follows (
            follower_id VARCHAR(20) NOT NULL,
            followee_id VARCHAR(20) NOT NULL,
            add_time INT NOT NULL AUTO_INCREMENT,
            PRIMARY KEY (add_time)
        );
        """)
        response = "Table 'follows' created."
    else:
        response = "Table 'follows' exists."
    conx.commit()
    return response

def check_table_study_records(conx, cursor):
    cursor.execute("SHOW TABLES LIKE 'study_records'")

    if not cursor.fetchone():
        cursor.execute("""
            CREATE TABLE study_records (
                user_id VARCHAR(20) NOT NULL,
                seconds INT NOT NULL DEFAULT 0,
                record_date DATE NOT NULL,
                PRIMARY KEY (user_id, record_date)
            )
        """)
        response = "Table 'study_records' created."
        conx.commit()
    else:
        response = "Table 'study_records' exists."
    
    return response

def insert_study_record(user_id, seconds, record_date=None):
    conx = get_db_connection()
    cursor = conx.cursor()
    
    try:
        if record_date is None:
            cursor.execute("""
                INSERT INTO study_records (user_id, seconds, record_date)
                VALUES (%s, %s, CURDATE())
                ON DUPLICATE KEY UPDATE seconds = seconds + %s;
                """, (user_id, seconds, seconds))
        else:
            cursor.execute("""
                INSERT INTO study_records (user_id, seconds, record_date)
                VALUES (%s, %s, %s)
                ON DUPLICATE KEY UPDATE seconds = seconds + %s;
                """, (user_id, seconds, record_date, seconds))
        conx.commit()
        print("New record for: " + user_id)
    except mysql.connector.Error as err:
        print("Failed to insert study record: " + str(err))
    finally:
        cursor.close()
        conx.close()    

@app.route('/insert_study_record_today', methods=['POST'])
def insert_study_record_today():
    data = request.json
    user_id = data.get("userid")
    seconds = data.get("seconds")
    try:
        insert_study_record(user_id, seconds)
        return "Study record inserted."
    except mysql.connector.Error as err:
        return "Failed to insert study record: " + str(err), 500

def get_study_records_helper(conx, cursor, time):
    left = """SELECT u.user_id, u.hashed_password, u.username, u.email, u.avatar_version, 
                (SELECT COUNT(*) FROM follows WHERE follower_id = u.user_id) AS followee_count, 
                (SELECT COUNT(*) FROM follows WHERE followee_id = u.user_id) AS follower_count,
                COALESCE(SUM(s.seconds), 0) AS total_seconds
                FROM users u
                LEFT JOIN study_records s ON u.user_id = s.user_id"""
    right = "GROUP BY u.user_id ORDER BY total_seconds DESC;"
    if time == "day":
        middle = " WHERE s.record_date = CURDATE() "
    elif time == "week":
        middle = " WHERE YEARWEEK(s.record_date) = YEARWEEK(NOW()) "
    elif time == "month":
        middle = " WHERE YEAR(s.record_date) = YEAR(NOW()) AND MONTH(s.record_date) = MONTH(NOW()) "
    statement = left + middle + right
    
    cursor.execute(statement)
    return cursor.fetchall()


@app.route('/get_study_records', methods=['GET'])
def get_study_records():
    conx = get_db_connection()
    cursor = conx.cursor()
    try:
        records = [
            get_study_records_helper(conx, cursor, "day"),
            get_study_records_helper(conx, cursor, "week"),
            get_study_records_helper(conx, cursor, "month")
        ]
        return jsonify(records)
    except mysql.connector.Error as err:
        print("Failed to get study recors:", str(err))
        return "Failed to get study recors:", str(err)
    finally:
        cursor.close()
        conx.close()


@app.route('/create_user', methods=['POST'])
def create_user():
    data = request.json
    user_id = data.get("userid")
    hashed_password = data.get("hashedpassword")

    conx = get_db_connection()
    cursor = conx.cursor()

    try:
        cursor.execute("SELECT user_id FROM users WHERE user_id = %s", (user_id,))
        if cursor.fetchone() is not None:
            return "User ID exists", 409
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

    db = load_db()
    posts = db.get("posts")
    for post in posts:
        if post.get('userId') == user_id:
            post['userName'] = username
            post['userAvatarVersion'] = avatar_version

    comments = db.get("comments")
    for comment in comments:
        if comment.get('userId') == user_id:
            comment['userName'] = username
            comment['userAvatarVersion'] = avatar_version
    
    save_db(db)

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
            SELECT 
                u.user_id, 
                u.hashed_password, 
                u.username, 
                u.email, 
                u.avatar_version,
                (SELECT COUNT(*) FROM follows WHERE follower_id = u.user_id),
                (SELECT COUNT(*) FROM follows WHERE followee_id = u.user_id)
            FROM users u
            WHERE u.user_id = %s
""", (user_id,))

        user = cursor.fetchone()

        if user:
            user_info = {
                'userid': user[0],
                'hashedpassword': user[1],
                'username': user[2],
                'email': user[3],
                'avatarversion': user[4],
                'numfollowee' : user[5],
                'numfollower' : user[6]
            }
            
            return jsonify(user_info), 200
        else:
            return f"404: User {user_id} Not Found!", 404
    except mysql.connector.Error as err:
        return "500: Failed to read user:" + err, 500
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
            image = Image.open(BytesIO(user[0]))
            image_type = image.format.lower()
            
            return send_file(BytesIO(user[0]), mimetype=f'image/{image_type}', as_attachment=False)
            
        else:
            return "Avatar not found!", 404
    except mysql.connector.Error as err:
        return 'Failed to retrieve avatar!', 500
    finally:
        cursor.close()
        conx.close()


@app.route('/update_follow', methods=['POST'])
def udpate_follow():
    data = request.json
    follower_id = data.get("followerid")
    followee_id = data.get("followeeid")
    method = data.get("method")

    conx = get_db_connection()
    cursor = conx.cursor()

    try:
        if method == "follow":
            cursor.execute("INSERT INTO follows (follower_id, followee_id) VALUES (%s, %s)", 
                        (follower_id, followee_id))
            conx.commit()
            return follower_id + " is now a follower of " + followee_id
        elif method == "unfollow":
            cursor.execute("DELETE FROM follows WHERE follower_id = %s AND followee_id = %s", (follower_id, followee_id))
            conx.commit()
            return follower_id + " has now unfollowed " + followee_id
        else:
            return "No method called " + method
    except mysql.connector.Error as err:
        return "Failed to udpate follows: " + str(err)
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


@app.route('/update_avatar/<user_id>', methods=['PUT'])
def update_avatar(user_id):
    try:
        # Get the raw binary data from the request body
        avatar_data = request.data

        if not avatar_data:
            return 'Avatar not provided!', 400

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
            return 'Avatar updated successfully', 200
        else:
            return 'User not found', 404

    except Exception as e:
        return f'Error updating avatar: {str(e)}', 500

@app.route('/get_follows_list', methods=['POST'])
def get_follows_list():
    data = request.json
    user_id = data.get("userid")
    method = data.get("method")

    conx = get_db_connection()
    cursor = conx.cursor()
    try:
        if method == "follower":
            cursor.execute("""
                SELECT 
                    u.user_id, 
                    u.hashed_password, 
                    u.username, 
                    u.email, 
                    u.avatar_version,
                    (SELECT COUNT(*) FROM follows WHERE follower_id = u.user_id) AS followee_count, 
                    (SELECT COUNT(*) FROM follows WHERE followee_id = u.user_id) AS follower_count
                FROM follows f
                JOIN users u ON f.follower_id = u.user_id 
                WHERE f.followee_id = %s
                ORDER BY f.add_time
            """, (user_id,))
            followers = cursor.fetchall()
            return jsonify(followers), 200
        elif method == "followee":
            cursor.execute("""
                SELECT 
                    u.user_id, 
                    u.hashed_password, 
                    u.username, 
                    u.email, 
                    u.avatar_version,   
                    (SELECT COUNT(*) FROM follows WHERE follower_id = u.user_id) AS followee_count, 
                    (SELECT COUNT(*) FROM follows WHERE followee_id = u.user_id) AS follower_count
                FROM follows f
                JOIN users u ON f.followee_id = u.user_id 
                WHERE f.follower_id = %s
                ORDER BY f.add_time
            """, (user_id,))
            
            followees = cursor.fetchall()
            
            return jsonify(followees), 200
        else:
            print("get_follow_list: undefined method: " + method)
            return "get_follow_list: undefined method: " + method, 500
    except mysql.connector.Error as err:
        print("500: Failed to read user:", str(err))
        return "500: Failed to read user!", 500
    finally:
        cursor.close()
        conx.close()



def sha256_hash(password):
    hashed_bytes = hashlib.sha256(password.encode("utf-8")).digest()
    return hashed_bytes.hex()  


def compress_image(image_data, quality=20):
    try:
        img = Image.open(BytesIO(image_data))

        buffer = BytesIO()
        img.save(buffer, format=img.format, quality=quality)

        return bytearray(buffer.getvalue())

    except Exception as e:
        print(f"Error compressing image: {e}")
        return bytearray()

# load db.json data
def load_db():
    with open('db.json', 'r') as file:
        return json.load(file)

# save db.json data
def save_db(db_data):
    with open('db.json', 'w') as file:
        json.dump(db_data, indent=2, fp=file)

def get_posts_or_comments(userId, type, db_data):
    data = list(reversed(db_data.get(type, [])))
    for i in range(len(data)):
        data[i]["isLikedByCurrentUser"] = userId in data[i]["likeList"]
    
    return data

# get post list
@app.route('/posts', methods=['GET', 'POST'])
def handle_posts():
    db_data = load_db()

    if request.method == 'GET':
        # get post list
        current_user_id = request.args.get('currentUserId')
        posts = get_posts_or_comments(current_user_id, "posts", db_data)
        user_id = request.args.get('userId')

        if user_id:
            # filter threads post by specific users
            user_posts = [post for post in posts if post.get('userId') == user_id]
            return jsonify({"posts": user_posts})

        return jsonify({"posts": posts})

    elif request.method == 'POST':
        # create new posts
        data = request.json
        user_id = data.get("userId")
        username = data.get("username")
        user_avatar_url = data.get("userAvatarUrl")
        user_avatar_version = data.get("userAvatarVersion")
        title = data.get('title')
        content = data.get('content')

        # new posts
        new_post = {
            "id": f"post{int(datetime.now().timestamp() * 1000)}",
            "title": title,
            "content": content,
            "userId": user_id, 
            "userName": username,
            "userAvatarUrl": user_avatar_url,
            "userAvatarVersion": user_avatar_version,
            "createdAt": int(datetime.now().timestamp() * 1000),
            "likeCount": 0,
            "commentCount": 0,
            "isLikedByCurrentUser": None, # initially not defined
            "likeList":[]
        }

        # add to database
        db_data['posts'].append(new_post)
        save_db(db_data)

        return jsonify({"post": new_post})



@app.route('/users/<user_id>/posts', methods=['GET'])
def get_user_posts(user_id):
    db_data = load_db()
    current_user_id = request.args.get('currentUserId', db_data)
    posts = get_posts_or_comments(current_user_id, "posts", db_data)

    user_posts = [post for post in posts if post.get('userId') == user_id]
        
    if not user_posts:
        return jsonify({"error": "Post not found"}), 404

    return jsonify({"posts": user_posts})

@app.route('/users/<user_id>/comments', methods=['GET'])
def get_user_comments(user_id):
    db_data = load_db()

    current_user_id = request.args.get('currentUserId')
    comments = get_posts_or_comments(current_user_id, "comments", db_data)

    user_comments  = [comment for comment in comments if comment.get('userId') == user_id]

    if not user_comments:
        return jsonify({"error": "Post not found"}), 404

    return jsonify({"comments": user_comments})

@app.route('/users/<user_id>/replies', methods=['GET'])
def get_user_replies(user_id):
    db_data = load_db()

    current_user_id = request.args.get('currentUserId')
    posts = get_posts_or_comments(current_user_id, "posts", db_data)
    comments = get_posts_or_comments(current_user_id, "comments", db_data)

    user_post_ids = [post.get('id') for post in posts if post.get('userId') == user_id]
    user_replies  = [comment for comment in comments if comment.get('postId') in user_post_ids]

    if not user_replies:
        return jsonify({"error": "Post not found"}), 404

    return jsonify({"replies": user_replies})


# get detailed post info
@app.route('/posts/<post_id>', methods=['GET'])
def get_post(post_id):
    db_data = load_db()
    current_user_id = request.args.get('currentUserId')
    posts = get_posts_or_comments(current_user_id, "posts", db_data)

    post = next((p for p in posts if p.get('id') == post_id), None)

    if not post:
        return jsonify({"error": "Post not found"}), 404

    return jsonify({"post": post})

# get comments
@app.route('/posts/<post_id>/comments', methods=['GET'])
def get_post_comments(post_id):
    db_data = load_db()

    current_user_id = request.args.get('currentUserId')
    comments = get_posts_or_comments(current_user_id, "comments", db_data)

    post_comments = [c for c in comments if c.get('postId') == post_id]

    return jsonify({"comments": post_comments})

# deal with post likes
@app.route('/posts/<post_id>/like', methods=['POST'])
def like_post(post_id):
    db_data = load_db()
    
    data = request.json
    user_id = data.get('userId')
    posts = get_posts_or_comments(user_id, "posts", db_data)

    post = next((p for p in posts if p.get('id') == post_id), None)

    if not post:
        return jsonify({"error": "Post not found"}), 404

    # current_like_state = post.get('isLikedByCurrentUser', False)
    # post['isLikedByCurrentUser'] = not current_like_state
    like_list = post.get('likeList')
    if like_list is None:
        like_list = []
        post['likeList'] = like_list
    
    if user_id not in like_list:
        like_list.append(user_id)
    else:
        like_list.remove(user_id)

    post['likeCount'] = len(like_list)

    save_db(db_data)

    return jsonify({"isLiked": user_id in like_list})

# deal with comment likes
@app.route('/comments/<comment_id>/like', methods=['POST'])
def like_comment(comment_id):
    db_data = load_db()

    data = request.json
    user_id = data.get('userId')

    comments = get_posts_or_comments(user_id, "comments", db_data)

    comment = next((c for c in comments if c.get('id') == comment_id), None)

    if not comment:
        return jsonify({"error": "Comment not found"}), 404


    like_list = comment.get('likeList')
    if like_list is None:
        like_list = []
        comment['likeList'] = like_list
    
    if user_id not in like_list:
        like_list.append(user_id)
    else:
        like_list.remove(user_id)

    comment['likeCount'] = len(like_list)
    save_db(db_data)

    return jsonify({"isLiked": user_id in like_list})

# handle create new comments
@app.route('/comments', methods=['POST'])
def create_comment():
    db_data = load_db()
    data = request.json
    post_id = data.get('postId')
    content = data.get('content')
    parent_comment_id = data.get('parentCommentId')
    
    user_id = data.get("userId")
    username = data.get("username")
    user_avatar_url = data.get("userAvatarUrl")
    user_avatar_version = data.get("userAvatarVersion")
    
    

    # get posts
    post = next((p for p in db_data.get('posts', []) if p.get('id') == post_id), None)
    if not post:
        return jsonify({"error": "Post not found"}), 404

    # create new comments
    new_comment = {
        "id": f"comment{int(datetime.now().timestamp() * 1000)}",
        "postId": post_id,
        "content": content,
        "userId": user_id,
        "userName": username,
        "userAvatarUrl": user_avatar_url,
        "userAvatarVersion": user_avatar_version,
        "createdAt": int(datetime.now().timestamp() * 1000),
        "likeCount": 0,
        "isLikedByCurrentUser": None,
        "likeList": []
    }

    if parent_comment_id:
        new_comment["parentCommentId"] = parent_comment_id

    # add to data base
    db_data['comments'].append(new_comment)

    # update comment counts
    post['commentCount'] += 1

    save_db(db_data)

    return jsonify({"comment": new_comment})

@app.route('/add_course', methods=['POST'])
def add_course():
    db = load_db()
    data = request.json
    user_id = data.get('userId')
    title = data.get('title')
    thumbnail_url = data.get('thumbnailUrl')
    watch_time_in_seconds = data.get('watchTimeInSeconds')
    video_url = data.get('videoUrl')

    new_course = {
        'title': title,
        'thumbnailUrl': thumbnail_url,
        'watchTimeInSeconds': watch_time_in_seconds,
        "videoUrl": video_url
    }

    users = db.get('users')
    if users is None:
        users = {}
        db['users'] = users
    
    user = users.get(user_id)
    if user is None:
        user = {}
        users[user_id] = user

    user[title] = new_course

    save_db(db)
    return "Course " + title + " added."

@app.route('/get_courses/<user_id>', methods=['GET'])
def get_courses(user_id):
    db_data = load_db()

    users = db_data.get('users', {})
    user = users.get(user_id, {})

    return jsonify({"courses": list(user.values())})


if __name__ == '__main__':
    if len(sys.argv) == 1:
        initialize_db()
        app.run(host="0.0.0.0", port=5000, debug=True)
    else: 
        if "test" in sys.argv:
            pass
        if "reset" in sys.argv:
            drop_all_tables()
            save_db({"posts": [], "comments": [], "users":{}})
            print("Dropped json database")
        if "demo" in sys.argv:
            users = ["Alan", "Bob", "Charlie", "Delta", "Eve","Fiona", "Trev", "Somebody", "Mia", "Hunter", "Lester"]
            
            # json db demo
            file_path = 'demo_json/demo_db.json'
            with open(file_path, 'r') as file:
                demo_json = json.load(file)
            save_db(demo_json)

            conx = get_db_connection()
            cursor = conx.cursor()

            initialize_db()

            try:
                index = 0
                idx = 100;
                for user in users:
                    avatar_path = f"demo_images/{user}.jpg" 
                    
                    try:
                        with open(avatar_path, "rb") as fp:
                            avatar_byte = fp.read()
                            avatar_byte = compress_image(avatar_byte)
                    except FileNotFoundError:
                        avatar_byte = None  

                    cursor.execute("INSERT INTO users (user_id, hashed_password, username, email, avatar) VALUES (%s, %s, %s, %s, %s)", 
                                (user, sha256_hash(user + "Password"), user + str(idx), user + "@gmail.com", avatar_byte))
                    
                    for i in range(index + 1, len(users)):
                        cursor.execute("INSERT INTO follows (follower_id, followee_id) VALUES (%s, %s)", (users[i], user))

                    if index % 2 == 0:
                        insert_study_record(user, index * 1000 + idx)
                    else:
                        insert_study_record(user, index * 500 + idx, "2025-04-01")

                    idx += 1
                    index += 1;
                
                insert_study_record("Alan", 10200, "2025-04-30")
                insert_study_record("Bob", 10200, "2025-04-30")
                insert_study_record("Delta", 10200, "2025-04-30")
                conx.commit()
                print("Demo created")
            except mysql.connector.Error as err:
                print("Failed to create demo: " + str(err))
            finally:
                cursor.close()
                conx.close()


