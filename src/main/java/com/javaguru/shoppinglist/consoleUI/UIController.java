package com.javaguru.shoppinglist.consoleUI;

import com.javaguru.shoppinglist.domain.product.Product;
import com.javaguru.shoppinglist.domain.product.ProductCategory;
import com.javaguru.shoppinglist.domain.product.request.CreateRequest;
import com.javaguru.shoppinglist.domain.product.request.FindRequest;
import com.javaguru.shoppinglist.domain.product.request.UpdateRequest;
import com.javaguru.shoppinglist.domain.product.response.CreateResponse;
import com.javaguru.shoppinglist.domain.product.response.FindResponse;
import com.javaguru.shoppinglist.domain.product.response.UpdateResponse;
import com.javaguru.shoppinglist.repository.DBErrors;
import com.javaguru.shoppinglist.service.Service;
import com.javaguru.shoppinglist.service.validation.ValidationErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Scanner;

@Component
public class UIController {
    private final static int MIN_VALUE = 0;
    private final static int MAX_VALUE = 8;
    private Service productService;

    @Autowired
    public UIController(Service productService) {
        this.productService = productService;
    }

    public void startUI() {
        BufferedReader keyPress = reader();
        boolean exitMainMenu = false;
        boolean exitSubMenu;

        while (exitMainMenu == false) {
            readMainMenuCommands();
            System.out.println("Type command: ");
            try {
                String commandForMainMenu = keyPress.readLine().toLowerCase();

                switch (commandForMainMenu) {
                    case "0":
                    case "exit":
                        exitMainMenu = true;
                        break;
                    case "1":
                    case "add":
                        addNewProduct();
                        break;
                    case "2":
                    case "get":
                        exitSubMenu = false;

                        while (exitSubMenu == false) {
                            readCommandsForPrint();
                            System.out.println("Get product(s): ");
                            String commandForPrint = keyPress.readLine().toLowerCase();

                            switch (commandForPrint) {
                                case "0":
                                case "up":
                                    exitSubMenu = true;
                                    break;
                                case "1":
                                case "getall":
                                    printProductsList(productService.getAllDatabase());
                                    break;
                                case "2":
                                case "byid":
                                    try {
                                        findProductByID();
                                    } catch (NumberFormatException e) {
                                        System.out.println("ID can't be non numeric");
                                    }
                                    break;
                                case "3":
                                case "bycat":
                                    try {
                                        findProductByCategory();
                                    } catch (IllegalArgumentException e) {
                                        System.out.println("Category can be only from " + MIN_VALUE + " to " + MAX_VALUE);
                                    }
                                    break;
                                case "?":
                                case "help":
                                    getHelp("get");
                                    break;
                                default:
                                    System.out.println("This command isn't support. Please read help documentation. (For help type \"?\", or \"help\")");
                            }
                        }
                        break;
                    case "3":
                    case "update":
                        exitSubMenu = false;

                        while (exitSubMenu == false) {
                            readCommandsForUpdate();
                            System.out.println("Update product(s) by:");
                            String commandForUpdate = keyPress.readLine().toLowerCase();

                            switch (commandForUpdate) {
                                case "0":
                                case "up":
                                    exitSubMenu = true;
                                    break;
                                case "1":
                                case "byid":
                                    updateProductByID();
                                    break;
                                case "?":
                                case "help":
                                    getHelp("update");
                                    break;
                            }
                        }
                        break;
                    case "4":
                    case "delete":
                        deleteProduct();
                        break;
                    case "?":
                    case "help":
                        getHelp("main");
                        break;
                    default:
                        System.out.println("This command isn't support. Please read help documentation. (For help type \"?\", or \"help\")");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readMainMenuCommands() {
        try {
            File commands = new File("src/main/java/com/javaguru/shoppinglist/consoleUI/MainMenuCommands");
            Scanner reader = new Scanner(commands);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                System.out.println(data);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }

    private void readCommandsForPrint() {
        try
        {
            File commands = new File("src/main/java/com/javaguru/shoppinglist/consoleUI/GetMenuCommands");
            Scanner reader = new Scanner(commands);
            while (reader.hasNextLine())
            {
                String data = reader.nextLine();
                System.out.println(data);
            }
            reader.close();
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
        }
    }

    private void readCommandsForUpdate() {
        try
        {
            File commands = new File("src/main/java/com/javaguru/shoppinglist/consoleUI/UpdateMenuCommands");
            Scanner reader = new Scanner(commands);
            while (reader.hasNextLine())
            {
                String data = reader.nextLine();
                System.out.println(data);
            }
            reader.close();
        } catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
        }
    }

    private void getHelp(String menu) {
        switch (menu) {
            case "main":
                System.out.println("(0), or exit - to exit from the program;");
                System.out.println("(1), or add - to add new product;");
                System.out.println("(2), or get - to get information from the databese;");
                System.out.println("(3), or update - to update the database information;");
                System.out.println("(4), or delete - to delete a product by ID;");
                System.out.println("(5), or generate - to fill the database test of the products;");
                System.out.println("(6), or drop - to drop all from the database;");
                break;
            case "get":
                System.out.println("(0), or up - back to the main menu;");
                System.out.println("(1), or all - get all information products in the database;");
                System.out.println("(2), or byid - get information about product in the database by ID;");
                System.out.println("(3), or bycat - get information about product in the database by category;");
                break;
            case "update":
                System.out.println("(0), or up - back to main menu;");
                System.out.println("(1), or byid - update information of product in the database by ID;");
                break;
        }
    }

    private BufferedReader reader() {
        InputStreamReader inputStream = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inputStream);
        return reader;
    }

    private String readCategories() {
        System.out.println("Available product categories: ");

        printProductsCategoriesList(ProductCategory.getCategories());

        BufferedReader reader = reader();

        System.out.println("Enter product category(or 0-8): ");
        String productCategory = null;
        try {
            productCategory = reader.readLine().toUpperCase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (productCategory) {
            case "0":
                productCategory = "ALCOHOL";
                break;
            case "1":
                productCategory = "BREAD";
                break;
            case "2":
                productCategory = "FISH";
                break;
            case "3":
                productCategory = "FRUITS";
                break;
            case "4":
                productCategory = "MEAT";
                break;
            case "5":
                productCategory = "MILK";
                break;
            case "6":
                productCategory = "SOFT_DRINKS";
                break;
            case "7":
                productCategory = "SWEETS";
                break;
            case "8":
                productCategory = "VEGETABLES";
                break;
            case "":
                productCategory = null;
                break;
        }
        return productCategory;
    }

    private BigDecimal readBigDecimal() {
        BufferedReader reader = reader();
        String decimal = null;

        try {
            decimal = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (decimal.isEmpty()) ? null : new BigDecimal(decimal).setScale(2, RoundingMode.HALF_EVEN);
    }

    private String readString() {
        BufferedReader reader = reader();
        String string = null;

        try {
            string = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (string.isEmpty()) ? null : string;
    }

    private BigDecimal readDiscount() {
        System.out.println("Enter discount (from 0 to 100): ");
        return readBigDecimal();
    }

    private void addNewProduct() throws IOException {
        CreateRequest productCreateRequest = new CreateRequest();
        BufferedReader reader = reader();

        System.out.println("Product name: ");
        String productName = reader.readLine();
        productCreateRequest.setProductName(productName);

        System.out.println("Product price: ");
        BigDecimal productPrice = readBigDecimal();
        productCreateRequest.setProductPrice(productPrice);

        String productCategory = readCategories();
        productCreateRequest.setProductCategory(productCategory);

        BigDecimal productDiscount = readDiscount();
        productCreateRequest.setProductDiscount(productDiscount);

        System.out.println("Product description: ");
        String productDescription = readString();
        productCreateRequest.setProductDescription(productDescription);


        CreateResponse createResponse = productService.addProduct(productCreateRequest);
        if (createResponse.hasValidationErrors() || createResponse.hasDBRrrors()) {
            printValidationErrorsList(createResponse.getValidationErrors());
            printDBErrorsList(createResponse.getDBErrors());
        } else {
            System.out.println("Product was successfuly added.");
        }
    }

    private void findProductByID() throws IOException {
        BufferedReader reader = reader();

        System.out.println("Enter product ID: ");
        String productID = reader.readLine();

        FindRequest findRequest = new FindRequest();
        if (!productID.isEmpty()) {
            findRequest.setProductID(Long.valueOf(productID));
            printProduct(productService.findByID(findRequest).getFoundProduct());
        } else {
            System.out.println("ID can't be empty");
        }
    }

    private void findProductByCategory() {
        String productCategory = readCategories();

        FindRequest findRequest = new FindRequest();

        if (productCategory != null) {
            findRequest.setProductCategory(ProductCategory.valueOf(productCategory));
            printProductsList(productService.findByCategory(findRequest).getListOfFoundProducts());
        } else {
            System.out.println("Category can't be empty");
        }
    }

    private void updateProductByID() throws IOException {
        BufferedReader reader = reader();

        System.out.println("Enter product ID: ");
        String productID = reader.readLine();

        FindRequest findRequest = new FindRequest();
        if (!productID.isEmpty()) {
            findRequest.setProductID(Long.valueOf(productID));

            FindResponse findResponse = productService.findByID(findRequest);

            if (findResponse.getFoundProduct() != null) {
                System.out.println("Product name: ");
                String productName = readString();
                productName = (productName == null || productName.isEmpty() ? findResponse.getFoundProduct().getProductName() : productName);

                System.out.println("Product price:");
                BigDecimal productPrice = readBigDecimal();
                productPrice = (productPrice == null ? findResponse.getFoundProduct().getProductRegularPrice() : productPrice);

                String productCategory = readCategories();
                productCategory = (productCategory == null ? String.valueOf(findResponse.getFoundProduct().getProductCategory()) : productCategory);

                BigDecimal productDiscount = readDiscount();
                productDiscount = (productDiscount == null ? findResponse.getFoundProduct().getProductDiscount() : productDiscount);

                System.out.println("Product description: ");
                String productDescription = readString();
                productDescription = (productDescription == null || productDescription.isEmpty() ? findResponse.getFoundProduct().getProductDescription() : productDescription);

                UpdateRequest updateRequest = new UpdateRequest();

                updateRequest.setProductID(Long.valueOf(productID));
                updateRequest.setProductName(productName);
                updateRequest.setProductCategory(productCategory);
                updateRequest.setProductPrice(productPrice);
                updateRequest.setProductDiscount(productDiscount);
                updateRequest.setProductDescription(productDescription);

                UpdateResponse updateResponse = productService.updateByID(updateRequest);

                if (updateResponse.hasValidationErrors()) {
                    printValidationErrorsList(updateResponse.getValidationErrors());
                } else {
                    printProduct(updateResponse.getUpdatedProduct());
                    System.out.println("Product was successfuly updated.");
                }
            } else if (findResponse.hasDBRrrors()) {
                printDBErrorsList(findResponse.getDBErrors());
            } else {
                System.out.println("Such entry doesn't exist in database");
            }
        } else {
            System.out.println("ID can't be empty");
        }
    }

    private void deleteProduct() throws IOException {
        BufferedReader reader = reader();

        System.out.println("Enter product ID: ");
        Long productID = Long.valueOf(reader.readLine());

        FindRequest findRequest = new FindRequest();
        findRequest.setProductID(productID);

        if (productService.deleteByID(findRequest)) {
            System.out.println("Product was successfuly deleted.");
        } else {
            System.out.println("Product not found.");
        }
    }

    private void printProduct(Product product) {
        System.out.print("ID: " + product.getProductID() +
                ", Product: " + product.getProductName() +
                ", Regular price: " + product.getProductRegularPrice() + "$" + ", " +
                "Discount: " + product.getProductDiscount() + "%, " +
                "Actual price: " + product.calculateActualPrice() + "$" + ", " +
                "Category: " + product.getProductCategory());
        if (product.getProductDescription() != null) {
            System.out.print(", Description: " + product.getProductDescription());
        }
        System.out.println();
    }

    private void printProductsList(List<Product> productsList) {
        if (productsList != null) {
            for (Product product : productsList) {
                printProduct(product);
            }
        } else {
            System.out.println("Please, try later");
        }
    }

    private void printProductsCategoriesList(List<ProductCategory> list) {
        for (ProductCategory productCategory : list) {
            System.out.println(list.indexOf(productCategory) + " - " + productCategory.name());
        }
    }

    private void printValidationErrorsList(List<ValidationErrors> errorsList) {
        if (errorsList != null && !errorsList.isEmpty()) {
            for (ValidationErrors validationErrors : errorsList) {
                System.out.println(validationErrors.getResponse());
            }
        }
    }

    private void printDBErrorsList(List<DBErrors> errorsList) {
        if (errorsList != null && !errorsList.isEmpty()) {
            for (DBErrors dbErrors : errorsList) {
                System.out.println(dbErrors.getResponse());
            }
        }
    }
}
