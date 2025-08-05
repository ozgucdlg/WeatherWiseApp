-- PostgreSQL Setup Script for WeatherWiseApp
-- Run this script as a PostgreSQL superuser (postgres)

-- Create the database
CREATE DATABASE weatherwise_db;

-- Create the user
CREATE USER weatherwise_user WITH PASSWORD 'weatherwise_password';

-- Grant privileges to the user
GRANT ALL PRIVILEGES ON DATABASE weatherwise_db TO weatherwise_user;

-- Connect to the weatherwise_db database
\c weatherwise_db;

-- Grant schema privileges
GRANT ALL ON SCHEMA public TO weatherwise_user;

-- The application will automatically create the tables when it starts
-- Tables will be created by the DatabaseInitializationService:
-- - users (id, username, email, password_hash, created_at, last_login_at, is_active)
-- - sessions (session_id, user_id, username, created_at, last_activity, is_valid)

-- Optional: Create indexes for better performance
-- These will be created automatically by the application, but you can create them manually if needed

COMMENT ON DATABASE weatherwise_db IS 'WeatherWiseApp database for user authentication and weather data'; 