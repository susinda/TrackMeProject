package com.example.susinda.carapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;


/**
 * Created by susinda on 6/15/15.
 */
public class JSONClient {


    public static String generateAddOrderMessage(Configuration order) throws JSONException {

        JSONObject orderJSON = new JSONObject();
        orderJSON.put("Model", order.getModel());
        orderJSON.put("Engine", order.getEngine());
        orderJSON.put("Color", order.getColor());
        orderJSON.put("Interior", order.getInterior());
        orderJSON.put("Exterior", order.getExterior());

        JSONObject rootJSON = new JSONObject();
        rootJSON.put("Configuration", orderJSON);
        return rootJSON.toString();
    }

    public static String generateUpdateOrderMessage(Configuration order, String id) throws JSONException {

        JSONObject updateJSON = new JSONObject();
        updateJSON.put("ID", id);

        JSONObject configJSON = new JSONObject();
        configJSON.put("Model", order.getModel());
        configJSON.put("Engine", order.getEngine());
        configJSON.put("Color", order.getColor());
        configJSON.put("Interior", order.getInterior());
        configJSON.put("Exterior", order.getExterior());
        updateJSON.put("Configuration", configJSON);

        JSONObject rootJSON = new JSONObject();
        rootJSON.put("update", updateJSON);
        return rootJSON.toString();
    }

    public static String generateDeleteOrderMessage(String id) throws JSONException {

        JSONObject orderJSON = new JSONObject();
        orderJSON.put("ID", id);
        JSONObject rootJSON = new JSONObject();
        rootJSON.put("delete", orderJSON);
        return rootJSON.toString();
    }

    public static String generateBuyOrderMessage(String model, String qty) throws JSONException {

        JSONObject orderJSON = new JSONObject();
        orderJSON.put("Model", model);
        orderJSON.put("Quantity", qty);

        JSONObject rootJSON = new JSONObject();
        rootJSON.put("Configuration", orderJSON);
        return rootJSON.toString();
    }



    public static Configuration getConfiguration(String orderJson){

        if (orderJson != null && !orderJson.isEmpty()) {
            Configuration order = new Configuration();

            try {
                JSONObject jsonObject = new JSONObject(orderJson);
                JSONObject configObject = (JSONObject) jsonObject.get("Configuration");
                if (configObject != null) {
                    order.setModel((String)configObject.get("Model"));
                    order.setEngine((String)configObject.get("Engine"));
                    order.setColor((String)configObject.get("Color"));
                    order.setInterior((String)configObject.get("Interior"));
                    order.setExterior((String)configObject.get("Exterior"));
                    return order;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static String getPrice(String orderJson){
        //placeOrderRespone":{"getQuotation":{"price"
        try {
            JSONObject jsonObject = new JSONObject(orderJson);
            JSONObject orderObject = (JSONObject) jsonObject.get("placeOrderRespone");
            JSONObject quotationObject = (JSONObject) orderObject.get("getQuotation");
            if (quotationObject != null) {
                String price = quotationObject.get("price").toString();
                return price;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Populates Token object using folloing JSON String
     * {
     * "token_type": "bearer",
     * "expires_in": 3600000,
     * "refresh_token": "f43de118a489d56c3b3b7ba77a1549e",
     * "access_token": "269becaec9b8b292906b3f9e69b5a9"
     }
     * @param accessTokenJson
     * @return
     */
    public static Token getAccessToken(String accessTokenJson){

        Token token = new Token();
        try {

            JSONObject jsonObject = new JSONObject(accessTokenJson);
            token.setAccessToken((String)jsonObject.get("access_token"));
            long expiresIn = ((Long)jsonObject.get("expires_in")).intValue();
            token.setExpiresIn(expiresIn);
            token.setRefreshToken((String)jsonObject.get("refresh_token"));
            token.setTokenType((String)jsonObject.get("token_type"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;

    }
}





