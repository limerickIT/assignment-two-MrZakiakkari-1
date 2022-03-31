package com.sd4.service;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.sd4.model.Beer;
import com.sd4.model.Brewery;
import com.sd4.model.Category;
import com.sd4.model.Style;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.core.io.ClassPathResource;

public class BeerPdfPrinter
{
	Beer beer;
	Brewery brewery;
	Category category;
	Style style;
	public BeerPdfPrinter(Beer beer, Brewery brewery, Category category, Style style)
	{
		this.beer = beer;
		this.brewery = brewery;
		this.category = category;
		this.style = style;
	}

	private static final Font COURIER = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
	private static final Font COURIER_SMALL = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
	private static final Font COURIER_SMALL_FOOTER = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

	public File generatePdfReport() throws DocumentException, IOException
	{
		File file = File.createTempFile("report", ".pdf");
		try ( FileOutputStream fileOutputStream = new FileOutputStream(file))
		{
			Document document = new Document();
			PdfWriter.getInstance(document, fileOutputStream);
			document.open();
			addLogo(document);
			addDocTitle(document);
			//createTable(document, 8);
			document.close();
			return file;
		}
	}

	private void addLogo(Document document) throws BadElementException, DocumentException, IOException
	{
		String path = "static/assets/images/large/" + beer.getImage();

		System.out.println(path);

		ClassPathResource resource = new ClassPathResource(path);

		System.out.println(resource.getURL().getPath());

		Image img = Image.getInstance(resource.getURL());
		img.scalePercent(50, 50);
		img.setAlignment(Element.ALIGN_RIGHT);
		document.add(img);
	}

	private void addDocTitle(Document document) throws DocumentException
	{
		Paragraph p1 = new Paragraph();
		leaveEmptyLine(p1, 1);
		p1.add(new Paragraph(beer.getName(), COURIER));
		p1.add(new Paragraph(beer.getAbv().toString(), COURIER));
		p1.add(new Paragraph(beer.getSell_price().toString(), COURIER));
		p1.add(new Paragraph(category.getCat_name(), COURIER));
		p1.add(new Paragraph(beer.getDescription(), COURIER));
		p1.add(new Paragraph(brewery.getName(), COURIER));
		p1.add(new Paragraph(new Anchor(brewery.getWebsite(), COURIER)));
		if (style != null)
		{
			p1.add(new Paragraph(style.getStyle_name(), COURIER));
		}
		p1.setAlignment(Element.ALIGN_CENTER);

		document.add(p1);

	}

	private void createTable(Document document, int noOfColumns) throws DocumentException
	{
		Paragraph paragraph = new Paragraph();
		leaveEmptyLine(paragraph, 3);
		document.add(paragraph);

		PdfPTable table = new PdfPTable(noOfColumns);

		for (int i = 0; i < noOfColumns; i++)
		{
			PdfPCell cell = new PdfPCell(new Phrase(i + ""));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBackgroundColor(BaseColor.CYAN);
			table.addCell(cell);
		}

		table.setHeaderRows(1);
		document.add(table);
	}

	private static void leaveEmptyLine(Paragraph paragraph, int number)
	{
		for (int i = 0; i < number; i++)
		{
			paragraph.add(new Paragraph(" "));
		}
	}

	private String getPdfNameWithDate()
	{
		String localDateString = LocalDateTime.now().toString();
		return "Report" + localDateString;
	}
}
