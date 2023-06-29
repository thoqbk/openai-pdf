package io.thoqbk.openaipdf;

import com.google.gson.Gson;
import io.github.jonathanlink.PDFLayoutTextStripper;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String OPENAI_API_KEY = "";
    private static final String SAMPLE_PDF_FILE = "io/thoqbk/openaipdf/sample-invoice.pdf";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/completions";
    private static final String QUERY = """
            Want to extract fields: "PO Number", "Total Amount" and "Delivery Address".
            Return result in JSON format without any explanation.
            The PO content is as follows:
            %s
            """;

    public static void main(String[] args) throws IOException {
        var pdf = extractPDFContent();
        var apiResponse = callOpenAIAPI(pdf);
        var results = extractAnswer(apiResponse);
        System.out.println(results);
    }

    private static String extractPDFContent() throws IOException {
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(SAMPLE_PDF_FILE);
        PDFParser pdfParser = new PDFParser(new RandomAccessBuffer(stream));
        pdfParser.parse();
        PDDocument pdDocument = new PDDocument(pdfParser.getDocument());
        PDFTextStripper pdfTextStripper = new PDFLayoutTextStripper();
        return pdfTextStripper.getText(pdDocument);
    }

    private static String callOpenAIAPI(String pdf) throws IOException {
        URL obj = new URL(OPENAI_API_URL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", String.format("Bearer %s", OPENAI_API_KEY));
        con.setDoOutput(true);

        Map<String, Object> json = new HashMap<>();
        json.put("model", "text-davinci-003");
        json.put("prompt", String.format(QUERY, pdf));
        json.put("temperature", 0.5);
        json.put("max_tokens", 2048);

        Gson gson = new Gson();
        String jsonString = gson.toJson(json);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(jsonString);
            wr.flush();
        }
        String retVal;
        try (InputStream inputStream = con.getInputStream()) {
            retVal = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
        return retVal;
    }

    private static Map<String, String> extractAnswer(String apiResponse) {
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(apiResponse, Map.class);
        if (!(map.get("choices") instanceof List)) {
            return Collections.emptyMap();
        }
        List<Map<String, Object>> choices = (List<Map<String, Object>>)map.get("choices");
        if (choices.isEmpty() || !choices.get(0).containsKey("text")) {
            return Collections.emptyMap();
        }
        String answer = (String)choices.get(0).get("text");
        return gson.fromJson(answer, Map.class);
    }
}
