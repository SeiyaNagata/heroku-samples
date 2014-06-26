CREATE TABLE hello_test(
  name     VARCHAR(100) NOT NULL,
  message  VARCHAR(100) NOT NULL
);

insert into hello_test (name, message) values('Jane', 'Hello!');
insert into hello_test (name, message) values('Taro', 'こんにちは!');
insert into hello_test (name, message) values('Miro', 'Guten Tag!');

