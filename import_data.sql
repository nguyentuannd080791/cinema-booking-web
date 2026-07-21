-- SQL Server script to insert sample data (20 records per table)
-- Except for: users, customer, staff, admin (only inserting minimal records to satisfy foreign key constraints)

-- 1. Insert minimal User, Customer, and Staff records first to satisfy foreign key constraints
-- (These tables do not need 20 records as per request, but need some records to be referenced by Booking)
SET IDENTITY_INSERT users ON;
INSERT INTO users (user_id, email, hash_password, full_name, phone, role, status) VALUES
(1, 'customer1@gmail.com', 'hashedpassword1', 'Nguyen Van A', '0912345678', 'CUSTOMER', 'ACTIVE'),
(2, 'customer2@gmail.com', 'hashedpassword2', 'Tran Thi B', '0987654321', 'CUSTOMER', 'ACTIVE'),
(3, 'staff1@gmail.com', 'hashedpassword3', 'Le Van C', '0901234567', 'STAFF', 'ACTIVE'),
(4, 'staff2@gmail.com', 'hashedpassword4', 'Pham Thi D', '0934567890', 'STAFF', 'ACTIVE');
SET IDENTITY_INSERT users OFF;

INSERT INTO customer (customer_id, loyalty_points, created_at) VALUES
(1, 100, GETDATE()),
(2, 250, GETDATE());

INSERT INTO staff (staff_id, employee_code) VALUES
(3, 'EMP001'),
(4, 'EMP002');


-- 2. Insert 20 records into ACTOR table
SET IDENTITY_INSERT actor ON;
INSERT INTO actor (actor_id, actor_name) VALUES
(1, 'Tom Hanks'),
(2, 'Leonardo DiCaprio'),
(3, 'Robert Downey Jr.'),
(4, 'Scarlett Johansson'),
(5, 'Brad Pitt'),
(6, 'Johnny Depp'),
(7, 'Morgan Freeman'),
(8, 'Christian Bale'),
(9, 'Matt Damon'),
(10, 'Hugh Jackman'),
(11, 'Jennifer Lawrence'),
(12, 'Angelina Jolie'),
(13, 'Keanu Reeves'),
(14, 'Denzel Washington'),
(15, 'Meryl Streep'),
(16, 'Anne Hathaway'),
(17, 'Chris Hemsworth'),
(18, 'Tom Cruise'),
(19, 'Will Smith'),
(20, 'Ryan Reynolds');
SET IDENTITY_INSERT actor OFF;


-- 3. Insert 20 records into CATEGORY table
SET IDENTITY_INSERT category ON;
INSERT INTO category (category_id, category_name) VALUES
(1, 'Action'),
(2, 'Adventure'),
(3, 'Comedy'),
(4, 'Drama'),
(5, 'Sci-Fi'),
(6, 'Horror'),
(7, 'Thriller'),
(8, 'Romance'),
(9, 'Fantasy'),
(10, 'Mystery'),
(11, 'Animation'),
(12, 'Family'),
(13, 'Biography'),
(14, 'History'),
(15, 'Crime'),
(16, 'Musical'),
(17, 'Documentary'),
(18, 'Western'),
(19, 'Sport'),
(20, 'War');
SET IDENTITY_INSERT category OFF;


-- 4. Insert 20 records into ROOM table
SET IDENTITY_INSERT room ON;
INSERT INTO room (room_id, room_name, capacity, [row], [col]) VALUES
(1, 'Cinema Hall 1', 100, 10, 10),
(2, 'Cinema Hall 2', 80, 8, 10),
(3, 'Cinema Hall 3', 120, 10, 12),
(4, 'IMAX Theater 1', 150, 10, 15),
(5, 'IMAX Theater 2', 150, 10, 15),
(6, 'VIP Lounge 1', 40, 5, 8),
(7, 'VIP Lounge 2', 40, 5, 8),
(8, 'Gold Class Room', 30, 5, 6),
(9, 'Cinema Hall 4', 100, 10, 10),
(10, 'Cinema Hall 5', 100, 10, 10),
(11, 'Cinema Hall 6', 80, 8, 10),
(12, 'Cinema Hall 7', 80, 8, 10),
(13, 'Cinema Hall 8', 120, 10, 12),
(14, 'Cinema Hall 9', 120, 10, 12),
(15, '4DX Room 1', 60, 6, 10),
(16, '4DX Room 2', 60, 6, 10),
(17, 'Kids Cinema', 50, 5, 10),
(18, 'Couple Suite 1', 20, 4, 5),
(19, 'Couple Suite 2', 20, 4, 5),
(20, 'Screen X Room', 110, 10, 11);
SET IDENTITY_INSERT room OFF;


