insert into user (id, username, roles, max_daily_calories, password) values (0, 'admin', 'ROLE_ADMIN', 2500, '$2a$10$G1IHrPEPraWmct2QcevI6u/kMmO8Zfnn41jJI9U4kHeGjxq5fvNFi');
insert into user (id, username, roles, max_daily_calories, password) values (1, 'manager', 'ROLE_MANAGER', 2500, '$2a$10$I8BgJVrLXg5ddo2BJe1DnuA5UfCWDNo4y2l501rbEOtvUhyiCPgpq');
insert into user (id, username, roles, max_daily_calories, password) values (2, 'adam', 'ROLE_USER', 2500, '$2a$10$pqxWQh1GKBI/srsq06vZe.QohSDzyax7ApSjmgl7lLoxrsAVuJ6Ja');


insert into meal (id, date, time, description, calories, user_id) values (0, '2020-01-29', '9:00:00.000', 'breakfast user 1', 600, 2);
insert into meal (id, date, time, description, calories, user_id) values (1, '2020-01-31', '15:00:00.000', 'lunch user 1', 500, 2);
insert into meal (id, date, time, description, calories, user_id) values (2, '2020-01-31', '19:00:00.000', 'dinner user 1', 300, 2);

insert into meal (id, date, time, description, calories, user_id) values (3, '2020-01-31', '10:00:00.000', 'breakfast manager', 399, 1);

