SmartECommerceSystem
=====================
Overview
-------------

SmartECommerceSystem is a JavaFX-based desktop application for managing an e-commerce platform. It integrates relational (PostgreSQL) and NoSQL (MongoDB) databases to handle products, categories, orders, and user reviews. The system includes role-based access control, allowing administrators to manage products and categories while regular users can browse products, add items to their cart, place orders, and submit reviews.

Features
========
User Features
-------------

User registration, login, and password reset.

Browse products with filtering and sorting by name, price, or date.

Add products to a shopping cart.

Place orders linked to the logged-in user.

Submit, view, and delete reviews.

Validation and alert messages for input errors and system feedback.


Administrator Features
----------------------

Add, edit, and delete products.

Add, edit, and delete categories.

View and manage all orders, including updating status or deleting orders.

Role-based access control to restrict sensitive actions.


General Features
----------------

Integration with PostgreSQL for relational data (users, products, orders, categories).

Integration with MongoDB for review storage.

Dynamic tables with sorting, editing, and deletion capabilities.

Cart management across scenes.

Alerts and confirmations for user actions.


Technologies Used
-----------------

Java 21

JavaFX 21 (FXML for UI)

PostgreSQL 42.7.8 (for relational data)

MongoDB Driver 5.6.2 (for review storage)

BCrypt 0.4 (for password hashing)

ControlsFX 11.2.1 (enhanced UI components)

FormsFX 11.6.0 and ValidatorFX 0.6.1 (form validation)

TilesFX 21.0.9, FXGL 11.17, BootstrapFX 0.4.0, Ikonli 12.3.1 (UI enhancements)

JUnit 5.12.1 (unit testing)


Project Structure
-----------------
org.example.smartecommercesystem
│
├─ sql                 # Folder containing Entity Relational Diagram and SQL queries
├─ controller          # JavaFX controllers (ProductController, CartController, DashboardController, etc.)
├─ dao                 # Data access objects for PostgreSQL and MongoDB
├─ model               # Entity classes (User, Product, Category, Order, Review)
├─ session             # Singleton session management for logged-in users
├─ HelloApplication.java   # Main JavaFX application launcher
└─ resources
    ├─ fxml            # FXML UI files
   

Database Design
===============
PostgreSQL
----------

Users: Stores user information, passwords (hashed), and roles.

Products: Stores product details, category linkage, stock, price, and creation date.

Categories: Product categories.

Orders: Linked to users, contains order items, total, status, and timestamps.

OrderItems: Details for products within each order.


MongoDB
-------
Reviews: Stores product reviews including user ID, rating, comment, product ID, and timestamp.

Installation & Setup

Clone the repository:

git clone <repository-url>
cd SmartECommerceSystem


Setup PostgreSQL database:
--------------------------

Create a database for the application.

Configure tables for users, products, categories, orders, and order items.

Update database connection details in DAO classes.


Setup MongoDB database:
-----------------------

Ensure MongoDB is running locally or remotely.

No strict schema required; the ReviewNoSQLDAO manages document insertion and retrieval.

Build & Run with Maven:

mvn clean javafx:run


Usage
-----

Launch the application to open the Sign Up page.

Create a new account or log in using existing credentials.

Navigate through the dashboard to access products, categories, orders, cart, and reviews.

Admin users will see additional options for managing products, categories, and orders.

Add products to the cart, view total prices, and place orders.

Submit reviews for products; only the user who created a review can delete it.


Code Highlights
---------------

Session Management: Ensures a single logged-in user is tracked throughout the application.

CartController: Handles adding, removing, and clearing items; computes total price; links orders to the logged-in user.

ProductController: Handles CRUD operations, sorting, filtering, and review management.

OrderController: Allows admins to update order statuses and delete orders.

Alerts: Comprehensive feedback to users for successful operations, errors, or invalid input.


Future Improvements
-------------------

Add product image upload and display.

Implement notifications for order status changes.

Integrate unit and integration tests for DAOs and controllers.
