-- ====================================================================
-- Thapar Journey Connect — Java Lab Edition
-- Pre-Seeded Academic Lab Database Initialization (data.sql)
-- Embedded H2 Schema Seed for Zero-Configuration Localhost Execution
-- ====================================================================

-- 1. Official Transit Destinations
MERGE INTO "destinations" ("id", "name", "icon", "description", "popular_time", "distance", "avg_cab_fare") KEY("id") VALUES
('railway', 'Patiala Railway Station', '🚆', 'Find students travelling from campus to Patiala Railway Station.', 'Peak: 4:00 PM - 8:00 PM', '8.5 km from Main Gate', '₹150 - ₹200 total'),
('bus_stand', 'Patiala Bus Stand', '🚌', 'Find students travelling from campus to Patiala Bus Stand for intercity buses.', 'Peak: 6:00 AM - 10:00 AM', '6.2 km from Main Gate', '₹120 - ₹160 total'),
('chd_airport', 'Chandigarh Airport', '✈️', 'Coordinate rides for flights departing from Shaheed Bhagat Singh Airport.', 'Peak: Morning & Evening Flights', '62 km from Main Gate', '₹1,200 - ₹1,500 total'),
('del_airport', 'Delhi Airport', '✈️', 'Connect with students taking long-distance cabs or trains to IGI Airport.', 'Peak: Overnight & Early Morning', '250 km from Main Gate', '₹4,500 - ₹5,500 total');

-- 2. 18 Official TIET Hostels (Halls of Residence)
MERGE INTO "hostels" ("id", "name", "gender", "cluster") KEY("id") VALUES
('HA', 'Agira Hall (Previously known as Hostel-A)', 'Boys', 'North Quad'),
('HB', 'Amritam Hall (Previously known as Hostel-B)', 'Boys', 'North Quad'),
('HC', 'Prithvi Hall (Previously known as Hostel-C)', 'Boys', 'North Quad'),
('HD', 'Neeram Hall (Previously known as Hostel-D)', 'Boys', 'Central Quad'),
('HH', 'Vyan Hall (Previously known as Hostel-H)', 'Boys', 'Central Quad'),
('HJ', 'Tejas Hall (Previously known as Hostel-J)', 'Boys', 'East Quad'),
('HK', 'Ambaram Hall (Previously known as Hostel-K)', 'Boys', 'East Quad'),
('HL', 'Viyat Hall (Previously known as Hostel-L)', 'Boys', 'East Quad'),
('HM', 'Anantam Hall (Previously known as Hostel-M)', 'Boys', 'West Quad'),
('HO', 'Vyom Hall (Previously known as Hostel-O)', 'Boys', 'West Quad'),
('FRFG', 'Hostel-FRF/G', 'Boys', 'West Quad'),
('HE', 'Vasudha Hall - Block E (Previously known as Hostel-E)', 'Girls', 'Vasudha Complex'),
('HG', 'Vasudha Hall - Block G (Previously known as Hostel-G)', 'Girls', 'Vasudha Complex'),
('HI', 'Ira Hall (Previously known as Hostel-I)', 'Girls', 'Girls Quad'),
('HN', 'Ananta Hall (Previously known as Hostel-N)', 'Girls', 'Girls Quad'),
('Dhriti', 'Dhriti Hall (Hostel-PG)', 'Girls', 'Girls Quad'),
('HQ', 'Vahni Hall (Previously known as Hostel-Q)', 'Girls', 'Girls Quad'),
('Pavani', 'Hostel-Pavani Hall (Previously Known As PG-2)', 'Girls', 'Girls Quad');

