package ru.goodsReview.analyzer.algorithmTesting;

/**
 * Date: 05.02.12
 * Time: 21:18
 * Author:
 * Ilya Makeev
 * ilya.makeev@gmail.com
 */

import ru.goodsReview.analyzer.ExtractThesis;
import ru.goodsReview.analyzer.wordAnalyzer.MystemAnalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ThesisExtractionTestDocument {
    private static double successExtract = 0;
    private static double numAlgo = 0;
    private static double numHum = 0;

    //   build list of Products for human markup file
    static ArrayList<Product> buildHumanProductList(String filePath, String encoding) throws IOException {
        ArrayList<Product> ProductList = new ArrayList<Product>();

        FileInputStream fis = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader in = new BufferedReader(isr);

        ArrayList<Review> reviewsList = new ArrayList<Review>();
        ArrayList<Thesis> thesisList = new ArrayList<Thesis>();
        String reviewID = "-1";
        String productID = "-1";
        String s = in.readLine();
        
        boolean reviewOpen = false;
        StringBuffer sentenceBuff = new StringBuffer();
        
        while (s != null) {
            s = s.trim();

            if (s.contains("<product id=")) {
                if (!productID.equals("-1")) {
                    reviewsList.add(new Review(reviewID, (ArrayList<Thesis>) thesisList.clone()));
                    thesisList.clear();
                    reviewID = "-1";

                    ProductList.add(new Product(productID, (ArrayList<Review>) reviewsList.clone()));
                    reviewsList.clear();
                }
                String s1 = s.substring(0, s.indexOf("name="));
                productID = s.substring(s1.indexOf("\"") + 1, s1.lastIndexOf("\""));
            }

            if (!productID.equals("-1")) {
                if (s.contains("<review")) {
                    reviewOpen = true;
                    if (!reviewID.equals("-1")) {
                        reviewsList.add(new Review(reviewID, (ArrayList<Thesis>) thesisList.clone()));
                        thesisList.clear();
                    }
                    reviewID = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
                }

                if (s.contains("</review>")) {
                    reviewOpen = false;
                    if(sentenceBuff.length()!=0){
                        fillThesisList(sentenceBuff.toString(), thesisList);
                        sentenceBuff.delete(0, sentenceBuff.length());
                    }
                }

                if (reviewOpen) {
                    if (s.contains("##")) {

                        if (sentenceBuff.length() == 0) {
                            sentenceBuff.append(s.trim());
                        } else {
                            String sentBuff = sentenceBuff.toString();
                            fillThesisList(sentBuff, thesisList);

                            sentenceBuff.delete(0, sentenceBuff.length());
                            sentenceBuff.append(s.trim());
                        }
                    } else {
                        if(sentenceBuff.length()!=0) {
                            sentenceBuff.append(" " + s.trim());
                        }
                    }
                }

                s = in.readLine();
            }
        }

        reviewsList.add(new Review(reviewID, thesisList));
        ProductList.add(new Product(productID, reviewsList));

        return ProductList;
    }
    
    static void fillThesisList (String s, ArrayList<Thesis> thesisList){
        String t = s.substring(0, s.indexOf("##")).trim();
        String sentence = s.substring(s.indexOf("##")+2).trim();

        if (!t.equals("")) {
            if (t.contains(",")) {
                String[] arr = t.split(",");
                for (int i = 0; i < arr.length; i++) {
                    String s1 = arr[i];
                    s1 = s1.trim();
                    if(s1.contains("[")){
                        s1 = splitBracket(s1);
                        if (!s1.equals("")&&!thesisList.contains(s1)) {
                            thesisList.add(new Thesis(s1, sentence));
                        }
                    }

                }
            } else {
                t = splitBracket(t);
                if(!t.equals("")){
                    thesisList.add(new Thesis(t, sentence));
                }
            }
        }
    }

    static  String splitBracket(String s){
        if(s.contains("[")){
            s = s.substring(0, s.indexOf("["));
        }
        return  s;
    }


    //   build list of Products for algo markup file
    static ArrayList<Product> buildAlgoProductList(String filePath, String encoding) throws IOException, InterruptedException {
        ArrayList<Product> ProductList = new ArrayList<Product>();
        MystemAnalyzer mystemAnalyzer = new MystemAnalyzer();

        FileInputStream fis = new FileInputStream(filePath);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader in = new BufferedReader(isr);

        ArrayList<Review> reviewsList = new ArrayList<Review>();
        ArrayList<Thesis> thesisList = new ArrayList<Thesis>();
        String reviewID = "-1";
        String productID = "-1";
        String s = in.readLine();

        while (s != null) {
            s = s.trim();

            if (s.contains("<product id=")) {
                if (!productID.equals("-1")) {
                    reviewsList.add(new Review(reviewID, (ArrayList<Thesis>) thesisList.clone()));
                    thesisList.clear();
                    reviewID = "-1";

                    ProductList.add(new Product(productID, (ArrayList<Review>) reviewsList.clone()));
                    reviewsList.clear();
                }
                String s1 = s.substring(0, s.indexOf("name="));
                productID = s.substring(s1.indexOf("\"") + 1, s1.lastIndexOf("\""));

            }

            boolean reviewOpen = false;
            if (!productID.equals("-1")) {
                if (s.contains("<review")) {  
                    reviewOpen = true;
                    if (!reviewID.equals("-1")) {
                        reviewsList.add(new Review(reviewID, (ArrayList<Thesis>) thesisList.clone()));
                        thesisList.clear();
                    }
                    reviewID = s.substring(s.indexOf("\"") + 1, s.lastIndexOf("\""));
                }


                if (reviewOpen == true) {
                    StringBuffer sb = new StringBuffer();
                    while (reviewOpen == true && s != null) {
                        s = in.readLine();
                        if (s.contains("</review>")) {
                            reviewOpen = false;
                            String review =  sb.toString();
                          // System.out.println("##"+review);
                            ArrayList<String> tList = ExtractThesis.doExtraction(review, mystemAnalyzer);
                            for (String str : tList) {
                                int div = str.indexOf("##");
                                String token1 = str.substring(0, div).trim();
                                String token2 = str.substring(div + 2).trim();
                                thesisList.add(new Thesis(token1, token2));
                            }
                        } else {
                            sb.append(" "+s);
                        }

                    }
                }

                s = in.readLine();
            }
        }

        reviewsList.add(new Review(reviewID, thesisList));
        ProductList.add(new Product(productID, reviewsList));

        mystemAnalyzer.close();
        return ProductList;
    }

    
    // comparison of thesis for two products lists
    static void compare(ArrayList<Product> algoProThesis, ArrayList<Product> humProThesis, String filePath) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

        for (int i = 0; i < humProThesis.size(); i++) {
            Product humProduct = humProThesis.get(i);

            for (int j = 0; j < algoProThesis.size(); j++) {
                Product algoProduct = algoProThesis.get(j);

                if(algoProduct.getId().equals(humProduct.getId())){
                    /*
                    System.out.println("--------new product  "+algoProduct.getName());
                    for(Review review : algoProduct.getReviews()){
                        System.out.println("review_alg"+review.getReview());
                        for(String thesis : review.getThesis()){
                            System.out.println(thesis);
                        }
                    }
                    System.out.println("--------------------");
                    for(Review review : humProduct.getReviews()){
                        System.out.println("review_hum"+review.getReview());
                        for(String thesis : review.getThesis()){
                            System.out.println(thesis);
                        }
                    }   */
                    comparator(algoProduct, humProduct, out);
                    break;
                }
            }

        }
               /*
        if(humProThesis.size()!=algoProThesis.size()){
            System.out.println("файлы содержат разное число продуктов");
        }else{
            for (int i = 0; i < humProThesis.size(); i++) {
                Product humProduct = humProThesis.get(i);
                Product algoProduct = algoProThesis.get(i);
                if(editDist(algoProduct.getName(), humProduct.getName())<2){
                    comparator(algoProduct, humProduct, out);
                }else{
                    System.out.print("сравнение продуктов с разными именами: ");
                    System.out.println(algoProduct.getName()+" и "+humProduct.getName());
                }
            }
        }*/


        out.flush();
    }

    // comparison of thesis for two products
    static void comparator(Product algoProduct, Product humProduct, PrintWriter out) {
        out.println("<product id=\"" + algoProduct.getId() + "\">");
        if (algoProduct.getReviews().size() > 0 && !algoProduct.getReviews().get(0).getReview().equals("-1")) {
            if(algoProduct.getReviews().size()!=humProduct.getReviews().size()){
                System.out.print("сравнение продуктов с разным числом ревью: ");
                System.out.println("id = "+algoProduct.getId());
            } else{
                compareThesisLists(algoProduct.getReviews(), humProduct.getReviews(), out);
            }

        }

        out.println("</product>");
    }

    // comparison of thesis for two Review lists
    static void compareThesisLists(ArrayList<Review> algoReview, ArrayList<Review> humReview, PrintWriter out) {
        int editDist = 3;

        for (int k = 0; k < algoReview.size(); k++) {
            String reviewID = algoReview.get(k).getReview();

            if(!reviewID.equals("-1")){
                out.println("   <review id=\"" + reviewID + "\">");

                ArrayList<Thesis> algoThesis = algoReview.get(k).getThesis();
                ArrayList<Thesis> humThesis = humReview.get(k).getThesis();

                numAlgo += algoThesis.size();
                numHum += humThesis.size();

                for (int i = 0; i < humThesis.size(); i++) {
                    String hThesis = humThesis.get(i).getThesis();
                    String sentence = humThesis.get(i).getPart2();
                    // System.out.println("   "+hThesis+" "+sentence);
                    for (int j = 0; j < algoThesis.size(); j++) {
                        String alThesis = algoThesis.get(j).getThesis();
                        String opinion = algoThesis.get(j).getPart2();
                        // System.out.println(alThesis+" "+opinion);

                        if (editDist(hThesis, alThesis) < editDist) {
                            if (contains(sentence, alThesis) && contains(sentence, opinion)) {
                                out.println("      <OK>" + hThesis + "</OK>");
                               // System.out.println(alThesis+" "+opinion+" ## "+sentence);
                                successExtract++;
                                break;
                            }
                        }
                    }
                }


                for (int i = 0; i < algoThesis.size(); i++) {
                    boolean t = false;
                    String alThesis = algoThesis.get(i).getThesis();
                    String opinion = algoThesis.get(i).getPart2();
                    for (int j = 0; j < humThesis.size(); j++) {
                        String hThesis = humThesis.get(j).getThesis();
                        String sentence = humThesis.get(j).getPart2();
                        if (editDist(hThesis, alThesis) < editDist) {
                            if (contains(sentence, alThesis) && contains(sentence, opinion)) {
                                t = true;
                                break;
                            }
                        }
                    }
                    if (t == false) {
                        out.println("      <algo>" + alThesis + "</algo>");
                    }
                }

                for (int i = 0; i < humThesis.size(); i++) {
                    boolean t = false;
                    String hThesis = humThesis.get(i).getThesis();
                    String sentence = humThesis.get(i).getPart2();
                    
                    for (int j = 0; j < algoThesis.size(); j++) {
                        String alThesis = algoThesis.get(j).getThesis();
                        String opinion = algoThesis.get(j).getPart2();

                        if (editDist(hThesis, alThesis) < editDist) {
                            if (contains(sentence, alThesis) && contains(sentence, opinion)) {
                                t = true;
                                break;
                            }
                        }
                    }
                    if (t == false) {
                        out.println("      <hum>" + hThesis + "</hum>");
                    }
                }
                //out.println("   </review>");
            }
        }
    }
    
    static boolean contains(String sentence, String s){
        String [] a = sentence.split(" ");
        for (String str:a){
            str = str.trim();
            if(editDist(str, s)<3){
                return true;
            }
        }
        
        return false;
    }

    public static int editDist(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[] D1;
        int[] D2 = new int[n + 1];
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        for (int i = 0; i <= n; i++) {
            D2[i] = i;
        }
        for (int i = 1; i <= m; i++) {
            D1 = D2;
            D2 = new int[n + 1];
            for (int j = 0; j <= n; j++) {
                if (j == 0) {
                    D2[j] = i;
                } else {
                    int cost = (s1.charAt(i - 1) != s2.charAt(j - 1)) ? 1 : 0;
                    if (D2[j - 1] < D1[j] && D2[j - 1] < D1[j - 1] + cost) {
                        D2[j] = D2[j - 1] + 1;
                    } else if (D1[j] < D1[j - 1] + cost) {
                        D2[j] = D1[j] + 1;
                    } else {
                        D2[j] = D1[j - 1] + cost;
                    }
                }
            }
        }
        return D2[n];
    }



    public static void main(String[] args) throws IOException, InterruptedException {
       ArrayList<Product> algoProThesis = buildAlgoProductList("Notebooks.txt", "utf8");

        /*
        for (Product p:algoProThesis){
            System.out.println("Product_Id = "+p.getId());
            for (Review r:p.getReviews()){
                if(r.getReview()!="-1"){
                    System.out.println("        Review_Id = "+r.getReview());
                    for (Thesis t:r.getThesis()){
                        System.out.println("            "+t.getThesis()+" "+t.getSentence());
                    }
                }
            }

        }*/

       ArrayList<Product> humProThesis = buildHumanProductList("Notebooks_marked_ds.txt", "utf8");

        /*
        for (Product p:humProThesis){
            System.out.println("Product_Id = "+p.getId());
            for (Review r:p.getReviews()){
                if(r.getReview()!="-1"){
                    System.out.println("        Review_Id= "+r.getReview());
                    for (Thesis t:r.getThesis()){
                        System.out.println("            "+t.getThesis());
                    }
                }
            }
        }*/
     compare(algoProThesis, humProThesis, "result.txt");


        System.out.println("successExtract = "+successExtract);
        System.out.println("numAlgo = "+numAlgo);
        System.out.println("numHum = "+numHum);

        if(numAlgo!=0){
            System.out.println(successExtract/numAlgo);
        }
         if(numHum!=0){
             System.out.print(successExtract/numHum);
         }





    }
}

