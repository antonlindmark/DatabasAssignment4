import com.mongodb.*;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Assignment4{
    public static MongoCollection<Document> carCollection;
    public static MongoCollection<Document> salesCollection;
    public static MongoCollection<Document> legalinfoCollection;
    public static Document carDoc;
    public static Document salesDoc;



    public static void UIAddCar(){
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter Car info \t");
        System.out.println("Enter regnr : \t");
        String regnr  = scan.nextLine();

        System.out.println("Enter color : \t");
        String color  = scan.nextLine();

        System.out.println("Enter manufacturer : \t");
        String manufacturer  = scan.nextLine();

        System.out.println("Enter condition : \t");
        String condition  = scan.nextLine();

        System.out.println("Enter model : \t");
        String model  = scan.nextLine();

        System.out.println("Enter mileage : \t");
        int milage = scan.nextInt();

        System.out.println("Enter year : \t");
        int year = scan.nextInt();

        System.out.println("Enter horsepower : \t");
        int horsepower = scan.nextInt();

        String statussold  = "null";
        insertCar(regnr,color,manufacturer,milage,year,horsepower,condition,model,statussold);
    }


    public static void UIAddLegalInfo() {
        Scanner scan1 = new Scanner(System.in);
        System.out.println("Enter Legal info \t");
        System.out.println("Enter regnr : \t");
        String regnr = scan1.nextLine();

        System.out.println("Enter previous owner : \t");
        String prevowner = scan1.nextLine();

        System.out.println("Enter price : \t");
        int price = scan1.nextInt();

        insertLegalInfo(regnr,price,prevowner);
    }

    public static void UIAddSales(){
        Scanner scan2 = new Scanner(System.in);
        System.out.println("Enter Sales info \t");
        System.out.println("Enter regnr : \t");
        String regnr = scan2.nextLine();

        System.out.println("Enter finalprice : \t");
        int finalprice = scan2.nextInt();
        scan2.nextLine();

        System.out.println("Enter owner : \t");
        String owner = scan2.nextLine();

        insertSales(regnr,owner,finalprice);
    }

    public static void main(String [] args ){
        System.out.println("hejjej");

        MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase database = mongoClient.getDatabase("Assignment4");

        // Creates collection if they dont exist.

        carCollection = database.getCollection("car");
        salesCollection = database.getCollection("sales");
        legalinfoCollection = database.getCollection("legalinfo");



        Scanner scan = new Scanner(System.in);
        Scanner scan2 = new Scanner(System.in);
        while(true){

            System.out.println("Press 1 to insert a car, press 2 to do a query,\npress 3 to enter sold car. ");

            int choice = scan.nextInt();
            if(choice==1){
                System.out.println("Enter info to insert in database");
                UIAddCar();
                UIAddLegalInfo();
            }
            else if(choice==2){
                System.out.println("Choose which query you want to do 1-6 \n"+
                        "1 :Search for previous owner \n"+
                        "2 :Search for all info about a car \n"+
                        "3 :Search for price and finalprice \n" +
                        "4 :Search for cars with specifik horsepower and model\n" +
                        "5 :Search for cars within a price range \n"+
                        "6 :Show all sold cars \n"+
                        "Enter desired number to execute query ! ");
                int secondChoice = scan2.nextInt();
                if(secondChoice ==1){
                    System.out.println("Enter regNr : ");
                    String regnr = scan2.next();
                    getPrevownerByRegnr(regnr);
                }
                else if(secondChoice==2){
                    System.out.println("Enter regNr : ");
                    String regnr = scan2.next();
                    getAllCarInfoByRegnr(regnr);
                }
                else if(secondChoice==3){
                    System.out.println("Enter regNr : ");
                    String regnr = scan2.next();
                    getPriceAndFinalPriceByRegnr(regnr);
                }
                else if(secondChoice==4){
                    System.out.println("Enter horsepower : ");
                    int hp = scan2.nextInt();
                    System.out.println("Enter model : ");
                    String model = scan2.next();

                    getAllInfoByHPandModel(hp,model);
                }
                else if(secondChoice==5){
                    System.out.println("Enter maxPrice : ");
                    int maxp = scan2.nextInt();
                    System.out.println("Enter minPrice : ");
                    int minp = scan2.nextInt();
                    getCarsWithinPriceRange(maxp,minp);
                }
                else if(secondChoice==6){
                    //showAllSoldCars();
                }

            }
            else if(choice==3){
                System.out.println("Enter sold car info");
                System.out.println("Enter regNr : ");
                String regnr = scan2.next();
                UIAddSales();
                // updateStatusSold(regnr);

            }
        }
    }

    public static void insertCar(String regnr, String color, String manufacturer, int mileage, int year, int horsepower, String condition, String model, String statussold){
        carDoc = new Document("regnr",regnr)
                .append("color",color)
                .append("manufacturer",manufacturer)
                .append("mileage",mileage)
                .append("year",year)
                .append("horsepower",horsepower)
                .append("condition",condition)
                .append("model",model)
                .append("statussold","null");
        carCollection.insertOne(carDoc);
    }
    public static void insertSales(String regnr, String owner, int finalprice){

        carCollection.updateOne(
                eq("regnr", regnr),
                combine(set("statussold","sold")));
        carDoc = carCollection.find(eq("regnr",regnr)).first();
        salesDoc = new Document("regnr",regnr)
                .append("owner",owner)
                .append("finalprice",finalprice)
                .append("car",carDoc);
        salesCollection.insertOne(salesDoc);
    }
    public static void insertLegalInfo(String regnr, int price, String prevowner){
        Document doc = new Document("regnr",regnr)
                .append("price",price)
                .append("prevowner",prevowner)
                .append("car",carDoc);
        legalinfoCollection.insertOne(doc);
    }
    public static void getPrevownerByRegnr(String regnr){

        Document doc = legalinfoCollection.find(eq("regnr",regnr)).first();
        System.out.println(doc.getString("prevowner"));
    }
    public static void getAllCarInfoByRegnr(String regnr){
        Document doc = carCollection.find(eq("regnr",regnr)).first();
        System.out.println(doc.toJson());

    }
    public static void getPriceAndFinalPriceByRegnr(String regnr){
        Document doc = salesCollection.find(eq("regnr",regnr)).first();
        Document doc2 = legalinfoCollection.find(eq("regnr",regnr)).first();
        System.out.println(doc.getInteger("finalprice"));
        System.out.println(doc2.getInteger("price"));
    }
    public static void getAllInfoByHPandModel(int hp,String model){
        Block<Document> printBlock = new Block<Document>() {
            public void apply(Document document) {
                System.out.println(document.toJson());
            }
        };
        carCollection.find(and(eq("horsepower",hp), eq("model",model))).forEach(printBlock);
    }
    public static void getCarsWithinPriceRange(int lowprice, int maxprice){
        Block<Document> printBlock = new Block<Document>() {
            public void apply(Document document) {
                System.out.println(document.toJson());
            }
        };
        legalinfoCollection.find(and(gt("price",lowprice),lt("price",maxprice))).forEach(printBlock);
    }

    public static void updateStatusSold(String regnr){
        //Updates statussold
        carCollection.updateOne(
                eq("regnr", regnr),
                combine(set("statussold","sold")));

        //Add updated car to sales
        // Document doc = salesCollection.find(eq("regnr",regnr)).first();
        Document docCar = carCollection.find(eq("regnr",regnr)).first();



    }












}