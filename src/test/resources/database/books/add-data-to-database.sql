insert into categories (id, name, description) values (1, 'Fiction', 'd1');
insert into categories (id, name, description) values (2, 'Action', 'd2');
insert into categories (id, name, description) values (3, 'Thriller', 'd3');
insert into books (id, title, author, isbn, price, description, cover_image) values (1, 'Title1', 'Author1', '978-1-2345-6789-7', 10.9, 'd1', 'cov1');
insert into books (id, title, author, isbn, price, description, cover_image) values (2, 'Title2', 'Author2', '978-2-266-11156-0', 20.9, 'd1', 'cov2');
insert into books (id, title, author, isbn, price, description, cover_image) values (3, 'Title3', 'Author3', '979-0-2600-0043-8', 30.9, 'd2', 'cov3');
insert into books (id, title, author, isbn, price, description, cover_image) values (4, 'Title4', 'Author4', '978-5-699-54574-2', 40.9, 'd4', 'cov4');
insert into books (id, title, author, isbn, price, description, cover_image) values (5, 'Title5', 'Author5', '978-966-2046-92-2', 50.9, 'd5', 'cov5');
insert into books_categories (book_id, category_id) values (1, 1);
insert into books_categories (book_id, category_id) values (2, 1);
insert into books_categories (book_id, category_id) values (3, 1);
insert into books_categories (book_id, category_id) values (4, 2);
insert into books_categories (book_id, category_id) values (5, 2);
