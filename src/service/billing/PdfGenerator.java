package service.billing;

import model.billing.Bill;
import model.billing.BillItem;

import java.io.File;
import java.io.FileOutputStream;

import java.time.format.DateTimeFormatter;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfGenerator {

    private static final String OUTPUT_DIR = "D:\\Project\\bill";

    public static void generateBillPdf(Bill bill)
    {
        try 
        {
            // Ensure directory exists
            File directory = new File(OUTPUT_DIR);
            
            if (!directory.exists()) 
            {
                directory.mkdirs();
            }

            // Construct full path with filename: invoices/bill123.pdf
            String filePath = OUTPUT_DIR + File.separator + "bill" + bill.getId() + ".pdf";

            Document document = new Document();
            
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            
            document.open();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            document.add(new Paragraph("🐾 Pet Shop Invoice 🐾"));
            document.add(new Paragraph("Transaction Time: " + bill.getTransactionTime().format(formatter)));
            document.add(new Paragraph("Staff ID: " + bill.getStaffId()));
            document.add(new Paragraph("Customer ID: " + bill.getCustomerId()));
            document.add(new Paragraph("Payment Method: " + bill.getPaymentMethod()));
            document.add(new Paragraph("\nItems Purchased:"));

            for (BillItem item : bill.getItems()) 
            {
                String line = String.format
                (
                        "[%s] %s x%d - $%.2f",
                        item.getItemType(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getTotal()
                );                
                Paragraph p = new Paragraph(line);
                
                p.setAlignment(Element.ALIGN_LEFT);
                
                document.add(p);
            }

            document.add(new Paragraph("\nTotal Amount: $" + bill.getTotalAmount()));
            
            document.close();

            System.out.println("✅ Invoice PDF generated at: " + filePath);

        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
