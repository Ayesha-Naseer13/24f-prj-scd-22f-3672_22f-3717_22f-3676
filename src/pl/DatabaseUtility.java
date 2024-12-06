package pl;

import bll.BusinessLayer;

public class DatabaseUtility {
    public static void main(String[] args) {
        try {
            String jarPath = "AlKhalil-2.1.21.jar"; 
            BusinessLayer businessLayer = new BusinessLayer();

            businessLayer.processWords();

            businessLayer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

