/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica4;

import java.util.Date;

/**
 *
 */
public class Book {
   private String  title;
   private String  author;
   private Date    publicationDate;
   private String  publicationPlace;
   private long    isbn;

    public Book(String newTitle ,String newAuthor, Date newDate, String newPlace, long newisbn){
        title = newTitle;
        author = newAuthor;
        publicationDate = newDate;
        publicationPlace = newPlace;
        isbn = newisbn;

    }

    public String getTitle(){
        return title;
    }

     public String getAuthor(){
        return author;
    }

     public Date getPublicationDate(){
         return publicationDate;
     }

      public String getPlace(){
        return publicationPlace;
    }


      public long getISBN(){
        return isbn;
    }

}
