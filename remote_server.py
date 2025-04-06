from flask import Flask, request, jsonify
import mysql.connector
import json
import os
from datetime import datetime

app = Flask(__name__)

# Database connection
def get_db_connection():
    return mysql.connector.connect(
        host="localhost",
        user="Trev",    # Change to yours
        password="20031231Lch.",    # Change to yours
        database="learn_league_db"
    )

# load db.json data
def load_db():
    with open('db.json', 'r') as file:
        return json.load(file)

# save db.json data
def save_db(db_data):
    with open('db.json', 'w') as file:
        json.dump(db_data, indent=2, fp=file)

# connection test
@app.route('/test_conn', methods=['POST'])
def test_conn():
    # MySQL 测试连接
    if request.content_type == 'application/json':
        data = request.json
        msg = data.get("message")

        conn = get_db_connection()
        cursor = conn.cursor()

        try:
            cursor.execute("INSERT INTO testtable (message) VALUES (%s)",
                        (msg, ))
            conn.commit()
            return jsonify({"message": "MySQL Connection successful!"})
        except mysql.connector.Error as err:
            return jsonify({"error": f"MySQL Connection failed: {str(err)}"}), 500
        finally:
            cursor.close()
            conn.close()
    else:
        # JSON Server connection test
        return jsonify({"message": "Connection successful!"})

# get post list
@app.route('/posts', methods=['GET', 'POST'])
def handle_posts():
    db_data = load_db()

    if request.method == 'GET':
        # get post list
        posts = db_data.get('posts', [])
        user_id = request.args.get('userId')

        if user_id:
            # filter threads post by specific users
            user_posts = [post for post in posts if post.get('userId') == user_id]
            return jsonify({"posts": user_posts})

        return jsonify({"posts": posts})

    elif request.method == 'POST':
        # create new posts
        data = request.json
        title = data.get('title')
        content = data.get('content')

        # new posts
        new_post = {
            "id": f"post{int(datetime.now().timestamp() * 1000)}",
            "title": title,
            "content": content,
            "userId": "user1",  # assume it's post by 1st usr
            "userName": "Alice Chen",
            "userAvatarUrl": "https://i.pravatar.cc/150?img=1",
            "createdAt": int(datetime.now().timestamp() * 1000),
            "likeCount": 0,
            "commentCount": 0,
            "isLikedByCurrentUser": False
        }

        # add to database
        db_data['posts'].append(new_post)
        save_db(db_data)

        return jsonify({"post": new_post})

# get detailed post info
@app.route('/posts/<post_id>', methods=['GET'])
def get_post(post_id):
    db_data = load_db()
    posts = db_data.get('posts', [])

    post = next((p for p in posts if p.get('id') == post_id), None)

    if not post:
        return jsonify({"error": "Post not found"}), 404

    return jsonify({"post": post})

# get comments
@app.route('/posts/<post_id>/comments', methods=['GET'])
def get_post_comments(post_id):
    db_data = load_db()
    comments = db_data.get('comments', [])

    post_comments = [c for c in comments if c.get('postId') == post_id]

    return jsonify({"comments": post_comments})

# deal with post likes
@app.route('/posts/<post_id>/like', methods=['POST'])
def like_post(post_id):
    db_data = load_db()
    posts = db_data.get('posts', [])

    post = next((p for p in posts if p.get('id') == post_id), None)

    if not post:
        return jsonify({"error": "Post not found"}), 404

    current_like_state = post.get('isLikedByCurrentUser', False)
    post['isLikedByCurrentUser'] = not current_like_state

    if current_like_state:
        post['likeCount'] = max(0, post['likeCount'] - 1)
    else:
        post['likeCount'] += 1

    save_db(db_data)

    return jsonify({"isLiked": post['isLikedByCurrentUser']})

# deal with comment likes
@app.route('/comments/<comment_id>/like', methods=['POST'])
def like_comment(comment_id):
    db_data = load_db()
    comments = db_data.get('comments', [])

    comment = next((c for c in comments if c.get('id') == comment_id), None)

    if not comment:
        return jsonify({"error": "Comment not found"}), 404

    current_like_state = comment.get('isLikedByCurrentUser', False)
    comment['isLikedByCurrentUser'] = not current_like_state

    if current_like_state:
        comment['likeCount'] = max(0, comment['likeCount'] - 1)
    else:
        comment['likeCount'] += 1

    save_db(db_data)

    return jsonify({"isLiked": comment['isLikedByCurrentUser']})

# handle create new comments
@app.route('/comments', methods=['POST'])
def create_comment():
    db_data = load_db()
    data = request.json
    post_id = data.get('postId')
    content = data.get('content')
    parent_comment_id = data.get('parentCommentId')

    # get posts
    post = next((p for p in db_data.get('posts', []) if p.get('id') == post_id), None)
    if not post:
        return jsonify({"error": "Post not found"}), 404

    # create new comments
    new_comment = {
        "id": f"comment{int(datetime.now().timestamp() * 1000)}",
        "postId": post_id,
        "content": content,
        "userId": "user1",  # assume 1st user's posts
        "userName": "Alice Chen",
        "userAvatarUrl": "https://i.pravatar.cc/150?img=1",
        "createdAt": int(datetime.now().timestamp() * 1000),
        "likeCount": 0,
        "isLikedByCurrentUser": False
    }

    if parent_comment_id:
        new_comment["parentCommentId"] = parent_comment_id

    # add to data base
    db_data['comments'].append(new_comment)

    # update comment counts
    post['commentCount'] += 1

    save_db(db_data)

    return jsonify({"comment": new_comment})

# get user info
@app.route('/users/<user_id>', methods=['GET'])
def get_user(user_id):
    db_data = load_db()
    users = db_data.get('users', [])

    user = next((u for u in users if u.get('id') == user_id), None)

    if not user:
        return jsonify({"error": "User not found"}), 404

    return jsonify({"user": user})

if __name__ == '__main__':
    # if db.json not found, create new empty structure
    if not os.path.exists('db.json'):
        empty_db = {
            "test_conn": {
                "message": "Connection successful"
            },
            "posts": [],
            "comments": [],
            "users": []
        }
        with open('db.json', 'w') as file:
            json.dump(empty_db, indent=2, fp=file)

    app.run(host="0.0.0.0", port=3000)

