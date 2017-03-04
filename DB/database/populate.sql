insert into edificio values ('IST');
insert into edificio values ('FEUP');
insert into edificio values ('Catolica');
insert into edificio values ('ISEL');

-- Senhorios
insert into user values ('123456719', 'Jorge Poeta', '992323123');
insert into user values ('113056729', 'António Martins', '992333123');
insert into user values ('133956139', 'David Manuel', '992323124');
insert into user values ('143856248', 'Nuno Sousa', '992323125');
-- Arrendatarios
insert into user values ('153756357', 'Armando Sousa', '992323126');
insert into user values ('163656466', 'Gonçalo Santos', '992323127');
insert into user values ('173516575', 'Alberto Silva', '992323128');
insert into user values ('183426684', 'Rubim Guerreiro', '992323129');
insert into user values ('193336793', 'Anacleto Vieira', '993323123');
insert into user values ('103246782', 'Luis Raposo', '995323123');
insert into user values ('120456781', 'Rui Vitória', '997323123');	

insert into fiscal values (1, 'ASAE');
insert into fiscal values (2, 'CIA');
insert into fiscal values (3, 'PJ');
insert into fiscal values (4, 'FBI');

insert into alugavel values ('IST', 'Central', 'http://lorempixel.com/400/200/');
insert into alugavel values ('IST', 'DEI', 'http://lorempixel.com/400/200/');
insert into alugavel values ('IST', 'Lab1', 'http://lorempixel.com/400/200/');
insert into alugavel values ('IST', 'Lab2', 'http://lorempixel.com/400/200/');
insert into alugavel values ('IST', 'Lab3', 'http://lorempixel.com/400/200/');
insert into alugavel values ('IST', 'DEG', 'http://lorempixel.com/400/200/');
insert into alugavel values ('IST', 'DEQ', 'http://lorempixel.com/400/200/');

insert into alugavel values ('FEUP', 'Central', 'http://lorempixel.com/400/200/');
insert into alugavel values ('FEUP', 'DEI', 'http://lorempixel.com/400/200/');
insert into alugavel values ('FEUP', 'DEG', 'http://lorempixel.com/400/200/');
insert into alugavel values ('FEUP', 'DEQ', 'http://lorempixel.com/400/200/');
insert into alugavel values ('FEUP', 'Lab1', 'http://lorempixel.com/400/200/');
insert into alugavel values ('FEUP', 'Lab2', 'http://lorempixel.com/400/200/');
insert into alugavel values ('FEUP', 'Lab3', 'http://lorempixel.com/400/200/');
insert into alugavel values ('FEUP', 'Lab4', 'http://lorempixel.com/400/200/');

insert into alugavel values ('Catolica', 'Central', 'http://lorempixel.com/400/200/');
insert into alugavel values ('Catolica', 'DMKT', 'http://lorempixel.com/400/200/');
insert into alugavel values ('Catolica', 'Sala1', 'http://lorempixel.com/400/200/');
insert into alugavel values ('Catolica', 'Sala2', 'http://lorempixel.com/400/200/');
insert into alugavel values ('Catolica', 'DG', 'http://lorempixel.com/400/200/');

insert into alugavel values ('ISEL', 'Central', 'http://lorempixel.com/400/200/');
insert into alugavel values ('ISEL', 'DEI', 'http://lorempixel.com/400/200/');
insert into alugavel values ('ISEL', 'DEG', 'http://lorempixel.com/400/200/');
insert into alugavel values ('ISEL', 'DEQ', 'http://lorempixel.com/400/200/');

insert into arrenda values ('IST', 'Central', '123456719');
insert into arrenda values ('IST', 'DEI', '123456719');
insert into arrenda values ('IST', 'Lab1', '123456719');
insert into arrenda values ('IST', 'Lab2', '123456719');
insert into arrenda values ('IST', 'Lab3', '123456719');
insert into arrenda values ('IST', 'DEG', '123456719');
insert into arrenda values ('IST', 'DEQ', '123456719');

insert into arrenda values ('FEUP', 'Central', '113056729');
insert into arrenda values ('FEUP', 'DEI', '113056729');
insert into arrenda values ('FEUP', 'DEG', '113056729');
insert into arrenda values ('FEUP', 'DEQ', '113056729');
insert into arrenda values ('FEUP', 'Lab1', '113056729');
insert into arrenda values ('FEUP', 'Lab2', '113056729');
insert into arrenda values ('FEUP', 'Lab3', '113056729');
insert into arrenda values ('FEUP', 'Lab4', '113056729');

insert into arrenda values ('Catolica', 'Central', '133956139');
insert into arrenda values ('Catolica', 'DMKT', '133956139');
insert into arrenda values ('Catolica', 'Sala1', '133956139');
insert into arrenda values ('Catolica', 'Sala2', '133956139');
insert into arrenda values ('Catolica', 'DG', '133956139');

