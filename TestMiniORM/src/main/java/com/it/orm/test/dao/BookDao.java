package com.it.orm.test.dao;

import com.it.orm.core.ORMConfig;
import com.it.orm.core.ORMSession;
import com.it.orm.test.entity.Book;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

public class BookDao {
    private ORMConfig config;
    @Before
    public void init(){
        config = new ORMConfig();
    }

    @Test
    public void testSave() throws SQLException, ClassNotFoundException, IllegalAccessException {
//        config = new ORMConfig();
        ORMSession session = config.buildORMSession();

        Book book = new Book();
        book.setId(11);
        book.setName("java");
        book.setAuthor("joan");
        book.setPrice(9.9);
        session.save(book);
        session.close();
    }

    @Test
    public void testFindOne() throws SQLException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException, InstantiationException {
        ORMSession session = config.buildORMSession();

        Book book = (Book)session.findOne(Book.class, 11);
        System.out.println(book.getName());
        session.close();
    }

    @Test
    public void testDelete() throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        ORMSession session = config.buildORMSession();
        Book book = new Book();
        book.setId(11);
        session.delete(book);
        session.close();
    }
}
