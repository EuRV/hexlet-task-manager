-- :name list-users :?
-- :doc Get all the users
SELECT * FROM users;

-- :name get-user-by-id :? :1
-- :doc Get a single user by ID
SELECT * FROM users
  WHERE id = :id;

-- :name create-user :i!
INSERT INTO users (first_name, last_name, email, password_digest)
  VALUES (:first-name, :last-name, :email, :password-digest);