insert into arrenda values ('ISEL', 'Central', '143856248');
insert into arrenda values ('ISEL', 'DEI', '143856248');
insert into arrenda values ('ISEL', 'DEG', '143856248');
insert into arrenda values ('ISEL', 'DEQ', '143856248');

insert into fiscaliza values (1, 'IST', 'Central');
insert into fiscaliza values (1, 'IST', 'DEI');
insert into fiscaliza values (1, 'IST', 'Lab1');
insert into fiscaliza values (1, 'FEUP', 'Central');
insert into fiscaliza values (2, 'FEUP', 'Central');
insert into fiscaliza values (2, 'FEUP', 'DEQ');
insert into fiscaliza values (3, 'FEUP', 'DEI');
insert into fiscaliza values (4, 'Catolica', 'Central');
insert into fiscaliza values (2, 'Catolica', 'DG');

insert into espaco values ('IST', 'Central');
insert into espaco values ('IST', 'DEI');
insert into espaco values ('IST', 'DEG');
insert into espaco values ('IST', 'DEQ');

insert into posto values ('IST', 'Lab1', 'DEI');
insert into posto values ('IST', 'Lab2', 'DEI');
insert into posto values ('IST', 'Lab3', 'DEI');

insert into espaco values ('FEUP', 'Central');
insert into espaco values ('FEUP', 'DEI');
insert into espaco values ('FEUP', 'DEG');
insert into espaco values ('FEUP', 'DEQ');

insert into posto values ('FEUP', 'Lab1', 'DEG');
insert into posto values ('FEUP', 'Lab2', 'DEG');
insert into posto values ('FEUP', 'Lab3', 'DEG');
insert into posto values ('FEUP', 'Lab4', 'DEG');

insert into espaco values ('Catolica', 'Central');
insert into espaco values ('Catolica', 'DMKT');
insert into espaco values ('Catolica', 'DG');

insert into posto values ('Catolica', 'Sala1', 'DMKT');
insert into posto values ('Catolica', 'Sala2', 'DMKT');

insert into espaco values ('ISEL', 'Central');
insert into espaco values ('ISEL', 'DEI');
insert into espaco values ('ISEL', 'DEG');
insert into espaco values ('ISEL', 'DEQ');

insert into oferta values ('IST', 'Central', '2016-01-01', '2016-01-31', 19.99);
insert into oferta values ('IST', 'DEI', '2016-01-01', '2016-01-31', 49.99);
insert into oferta values ('IST', 'DEG', '2016-01-01', '2016-01-31', 39.99);
insert into oferta values ('IST', 'DEQ', '2016-01-01', '2016-01-31', 29.99);
insert into oferta values ('IST', 'DEI', '2016-02-01', '2016-02-28', 49.99);
insert into oferta values ('IST', 'DEG', '2016-02-01', '2016-02-28', 39.99);
insert into oferta values ('IST', 'DEQ', '2016-02-01', '2016-02-28', 29.99);	

insert into oferta values ('FEUP', 'Central', '2016-01-01', '2016-01-31', 23.99);
insert into oferta values ('FEUP', 'DEI', '2016-01-01', '2016-01-31', 32.00);
insert into oferta values ('FEUP', 'Lab1', '2016-01-01', '2016-01-31', 25.00);
insert into oferta values ('FEUP', 'Lab2', '2016-01-01', '2016-01-31', 15.00);
insert into oferta values ('FEUP', 'Lab3', '2016-01-01', '2016-01-31', 15.00);
insert into oferta values ('FEUP', 'Lab4', '2016-01-01', '2016-01-31', 15.00);

insert into oferta values ('Catolica', 'Central', '2016-01-01', '2016-01-31', 17.00);   
insert into oferta values ('Catolica', 'Sala1', '2016-01-01', '2016-01-31', 4.00);
insert into oferta values ('Catolica', 'Sala2', '2016-01-01', '2016-01-31', 2.00);
insert into oferta values ('Catolica', 'Central', '2016-02-01', '2016-02-28', 17.00);

insert into oferta values ('ISEL', 'Central', '2016-01-01', '2016-01-31', 89.00);
insert into oferta values ('ISEL', 'DEI', '2016-01-01', '2016-01-31', 29.00);
insert into oferta values ('ISEL', 'DEG', '2016-01-01', '2016-01-31', 49.00);
insert into oferta values ('ISEL', 'DEQ', '2016-01-01', '2016-01-31', 29.00);
insert into oferta values ('ISEL', 'Central', '2016-02-01', '2016-02-28', 89.00);
insert into oferta values ('ISEL', 'DEI', '2016-02-01', '2016-02-28', 29.00);
insert into oferta values ('ISEL', 'DEG', '2016-02-01', '2016-02-28', 49.00);
insert into oferta values ('ISEL', 'DEQ', '2016-02-01', '2016-02-28', 29.00);

