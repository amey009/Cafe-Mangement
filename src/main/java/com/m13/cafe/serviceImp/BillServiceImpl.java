package com.m13.cafe.serviceImp;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;
import com.m13.cafe.constants.CafeConstants;
import com.m13.cafe.dao.BillDAO;
import com.m13.cafe.jwt.JwtFilter;
import com.m13.cafe.model.Bill;
import com.m13.cafe.model.Category;
import com.m13.cafe.service.BillService;
import com.m13.cafe.utils.CafeUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.graphics.color.PDCIEBasedColorSpace;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.lowagie.text.FontFactory.getFont;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

    @Autowired
    BillDAO billDAO;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("inside generate reposrt");
        try{
            String fileName;
            //it will check if all the data is present in requestMap then it goes inside the if block
            if (vaildateRequestMap(requestMap)){
                if (requestMap.containsKey("isGenerate") && !(Boolean)requestMap.get("isGenerate")){
                    fileName= (String) requestMap.get("uuid");
                }
                else{
                    fileName = CafeUtils.getUUId();
                    requestMap.put("uuid",fileName);
                    insertBill(requestMap);
                }

                String data= "Name: "+requestMap.get("name") + "\n" +
                        "Contact Number: " + requestMap.get("contactNumber") + "\n" +
                        "Email: " +requestMap.get("email") + "\n" +
                        "Payment Method: " + requestMap.get("paymentMethod");


                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(CafeConstants.STORE_LOCATION + "\\" + fileName+ ".pdf"));

                document.open();
                setRecatngleInPdf(document);


                Paragraph chunk=new Paragraph("Cafe Manegement System...!",getFontss("Header"));
                chunk.setAlignment(Element.ALIGN_CENTER);
                document.add(chunk);

                Paragraph paragraph=new Paragraph(data + "\n \n",getFontss("Data"));
                paragraph.setAlignment(Element.ALIGN_LEFT);
                document.add(paragraph);

                PdfPTable pdfTable= new PdfPTable(5);
                pdfTable.setWidthPercentage(100);
                addTableHeader(pdfTable);

                JSONArray jsonArray=CafeUtils.getJSONArrayFromString((String) requestMap.get("productDetails"));

                for (int i = 0; i < jsonArray.length(); i++) {
                    addRow(pdfTable,CafeUtils.getMapFromJSON(jsonArray.getString(i)));
                }
                document.add(pdfTable);

                Paragraph footer=new Paragraph("Total : "+ requestMap.get("totalAmount") + "\n" +
                        "Thank you for visiting", getFontss("data"));
                document.add(footer);

                document.close();
                return new ResponseEntity<>("{\"uuid\":\""+fileName  + "\"}", HttpStatus.OK);
            }
            //otherwise it returns invalid data
            return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void addRow(PdfPTable pdfTable, Map<String, Object> data) {
        log.info("insideadd rows");
        pdfTable.addCell((String) data.get("name"));
        pdfTable.addCell((String) data.get("category"));
        pdfTable.addCell((String) data.get("quantity"));
        pdfTable.addCell(Double.toString((Double) data.get("price")));
        pdfTable.addCell(Double.toString((Double) data.get("total")));
    }

    private void addTableHeader(PdfPTable pdfTable) {
        log.info("onside addTablehEADER");
        Stream.of("Name", "Category","Quantity","Price","Sub Toatal")
                .forEach(columnTitle->{
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(Color.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    header.setBackgroundColor(Color.YELLOW);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    pdfTable.addCell(header);
                });
        
    }

    private Font getFontss(String type) {
        log.info("inside getFontss");
        switch (type){
            case "Header":
                Font headerFont= FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,Color.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;


            case "Data":
                Font dataFont= FontFactory.getFont(FontFactory.TIMES_ROMAN,11,Color.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;

            default:
                return new Font();

        }
    }

    private void setRecatngleInPdf(Document document) throws DocumentException {
        log.info("inside setRecatngleInPdf()");

        Rectangle rectangle = new Rectangle(577, 825, 18, 15);

        // Enable all sides of the border
        rectangle.enableBorderSide(Rectangle.LEFT);
        rectangle.enableBorderSide(Rectangle.RIGHT);
        rectangle.enableBorderSide(Rectangle.TOP);
        rectangle.enableBorderSide(Rectangle.BOTTOM);

        // You can optionally set border width or color using RGB values instead of BaseColor
        rectangle.setBorderWidth(2f); // Optional: make the border thicker
        rectangle.setBorderColor(Color.BLACK); // Optional: requires java.awt.Color

        document.add(rectangle); // You must add it to the document
    }



    //this all the keys which pass into the API
    private boolean vaildateRequestMap(Map<String, Object> requestMap) {
        return requestMap.containsKey("name") &&
                requestMap.containsKey("contactNumber") &&
                requestMap.containsKey("email") &&
                requestMap.containsKey("paymentMethod") &&
                requestMap.containsKey("productDetails") &&
                requestMap.containsKey("totalAmount");
    }

    private void insertBill(Map<String, Object> requestMap) {
        try{
            Bill bill=new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
//            bill.setTotalAmount(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setTotalAmount(Integer.parseInt(requestMap.get("totalAmount").toString()));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUserName());
            billDAO.save(bill);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseEntity<List<Bill>> getBill() {
        List<Bill> list=new ArrayList<>();
        //first it will check is that user is admin then it will return all the bills in decesending order
        if (jwtFilter.isAdmin()) {
            list=billDAO.getAllBills();
        }
        //if the user is normal user then we get the bill by there name
        else {
            list=billDAO.getBillByUserName(jwtFilter.getCurrentUserName());
        }

        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("inside getPdf: requestMap{}",requestMap);
        try {
            byte[] byteArray=new byte[0];
            if (!requestMap.containsKey("uuid") && vaildateRequestMap(requestMap))
                return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
            String filePath=CafeConstants.STORE_LOCATION+"\\"+(String) requestMap.get("uuid")+ ".pdf";

            if (CafeUtils.isFileExist(filePath)){
                byteArray = getByteArray(filePath);
                return new ResponseEntity<>(byteArray,HttpStatus.OK);
            }
            else {
                requestMap.put("isGenerate",false);
                generateReport(requestMap);
                byteArray=getByteArray(filePath);

                return new ResponseEntity<>(byteArray,HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Long id) {
        log.info("inside deleteBill");
        try {

            Optional optional=billDAO.findById(id);
            if (!optional.isEmpty()){
                 billDAO.deleteById(id);
                return CafeUtils.getResponseEntity("deleted Sucessfully",HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Bill id does not exists",HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(String filePath) throws IOException {
        File initialFile=new File(filePath);
        InputStream targetStream=new FileInputStream(initialFile);
        byte[] byteArray= IOUtils.toByteArray(targetStream);
        targetStream.close();
        return byteArray;
    }
}
