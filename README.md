# Utilize OpenAI API to extract information from PDF

## Why it's hard to extract information from PDF?
PDF, or Portable Document Format, is a popular file format that is widely used for documents such as invoices, purchase orders, and other business documents. However, extracting information from PDFs can be a challenging task for developers.

One reason why it is difficult to extract information from PDFs is that the format is not structured. Unlike HTML, which has a specific format for tables and headers that developers can easily identify, PDFs do not have a consistent layout for information. This makes it harder for developers to know where to find the specific information they need.

Another reason why it is difficult to extract information from PDFs is that there is no standard layout for information. Each system generates invoices and purchase orders differently, so developers must often write custom code to extract information from each individual document. This can be a time-consuming and error-prone process.

Additionally, PDFs can contain both text and images, making it difficult for developers to programmatically extract information from the document. OCR (optical character recognition) can be used to extract text from images, but this adds complexity to the process and may result in errors if the OCR software is not accurate.

## Existing solutions
Existing solutions for extracting information from PDFs include:

- Using regex: to match patterns in text after converting the PDF to plain text. Examples include invoice2data and traprange-invoice. However, this method requires knowledge of the format of the data fields.

- AI-based cloud services: utilize machine learning to extract structured data from PDFs. Examples include [pdftables](https://pdftables.com/) and [docparser](https://docparser.com/), but these are not open-source friendly.

## Yet, another PDF data extracting solution: using OpenAI API

One solution to extract information from PDF files is to use OpenAI's natural language processing capabilities to understand the content of the document. However, OpenAI is not able to work with PDF or image formats directly, so the first step is to convert the PDF to text while retaining the relative positions of the text items.

One way to achieve this is to use the PDFLayoutTextStripper library, which uses PDFBox to read through all text items in the PDF file and organize them in lines, keeping the relative positions the same as in the original PDF file. This is important because, for example, in an invoice's items table, if the amount is in the same column as the quantity, it will result in incorrect values when querying for the total amount and total quantity.

Once the PDF has been converted to text, the next step is to call the OpenAI API and pass the text along with queries such as "Extract fields: 'PO Number', 'Total Quantity'". The response will be in JSON format, and GSON can be used to parse it and extract the final results. This two-step process of converting the PDF to text and then using OpenAI's natural language processing capabilities can be an effective solution for extracting information from PDF files.