-- 3. Official Hometown Community Hubs
MERGE INTO "communities" ("id", "name", "state", "icon", "members_count", "distance_from_thapar", "popular_transport", "description", "tags") KEY("id") VALUES
('del', 'Delhi NCR', 'Delhi / NCR', '🚇', 2, '250 km', 'Shatabdi Express, Long Cabs, Volvos', 'Student network for South Delhi, Noida, Gurgaon, West Delhi & IGI Airport transit.', 'Holiday Rush,Shatabdi Cohort,IGI Airport'),
('jal', 'Jalandhar', 'Punjab', '🏛️', 0, '145 km', 'GT Road Cabs, Intercity Trains', 'Student hub for Jalandhar, Phagwara & Doaba region travelers.', 'Weekly Commuters,Cab Sharing,GT Road'),
('ldh', 'Ludhiana', 'Punjab', '🏭', 0, '95 km', 'Direct Cabs, Passenger Trains', 'Commute corridor for Ludhiana travelers sharing cabs via GT Road.', 'Weekend Trips,Quick Ride,Day Scholars'),
('chd', 'Chandigarh & Tri-City', 'Chandigarh / Mohali / Panchkula', '🏙️', 0, '65 km', 'CTU Buses, Shared Cabs, Personal Cars', 'Travelers heading towards Mohali, Chandigarh Sector 17/43, and Panchkula.', 'Daily/Weekly,Mohali IT,Airport Cabs'),
('asr', 'Amritsar', 'Punjab', '🛕', 0, '225 km', 'Express Trains (Shan-e-Punjab), Highway Cabs', 'Connecting students heading towards Amritsar and Majha belt.', 'Long Weekend,Festival Travel,Trains'),
('bth', 'Bathinda', 'Punjab', '⚡', 0, '155 km', 'PRTC Buses, Local Trains', 'Malwa region student cohort sharing weekend travel.', 'Malwa Cohort,Weekend Trips'),
('jpr', 'Jaipur', 'Rajasthan', '🏰', 0, '470 km', 'Overnight Buses, Intercity Trains', 'Long-distance cohort coordinating semester breaks and Diwali holiday trips to Pink City.', 'Semester Breaks,Holiday Trains');

-- 4. Pre-Seeded Student Accounts (Password for all accounts: Thapar@123)
-- BCrypt Hash: $2a$10$wE9K2j3P9X9wN6Vq7bFfCeC0.9o81oO3V6q2y9iB1jK5Gf7c.7Aum
MERGE INTO "users" ("id", "name", "email", "password_hash", "hostel", "avatar", "phone", "degree", "study_year", "hometown", "reliability", "trips_completed", "role", "joined_community", "created_at") KEY("id") VALUES
('student_aarav', 'Aarav Sharma', 'student1@thapar.edu', '$2a$10$wE9K2j3P9X9wN6Vq7bFfCeC0.9o81oO3V6q2y9iB1jK5Gf7c.7Aum', 'Tejas Hall (Hostel-J)', 'AS', '9876543210', 'B.E. Computer Engineering', '3rd Year', 'Delhi NCR', 5.0, 4, 'student', 'del', CURRENT_TIMESTAMP),
('student_diya', 'Diya Verma', 'student2@thapar.edu', '$2a$10$wE9K2j3P9X9wN6Vq7bFfCeC0.9o81oO3V6q2y9iB1jK5Gf7c.7Aum', 'Vasudha Hall (Hostel-E)', 'DV', '9876543211', 'B.E. Electronics & Communication', '2nd Year', 'Delhi NCR', 4.9, 2, 'student', 'del', CURRENT_TIMESTAMP),
('admin_thapar', 'Thapar Admin Desk', 'admin@thapar.edu', '$2a$10$wE9K2j3P9X9wN6Vq7bFfCeC0.9o81oO3V6q2y9iB1jK5Gf7c.7Aum', 'Administrative Block', 'AD', '9888999999', 'Dean of Student Affairs Office', 'Administration', 'Patiala', 5.0, 0, 'admin', NULL, CURRENT_TIMESTAMP),
('student_default', 'Campus Student', 'student@thapar.edu', '$2a$10$wE9K2j3P9X9wN6Vq7bFfCeC0.9o81oO3V6q2y9iB1jK5Gf7c.7Aum', 'Tejas Hall (Hostel-J)', 'CS', '9876543212', 'B.E. Computer Engineering', '3rd Year', 'Delhi NCR', 5.0, 1, 'student', 'del', CURRENT_TIMESTAMP);
