package com.example.model;

import com.example.model.Order;
import com.example.security.model.User;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.text.Element;
import com.itextpdf.text.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;
// przydatne linki
// https://www.tutorialspoint.com/itext/itext_adding_list.htm
// https://itextpdf.com/en/resources/faq/getting-started/itext-7

public class Pdfcreator {

        Order general_info;
        public Pdfcreator(Order in){
            general_info = in;
        }

        public byte[] createPdf(User user) throws IOException
        {
            //Stream<Integer> stream2 = Arrays.stream(new Integer[]{});


//            OutputStream os = new OutputStream() {
//
//                public LinkedList<Integer> list = new LinkedList<>();
//
//                @Override
//                public void write(int b) throws IOException {
//                    list.add(b);
//                }
//            };

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            //inicjalizacja PDF WRITER
            //FileOutputStream fos = new FileOutputStream(DESTout);

            PdfWriter writer = new PdfWriter(buffer);

            //inicjalizacja PDF Document
            PdfDocument pdf = new PdfDocument(writer);

            //inicjalizacja Document
            Document document = new Document(pdf);

            //dodanie tekstu
            // addLogo(document);
            //Lokalizacja
            //Pobranie daty poczatkowej i koncowek
            DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            addParaToRight("Warszawa",
                    dateFormat.format(general_info.getDate()),
                    dateFormat.format(general_info.getEndDate()),
                            document);

            addPara(" ",document);
            createTable_1(document, user);
            addPara(" ",document);
            createTable_2(document);
            addPara(" ",document);
            createTableSum(document);
            addPara(" ",document);
            addPara(" ",document);
            addParaFooter(document);

            //zamkniecie pliku
            document.close();
            pdf.close();
            buffer.close();

            return buffer.toByteArray();
        }
        public void addLogo(Document doc) throws IOException {

            // Creating an ImageData object
            String imFile = "C:/Users/Zielu/Desktop/java_faktura_pdf/logo.png";
            ImageData data = ImageDataFactory.create(imFile);
            // Creating an Image object
            Image image = new Image(data);
            // Adding image to the document
            doc.add(image);
            System.out.println("Image added");
        }
        public void addParaToRight(String place,String dataW,String dataS,Document doc) throws IOException
        {
            // Creating a table
            float [] pointColumnWidths = {1000F};
            Table table = new Table(pointColumnWidths);

            // Adding cells to the table
            table.addCell( getCell("Faktura Var nr./Var FV/123/2019/02", TextAlignment.RIGHT));
            table.addCell( getCell("Orginał/Orginal", TextAlignment.RIGHT));
            table.addCell( getCell("Miejscowosc/Place: " + place, TextAlignment.RIGHT));
            table.addCell( getCell("Data wystawienia/Date of issue: " + dataW, TextAlignment.RIGHT));
            table.addCell( getCell("Data sprzedaży/Date of sell : " + dataS, TextAlignment.RIGHT));
            doc.add(table);
        }
        public Cell getCell(String text, TextAlignment alignment) {
            Cell cell = new Cell().add(new Paragraph(text));
            cell.setPadding(0);
            cell.setTextAlignment(alignment);
            cell.setBorder(Border.NO_BORDER);
            return cell;
        }
        public void createTable_1(Document doc, User user) throws IOException {

            // Creating a table
            float [] pointColumnWidths = {500F,500F};
            Table table = new Table(pointColumnWidths);

            //Lista przykladowa  nabywcy
            List list1 = new List();
            list1.setListSymbol("");
            list1.add(new ListItem(general_info.getPrincipal().getAddress()));
            list1.add(new ListItem(general_info.getPrincipal().getCompanyName()));
            list1.add(new ListItem(general_info.getPrincipal().getNip()));
            list1.add(new ListItem(general_info.getPrincipal().getPhoneNo()));

            //Lista przykladowa pracownika
            List list2 = new List();
            list2.setListSymbol("");
            list2.add(new ListItem(user.getUsername()));
            list2.add(new ListItem(user.getEmail()));
            list2.add(new ListItem(user.getFirstname()));
            list2.add(new ListItem(user.getLastname()));

            // Adding cells to the table
            table.addCell(new Cell().add("Nabywca (Purchaser)"));
            table.addCell(new Cell().add("Sprzedawca (Vendor)"));
            table.addCell(new Cell().add(list1));
            table.addCell(new Cell().add(list2));




            // Adding Table to document
            doc.add(table);
        }
        public void createTable_2(Document doc) throws IOException {

            // Creating a table
            float [] pointColumnWidths = {10F, 90F ,25F, 10F, 30F,70F,40F,70F,75F};
            Table table = new Table(pointColumnWidths);

            // Adding cells to the table
            table.addCell(new Cell().add("L.P"));
            table.addCell(new Cell().add("Nazwa towaru"));
            table.addCell(new Cell().add("Ilosc"));
            table.addCell(new Cell().add("j.m"));
            table.addCell(new Cell().add("Cena netto"));
            table.addCell(new Cell().add("Wartość netto"));
            table.addCell(new Cell().add("Stawka VAT"));
            table.addCell(new Cell().add("Kwota VAT"));
            table.addCell(new Cell().add("Wartość brutto"));
            ////////////////////////////////////
            for(int i=0;i<general_info.getUsedProductList().size();i++)
            {
                String cout = Integer.toString(i+1);
                table.addCell(new Cell().add(cout));
                table.addCell(new Cell().add("Szafa"));
                table.addCell(new Cell().add("2"));
                table.addCell(new Cell().add("szt."));
                table.addCell(new Cell().add("437,00"));
                table.addCell(new Cell().add("2400,00"));
                table.addCell(new Cell().add("23%"));
                table.addCell(new Cell().add("23,00"));
                table.addCell(new Cell().add("4400,00"));
            }

            ////////////////////////////////////
//        orderObject.put("id", order.getId());
//        orderObject.put("date", order.getDate());
//        orderObject.put("endDate", order.getEndDate());
//        orderObject.put("principal", principal);
//        orderObject.put("price", order.getPrice());
//        orderObject.put("departureDate", order.getDepartureDate());
//        orderObject.put("amountOfArticles", countArticles(order.getUsedProductList()));
//        orderObject.put("palletes", countPalletes(order.getUsedProductList()));
//        orderObject.put("productsCount", order.getUsedProductList().size());
            // Adding Table to document
            doc.add(table);
        }
        public void createTableSum(Document doc) throws IOException {

            // Creating a table
            float [] pointColumnWidths = {200F,500F};
            Table table = new Table(pointColumnWidths);

            //Lista przykladowa sprzedawcy
            List list1 = new List();
            list1.setListSymbol("");
            list1.add(new ListItem("Sposob zaplaty : "));
            list1.add(new ListItem("Termin zaplaty : "));
            list1.add(new ListItem("Numer rachunku : "));


            //Lista przykladowa nabywcy
            List list2 = new List();
            list2.setListSymbol("");
            list2.add(new ListItem("Transfer"));
            list2.add(new ListItem("2019-02-19"));
            list2.add(new ListItem("12 3243 1232 1222 0040 1243 4332 IMG BANK"));

            // Adding cells to the table
            table.addCell(getCell("Razem do zaplaty : ",TextAlignment.RIGHT));
            table.addCell(getCell(" 32000,89 zl", TextAlignment.LEFT));
            table.addCell(getCellList(list1, TextAlignment.RIGHT));
            table.addCell(getCellList(list2, TextAlignment.LEFT));


            // Adding Table to document
            doc.add(table);
        }
        public Cell getCellList(List list, TextAlignment alignment) {
            Cell cell = new Cell().add(list);
            cell.setPadding(0);
            cell.setTextAlignment(alignment);
            cell.setBorder(Border.NO_BORDER);
            return cell;
        }
        public void addParaFooter(Document doc) throws IOException
        {
            // Creating a table
            float [] pointColumnWidths = {500F,500F};
            Table table = new Table(pointColumnWidths);

            // Adding cells to the table
            table.addCell( getCell("....................................", TextAlignment.CENTER));
            table.addCell( getCell("....................................", TextAlignment.CENTER));
            table.addCell( getCell("Wystawil", TextAlignment.CENTER));
            table.addCell( getCell("Odebral", TextAlignment.CENTER));
            doc.add(table);
        }
        public void addPara(String string,Document doc) throws IOException
        {
            //dodaje paragraf
            doc.add(new Paragraph(string));
        }
    }
