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

## Yet, another solution for PDF data extracting: using OpenAI

One solution to extract information from PDF files is to use OpenAI's natural language processing capabilities to understand the content of the document. However, OpenAI is not able to work with PDF or image formats directly, so the first step is to convert the PDF to text while retaining the relative positions of the text items.

One way to achieve this is to use the PDFLayoutTextStripper library, which uses PDFBox to read through all text items in the PDF file and organize them in lines, keeping the relative positions the same as in the original PDF file. This is important because, for example, in an invoice's items table, if the amount is in the same column as the quantity, it will result in incorrect values when querying for the total amount and total quantity. Here is an example of the output from stripper:
```
                       
                                                                                                *PO-003847945*                                           
                                                                                                                                                         
                                                                                      Page.........................: 1    of    1                        
                                                                                                                                                         
                                                                                                                                                         
                                                                                                                                                         
                                                                                                                                                         
                                                                                                                                                         
                Address...........:     Aeeee  Consumer  Good  Co.(QSC)            Purchase       Order                                                  
                                        P.O.Box 1234                                                                                                     
                                        Dooo,                                      PO-003847945                                                          
                                        ABC                                       TL-00074                                   
                                                                                                                                                         
                Telephone........:                                                 USR\S.Morato         5/10/2020 3:40 PM                                
                Fax...................:                                                                                                                  
                                                                                                                                                         
                                                                                                                                                         
               100225                Aaaaaa  Eeeeee                                 Date...................................: 5/10/2020                   
                                                                                    Expected  DeliveryDate...:  5/10/2020                                
               Phone........:                                                       Attention Information                                                
               Fax.............:                                                                                                                         
               Vendor :    TL-00074                                                                                                                      
               AAAA BBBB CCCCCAAI    W.L.L.                                         Payment  Terms     Current month  plus  60  days                     
                                                                                                                                                         
                                                                                                                                                         
                                                                                                                         Discount                        
          Barcode           Item number     Description                  Quantity   Unit     Unit price       Amount                  Discount           
          5449000165336     304100          CRET ZERO 350ML  PET             5.00 PACK24          54.00        270.00         0.00         0.00          
                                                     350                                                                                                 
          5449000105394     300742          CEEOCE  EOE SOFT DRINKS                                                                                      
                                            1.25LTR                          5.00  PACK6          27.00        135.00         0.00         0.00          
                                                                                                                                                         
                                                1.25                                                                                                                        
(truncated...)
```

Once the PDF has been converted to text, the next step is to call the OpenAI API and pass the text along with queries such as "Extract fields: 'PO Number', 'Total Quantity'". The response will be in JSON format, and GSON can be used to parse it and extract the final results. This two-step process of converting the PDF to text and then using OpenAI's natural language processing capabilities can be an effective solution for extracting information from PDF files.

The query is as simple as follows with %s is replaced by PO text content:
```java
private static final String QUERY = """
    Want to extract fields: "PO Number", "Total Amount", "Total Quantity" and "Delivery Address".
    Return result in JSON format without any explanation. 
    The PO content is as follows:
    %s
    """;
```

Sample results from OpenAI:
```js
{
  "object": "text_completion",
  "model": "text-davinci-003",
  "choices": [
    {
      "text": "\\n{\\n  \\"PO Number\\": \\"PO-003847945\\",\\n  \\"Total Amount\\": \\"1,485.00\\",\\n  \\"Total Quantity\\": \\"100.00\\",\\n  \\"Delivery Address\\": \\"Peera Consumer Good Co.(QSC), P.O.Box 3371, Dohe, QAT\\"\\n}",
      "index": 0,
      "logprobs": null,
      "finish_reason": "stop"
    }
  ],
  // ... some more fields
}
```
