-- 1. Insert Categories
INSERT INTO categories (name)
VALUES ('Produce'),
       ('Dairy & Eggs'),
       ('Bakery'),
       ('Meat & Seafood'),
       ('Pantry');

-- 2. Insert 10 Real-World Grocery Products
INSERT INTO products (name, price, description, category_id)
VALUES ('Organic Bananas', 1.99, 'Fresh bunch of organic bananas. Great for snacking or smoothies.', 1),
       ('Honeycrisp Apples (1 lb)', 2.49, 'Sweet, crisp, and juicy apples sourced from local orchards.', 1),
       ('Whole Milk (1 Gallon)', 3.49, 'Farm-fresh whole milk, fortified with Vitamin D.', 2),
       ('Plain Greek Yogurt (32 oz)', 4.99, 'Thick and creamy unsweetened Greek yogurt, packed with protein.', 2),
       ('Artisan Sourdough Loaf', 3.99, 'Freshly baked naturally leavened sourdough bread with a crispy crust.', 3),
       ('Butter Croissants (4-Pack)', 5.49, 'Flaky, buttery pastries baked fresh daily.', 3),
       ('Boneless Skinless Chicken Breasts', 8.99,
        'Lean, high-quality chicken breasts. Approximately 1.5 lbs per package.', 4),
       ('Ground Beef 80/20 (1 lb)', 6.49, 'Fresh ground beef, 80% lean and 20% fat. Perfect for burgers or tacos.', 4),
       ('Extra Virgin Olive Oil (16 oz)', 12.99, 'Cold-pressed extra virgin olive oil imported from Italy.', 5),
       ('Organic Marinara Sauce (24 oz)', 4.49,
        'Rich and savory tomato basil pasta sauce made with organic ingredients.', 5);