-- 5. Insert 20 records into MOVIE table
-- Note: MovieStatus values are: COMINGSOON, NOWSHOwING, NOLONGERSHOING
SET IDENTITY_INSERT movie ON;
INSERT INTO movie (movie_id, title, duration, release_date, description, poster_url, language, age_rating, status) VALUES
(1, 'Inception', 148, '2010-07-16', 'A thief who steals corporate secrets through the use of dream-sharing technology.', 'inception.jpg', 'English', 'PG-13', 'NOWSHOwING'),
(2, 'The Dark Knight', 152, '2008-07-18', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham.', 'dark_knight.jpg', 'English', 'PG-13', 'NOLONGERSHOING'),
(3, 'Avatar: The Way of Water', 192, '2022-12-16', 'Jake Sully lives with his newfound family formed on the extrasolar moon Pandora.', 'avatar_2.jpg', 'English', 'PG-13', 'NOWSHOwING'),
(4, 'Interstellar', 169, '2014-11-07', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity survival.', 'interstellar.jpg', 'English', 'PG-13', 'NOWSHOwING'),
(5, 'Gladiator II', 140, '2024-11-22', 'Years after witnessing the death of the revered hero Maximus.', 'gladiator_2.jpg', 'English', 'R', 'COMINGSOON'),
(6, 'Dune: Part Two', 166, '2024-03-01', 'Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators.', 'dune_2.jpg', 'English', 'PG-13', 'NOWSHOwING'),
(7, 'Oppenheimer', 180, '2023-07-21', 'The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb.', 'oppenheimer.jpg', 'English', 'R', 'NOWSHOwING'),
(8, 'The Matrix', 136, '1999-03-31', 'When a beautiful stranger leads computer hacker Neo to a forbidding underworld.', 'matrix.jpg', 'English', 'R', 'NOLONGERSHOING'),
(9, 'Titanic', 194, '1997-12-19', 'A seventeen-year-old aristocrat falls in love with a kind but poor artist aboard the luxurious R.M.S. Titanic.', 'titanic.jpg', 'English', 'PG-13', 'NOLONGERSHOING'),
(10, 'Spirited Away', 125, '2001-07-20', 'During her family move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods.', 'spirited_away.jpg', 'Japanese', 'PG', 'NOLONGERSHOING'),
(11, 'Parasite', 132, '2019-05-30', 'Greed and class discrimination threaten the newly formed symbiotic relationship.', 'parasite.jpg', 'Korean', 'R', 'NOLONGERSHOING'),
(12, 'Avengers: Endgame', 181, '2019-04-26', 'After the devastating events of Avengers: Infinity War, the universe is in ruins.', 'avengers_endgame.jpg', 'English', 'PG-13', 'NOLONGERSHOING'),
(13, 'Spider-Man: Beyond the Spider-Verse', 140, '2026-12-18', 'The thrilling conclusion to Miles Morales animated trilogy.', 'spiderman_beyond.jpg', 'English', 'PG', 'COMINGSOON'),
(14, 'John Wick: Chapter 4', 169, '2023-03-24', 'John Wick uncovers a path to defeating The High Table.', 'john_wick_4.jpg', 'English', 'R', 'NOWSHOwING'),
(15, 'Whiplash', 106, '2014-10-10', 'A promising young drummer enrolls at a cut-throat music conservatory.', 'whiplash.jpg', 'English', 'R', 'NOLONGERSHOING'),
(16, 'The Shawshank Redemption', 142, '1994-09-23', 'Over the course of several years, two convicts form a friendship.', 'shawshank.jpg', 'English', 'R', 'NOLONGERSHOING'),
(17, 'Kimi no Na wa', 106, '2016-08-26', 'Two strangers find themselves linked in a bizarre way.', 'your_name.jpg', 'Japanese', 'PG', 'NOLONGERSHOING'),
(18, 'Pulp Fiction', 154, '1994-10-14', 'The lives of two mob hitmen, a boxer, a gangster and his wife.', 'pulp_fiction.jpg', 'English', 'R', 'NOLONGERSHOING'),
(19, 'The Godfather', 175, '1972-03-24', 'The aging patriarch of an organized crime dynasty transfers control to his reluctant son.', 'godfather.jpg', 'English', 'R', 'NOLONGERSHOING'),
(20, 'Inside Out 2', 96, '2024-06-14', 'Follow Riley, in her teenage years, encountering new Emotions.', 'inside_out_2.jpg', 'English', 'PG', 'NOWSHOwING');
SET IDENTITY_INSERT movie OFF;


-- 6. Insert 20 records into MOVIE_ACTOR relationship table
INSERT INTO movie_actor (movie_id, actor_id) VALUES
(1, 2), -- Inception - Leonardo DiCaprio
(1, 7), -- Inception - Morgan Freeman
(2, 8), -- The Dark Knight - Christian Bale
(3, 4), -- Avatar 2 - Scarlett Johansson
(4, 9), -- Interstellar - Matt Damon
(4, 16), -- Interstellar - Anne Hathaway
(5, 14), -- Gladiator II - Denzel Washington
(6, 11), -- Dune 2 - Jennifer Lawrence
(7, 3), -- Oppenheimer - Robert Downey Jr.
(7, 9), -- Oppenheimer - Matt Damon
(8, 13), -- The Matrix - Keanu Reeves
(9, 2), -- Titanic - Leonardo DiCaprio
(9, 12), -- Titanic - Angelina Jolie
(12, 3), -- Avengers - Robert Downey Jr.
(12, 4), -- Avengers - Scarlett Johansson
(12, 17), -- Avengers - Chris Hemsworth
(14, 13), -- John Wick 4 - Keanu Reeves
(14, 14), -- John Wick 4 - Denzel Washington
(18, 19), -- Pulp Fiction - Will Smith
(20, 20); -- Inside Out 2 - Ryan Reynolds


-- 7. Insert 20 records into MOVIE_CATEGORY relationship table
INSERT INTO movie_category (movie_id, category_id) VALUES
(1, 1), -- Inception - Action
(1, 5), -- Inception - Sci-Fi
(2, 1), -- The Dark Knight - Action
(2, 7), -- The Dark Knight - Thriller
(3, 1), -- Avatar 2 - Action
(3, 5), -- Avatar 2 - Sci-Fi
(4, 4), -- Interstellar - Drama
(4, 5), -- Interstellar - Sci-Fi
(5, 1), -- Gladiator II - Action
(5, 4), -- Gladiator II - Drama
(6, 2), -- Dune 2 - Adventure
(6, 5), -- Dune 2 - Sci-Fi
(7, 4), -- Oppenheimer - Drama
(7, 13), -- Oppenheimer - Biography
(8, 1), -- The Matrix - Action
(8, 5), -- The Matrix - Sci-Fi
(9, 4), -- Titanic - Drama
(9, 8), -- Titanic - Romance
(10, 9), -- Spirited Away - Fantasy
(10, 11); -- Spirited Away - Animation


-- 8. Insert 20 records into SEAT table
-- SeatType values: NORMAL, VIP, COUPLE
SET IDENTITY_INSERT seat ON;
INSERT INTO seat (seat_id, seat_number, row_index, col_index, seat_type, room_id) VALUES
(1, 'A1', 1, 1, 'NORMAL', 1),
(2, 'A2', 1, 2, 'NORMAL', 1),
(3, 'B1', 2, 1, 'NORMAL', 1),
(4, 'B2', 2, 2, 'NORMAL', 1),
(5, 'E5', 5, 5, 'VIP', 1),
(6, 'E6', 5, 6, 'VIP', 1),
(7, 'K1', 10, 1, 'COUPLE', 1),
(8, 'K2', 10, 2, 'COUPLE', 1),
(9, 'A1', 1, 1, 'NORMAL', 2),
(10, 'A2', 1, 2, 'NORMAL', 2),
(11, 'D5', 4, 5, 'VIP', 2),
(12, 'D6', 4, 6, 'VIP', 2),
(13, 'A1', 1, 1, 'NORMAL', 3),
(14, 'A2', 1, 2, 'NORMAL', 3),
(15, 'F5', 6, 5, 'VIP', 3),
(16, 'A1', 1, 1, 'NORMAL', 4),
(17, 'A2', 1, 2, 'NORMAL', 4),
(18, 'G7', 7, 7, 'VIP', 4),
(19, 'A1', 1, 1, 'NORMAL', 5),
(20, 'A2', 1, 2, 'NORMAL', 5);
SET IDENTITY_INSERT seat OFF;


-- 9. Insert 20 records into SHOWTIME table
SET IDENTITY_INSERT showtime ON;
INSERT INTO showtime (showtime_id, start_time, end_time, price, movie_id, room_id) VALUES
(1, '2026-07-10 09:00:00', '2026-07-10 11:28:00', 90000.00, 1, 1),
(2, '2026-07-10 12:00:00', '2026-07-10 14:32:00', 95000.00, 2, 2),
(3, '2026-07-10 15:00:00', '2026-07-10 18:12:00', 120000.00, 3, 4),
(4, '2026-07-10 19:00:00', '2026-07-10 21:49:00', 100000.00, 4, 3),
(5, '2026-07-11 10:00:00', '2026-07-11 12:46:00', 95000.00, 6, 1),
(6, '2026-07-11 13:30:00', '2026-07-11 16:30:00', 110000.00, 7, 5),
(7, '2026-07-11 17:00:00', '2026-07-11 19:49:00', 105000.00, 14, 2),
(8, '2026-07-11 20:30:00', '2026-07-11 22:06:00', 90000.00, 20, 3),
(9, '2026-07-12 09:00:00', '2026-07-12 11:28:00', 90000.00, 1, 9),
(10, '2026-07-12 12:00:00', '2026-07-12 14:49:00', 100000.00, 4, 10),
(11, '2026-07-12 15:00:00', '2026-07-12 17:46:00', 95000.00, 6, 11),
(12, '2026-07-12 18:30:00', '2026-07-12 21:19:00', 105000.00, 14, 12),
(13, '2026-07-13 10:00:00', '2026-07-13 12:06:00', 90000.00, 20, 13),
(14, '2026-07-13 13:00:00', '2026-07-13 16:12:00', 120000.00, 3, 14),
(15, '2026-07-13 17:00:00', '2026-07-13 20:00:00', 110000.00, 7, 15),
(16, '2026-07-14 09:00:00', '2026-07-14 11:28:00', 90000.00, 1, 16),
(17, '2026-07-14 12:00:00', '2026-07-14 14:49:00', 100000.00, 4, 17),
(18, '2026-07-14 15:00:00', '2026-07-14 17:46:00', 95000.00, 6, 18),
(19, '2026-07-14 18:00:00', '2026-07-14 20:49:00', 105000.00, 14, 19),
(20, '2026-07-14 21:00:00', '2026-07-14 22:36:00', 90000.00, 20, 20);
SET IDENTITY_INSERT showtime OFF;


-- 10. Insert 20 records into BOOKING table
-- BookingStatus values: PENDING, PAID, CANCELLED
SET IDENTITY_INSERT booking ON;
INSERT INTO booking (booking_id, booking_time, booking_status, total_amount, customer_id, staff_id) VALUES
(1, '2026-07-09 08:30:00', 'PAID', 90000.00, 1, 3),
(2, '2026-07-09 09:15:00', 'PAID', 180000.00, 2, 3),
(3, '2026-07-09 10:00:00', 'PENDING', 95000.00, 1, NULL),
(4, '2026-07-09 11:20:00', 'PAID', 120000.00, 2, 4),
(5, '2026-07-09 12:45:00', 'CANCELLED', 100000.00, 1, 3),
(6, '2026-07-09 13:10:00', 'PAID', 190000.00, 2, 4),
(7, '2026-07-09 14:05:00', 'PAID', 110000.00, 1, 3),
(8, '2026-07-09 15:30:00', 'PAID', 210000.00, 2, 4),
(9, '2026-07-09 16:22:00', 'PENDING', 90000.00, 1, NULL),
(10, '2026-07-09 17:40:00', 'PAID', 90000.00, 2, 3),
(11, '2026-07-09 18:15:00', 'PAID', 100000.00, 1, 3),
(12, '2026-07-09 19:00:00', 'PAID', 95000.00, 2, 4),
(13, '2026-07-09 19:45:00', 'PAID', 105000.00, 1, 3),
(14, '2026-07-09 20:30:00', 'PAID', 90000.00, 2, 4),
(15, '2026-07-09 21:00:00', 'PAID', 120000.00, 1, 3),
(16, '2026-07-09 21:30:00', 'PAID', 110000.00, 2, 4),
(17, '2026-07-09 22:00:00', 'CANCELLED', 90000.00, 1, NULL),
(18, '2026-07-09 22:15:00', 'PAID', 100000.00, 2, 3),
(19, '2026-07-09 22:45:00', 'PAID', 95000.00, 1, 4),
(20, '2026-07-09 23:00:00', 'PAID', 105000.00, 2, 3);
SET IDENTITY_INSERT booking OFF;


-- 11. Insert 20 records into TICKET table
-- TicketStatus values: VALID, USED, INVALID
SET IDENTITY_INSERT ticket ON;
INSERT INTO ticket (ticket_id, status, price, showtime_id, booking_id, seat_id) VALUES
(1, 'USED', 90000.00, 1, 1, 1),
(2, 'USED', 90000.00, 1, 2, 2),
(3, 'USED', 90000.00, 1, 2, 3),
(4, 'VALID', 95000.00, 2, 3, 9),
(5, 'USED', 120000.00, 3, 4, 18),
(6, 'INVALID', 100000.00, 4, 5, 13),
(7, 'USED', 95000.00, 5, 6, 1),
(8, 'USED', 95000.00, 5, 6, 2),
(9, 'USED', 110000.00, 6, 7, 19),
(10, 'USED', 105000.00, 7, 8, 9),
(11, 'USED', 105000.00, 7, 8, 10),
(12, 'VALID', 90000.00, 8, 9, 13),
(13, 'USED', 90000.00, 9, 10, 1),
(14, 'USED', 100000.00, 10, 11, 19),
(15, 'USED', 95000.00, 11, 12, 9),
(16, 'USED', 105000.00, 12, 13, 13),
(17, 'USED', 90000.00, 13, 14, 13),
(18, 'USED', 120000.00, 14, 15, 16),
(19, 'USED', 110000.00, 15, 16, 19),
(20, 'INVALID', 90000.00, 16, 17, 1);
SET IDENTITY_INSERT ticket OFF;


-- 12. Insert 20 records into PAYMENT table
-- PaymentStatus values: PENDING, SUCCESS, FAILED
SET IDENTITY_INSERT payment ON;
INSERT INTO payment (payment_id, payment_method, amount, payment_time, status, booking_id) VALUES
(1, 'Credit Card', 90000.00, '2026-07-09 08:31:00', 'SUCCESS', 1),
(2, 'E-Wallet', 180000.00, '2026-07-09 09:16:00', 'SUCCESS', 2),
(3, 'E-Wallet', 95000.00, '2026-07-09 10:02:00', 'PENDING', 3),
(4, 'Credit Card', 120000.00, '2026-07-09 11:22:00', 'SUCCESS', 4),
(5, 'Cash', 100000.00, '2026-07-09 12:46:00', 'FAILED', 5),
(6, 'E-Wallet', 190000.00, '2026-07-09 13:12:00', 'SUCCESS', 6),
(7, 'Cash', 110000.00, '2026-07-09 14:06:00', 'SUCCESS', 7),
(8, 'Credit Card', 210000.00, '2026-07-09 15:32:00', 'SUCCESS', 8),
(9, 'E-Wallet', 90000.00, '2026-07-09 16:23:00', 'PENDING', 9),
(10, 'Credit Card', 90000.00, '2026-07-09 17:42:00', 'SUCCESS', 10),
(11, 'Cash', 100000.00, '2026-07-09 18:16:00', 'SUCCESS', 11),
(12, 'E-Wallet', 95000.00, '2026-07-09 19:01:00', 'SUCCESS', 12),
(13, 'Credit Card', 105000.00, '2026-07-09 19:47:00', 'SUCCESS', 13),
(14, 'E-Wallet', 90000.00, '2026-07-09 20:32:00', 'SUCCESS', 14),
(15, 'Cash', 120000.00, '2026-07-09 21:02:00', 'SUCCESS', 15),
(16, 'Credit Card', 110000.00, '2026-07-09 21:31:00', 'SUCCESS', 16),
(17, 'Credit Card', 90000.00, '2026-07-09 22:01:00', 'FAILED', 17),
(18, 'E-Wallet', 100000.00, '2026-07-09 22:16:00', 'SUCCESS', 18),
(19, 'E-Wallet', 95000.00, '2026-07-09 22:46:00', 'SUCCESS', 19),
(20, 'Credit Card', 105000.00, '2026-07-09 23:01:00', 'SUCCESS', 20);
SET IDENTITY_INSERT payment OFF;