insert into reserva values ('2016-1');
insert into reserva values ('2016-2');
insert into reserva values ('2016-3');
insert into reserva values ('2016-4');
insert into reserva values ('2016-5');
insert into reserva values ('2016-6');
insert into reserva values ('2016-7');
insert into reserva values ('2016-8');
insert into reserva values ('2016-9');
insert into reserva values ('2016-10');
insert into reserva values ('2016-11');
insert into reserva values ('2016-12');
insert into reserva values ('2016-13');

insert into aluga values ('IST', 'Central', '2016-01-01', '120456781', '2016-1');
insert into aluga values ('IST', 'DEI', '2016-01-01', '153756357', '2016-2');
insert into aluga values ('IST', 'DEG', '2016-01-01', '163656466', '2016-3');
insert into aluga values ('IST', 'DEQ', '2016-01-01', '163656466', '2016-4');
insert into aluga values ('IST', 'DEI', '2016-02-01', '120456781', '2016-5');

insert into aluga values ('FEUP', 'Central', '2016-01-01', '183426684', '2016-6');
insert into aluga values ('FEUP', 'Lab1', '2016-01-01', '173516575', '2016-7');

insert into aluga values ('Catolica', 'Central', '2016-01-01', '193336793', '2016-8');
insert into aluga values ('Catolica', 'Sala1', '2016-01-01', '103246782', '2016-9');
insert into aluga values ('Catolica', 'Sala2', '2016-01-01', '103246782', '2016-10');

insert into aluga values ('ISEL', 'Central', '2016-01-01', '103246782', '2016-11');
insert into aluga values ('ISEL', 'DEI', '2016-01-01', '103246782', '2016-12');
insert into aluga values ('ISEL', 'Central', '2016-02-01', '103246782', '2016-13');

insert into paga values ('2016-1', '2016-01-02 10:43:41', 'Cartão Crédito');
insert into paga values ('2016-2', '2016-01-02 11:33:25', 'Cartão Crédito');
insert into paga values ('2016-3', '2016-01-02 12:23:42', 'Paypal');
insert into paga values ('2016-4', '2016-01-01 08:43:23', 'Cartão Crédito');
insert into paga values ('2016-6', '2016-01-02 11:53:38', 'Cartão Crédito');
insert into paga values ('2016-7', '2016-01-03 08:33:03', 'Paypal');
insert into paga values ('2016-8', '2016-01-02 19:13:14', 'Cartão Crédito');
insert into paga values ('2016-9', '2016-01-01 18:23:46', 'Paypal');
insert into paga values ('2016-10', '2016-01-02 12:03:37', 'Cartão Crédito');
insert into paga values ('2016-11', '2016-01-01 13:23:25', 'Paypal');
insert into paga values ('2016-13', '2016-02-01 09:21:05', 'Cartão Crédito');

insert into estado values ('2016-1', '2016-01-01 02:53:21', 'Aceite');
insert into estado values ('2016-1', '2016-01-02 10:43:41', 'Paga');
insert into estado values ('2016-2', '2016-01-01 01:13:15', 'Aceite');
insert into estado values ('2016-2', '2016-01-02 11:33:25', 'Paga');
insert into estado values ('2016-3', '2016-01-01 11:03:22', 'Aceite');
insert into estado values ('2016-3', '2016-01-02 12:23:42', 'Paga');
insert into estado values ('2016-4', '2016-01-01 02:13:23', 'Aceite');
insert into estado values ('2016-4', '2016-01-01 08:43:23', 'Paga');
insert into estado values ('2016-5', '2016-01-01 12:23:21', 'Aceite');
insert into estado values ('2016-6', '2016-01-01 02:23:33', 'Aceite');
insert into estado values ('2016-6', '2016-01-02 11:53:38', 'Paga');
insert into estado values ('2016-7', '2016-01-02 01:23:03', 'Aceite');
insert into estado values ('2016-7', '2016-01-03 08:33:03', 'Paga');
insert into estado values ('2016-8', '2016-01-01 13:13:14', 'Aceite');
insert into estado values ('2016-8', '2016-01-02 19:13:14', 'Paga');
insert into estado values ('2016-9', '2016-01-01 10:13:46', 'Aceite');
insert into estado values ('2016-9', '2016-01-01 18:23:46', 'Paga');
insert into estado values ('2016-10', '2016-01-01 10:33:17', 'Aceite');
insert into estado values ('2016-10', '2016-01-02 12:03:37', 'Paga');
insert into estado values ('2016-11', '2016-01-01 11:03:15', 'Aceite');
insert into estado values ('2016-11', '2016-01-01 13:23:25', 'Paga');
insert into estado values ('2016-12', '2016-01-01 01:33:19', 'Aceite');
insert into estado values ('2016-13', '2016-02-01 07:15:27', 'Aceite');
insert into estado values ('2016-13', '2016-02-01 09:21:05', 'Paga');
