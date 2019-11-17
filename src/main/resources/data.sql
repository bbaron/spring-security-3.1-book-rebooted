insert into calendar_users(id, email, password, first_name, last_name)
values (0, 'user1@example.com', '{bcrypt}$2a$10$RiEwfArlwAuYkSEKTBD/p.IyggNlB1T31X7GH/1IdABuAgkkbNHVi', 'User1', 'One');
insert into calendar_users(id, email, password, first_name, last_name)
values (1, 'admin1@example.com', '{bcrypt}$2a$10$UaK4ceQ1mw4XtJxebxwoI.cPlI9P4tYShb1F18BR1orBirEVrweh.', 'Admin1', '1');
insert into calendar_users(id, email, password, first_name, last_name)
values (2, 'user2@example.com', '{bcrypt}$2a$10$1rWbS0AGHDD85JcZoixIzeqQ3n1ZVN30930/2KgSOPbkRlPeHU7OK', 'User2', 'Two');

insert into events (id, when, summary, description, owner, attendee)
values (100, '2013-10-04 20:30:00', 'Birthday Party', 'This is going to be a great birthday', 0, 1);
insert into events (id, when, summary, description, owner, attendee)
values (101, '2013-12-23 13:00:00', 'Conference Call', 'Call with the client', 2, 0);
insert into events (id, when, summary, description, owner, attendee)
values (102, '2014-01-23 11:30:00', 'Lunch', 'Eating lunch together', 1, 2);