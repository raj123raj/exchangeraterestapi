package com.exchangeRate.exchangeRate_Rest_API.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ExchangeRateControllerTest {
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ExchangeRateController()).build();
    }


    @Test
    public void testExchangeRateDetails() throws Exception {

        
        //expected
        String expectedData = null;
        StringBuilder responseData = new StringBuilder();
        JsonObject expectedJsonObject = null;
        JsonArray expectedJsonArray = null;
        String expectedINR = null,expectedGBP = null,expectedEuro = null;
        URL url = new URL("https://api.exchangerate-api.com/v4/latest/INR");
        //URL url = new URL("http://universities.hipolabs.com/search?name=anna&country=India");//working
        //URL url = new URL("http://api.zippopotam.us/IN/600028");  //This one will work
        //URL url = new URL("http://api.tvmaze.com/search/shows?q=mahabharata");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()))) {

            String line;

            while ((line = in.readLine()) != null) {
                responseData.append(line);
            }

            expectedJsonObject = new Gson().fromJson(responseData.toString(), JsonObject.class);
            //expectedJsonArray = new Gson().fromJson(responseData.toString(), JsonArray.class);
            expectedData = expectedJsonObject.get("rates").toString();
            expectedData = expectedData.replaceAll("^\"|\"$", "");
            StringTokenizer jsonTokenizer = new StringTokenizer(expectedData,",");
            String internalData[];
            String expectedExchangeRateOutput = null;
            
            while (jsonTokenizer.hasMoreTokens()) {  
            	expectedExchangeRateOutput = jsonTokenizer.nextToken();
            	internalData = StringUtils.split(expectedExchangeRateOutput,":");
            	if (internalData[0].substring(2,internalData[0].length()-1).equalsIgnoreCase("INR")) {
            		expectedINR = internalData[1];          		
            	}
            	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("GBP")) {
            		expectedGBP = internalData[1];
            	}
            	if (internalData[0].substring(1,internalData[0].length()-1).equalsIgnoreCase("EUR")) {
            		expectedEuro = internalData[1];
            	}
            }
            
            System.out.println(expectedINR + expectedGBP + expectedEuro);
        }

        //actual
        MvcResult result = mockMvc.perform(get("/getExchangeRateDetailsByCurrency?currency=INR"))
                .andReturn();
        String recievedResponse = result.getResponse().getContentAsString();
        JsonObject actualJsonObject = new Gson().fromJson(recievedResponse, JsonObject.class);
        String actualINR = actualJsonObject.get("INR").toString();
        actualINR = actualINR.replaceAll("^\"|\"$", "");
        String actualGBP = actualJsonObject.get("GBP").toString();
        actualGBP = actualGBP.replaceAll("^\"|\"$", "");
        String actualEuro = actualJsonObject.get("EUR").toString();
        actualEuro = actualEuro.replaceAll("^\"|\"$", "");
        assertEquals(expectedINR, actualINR);
        assertEquals(expectedGBP, actualGBP);
        assertEquals(expectedEuro, actualEuro);
    }

}
