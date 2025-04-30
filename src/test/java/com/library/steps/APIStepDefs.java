package com.library.steps;

import com.library.pages.BookPage;
import com.library.pages.LoginPage;
import com.library.utility.DB_Util;
import com.library.utility.LibraryAPI_Util;
import io.cucumber.java.en.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class APIStepDefs {

    RequestSpecification givenPart = given().log().all(); //RestAssured den alıyor.
    Response response;
    ValidatableResponse thenPart;
    JsonPath jp;
    LoginPage loginPage=new LoginPage();
    BookPage bookPage=new BookPage();
    Map<String, Object> randomData=new HashMap<>();


    String expectedID;

    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String role) {
        givenPart.header("x-library-token", LibraryAPI_Util.getToken(role));

    }

    @Given("Accept header is {string}")
    public void accept_header_is(String acceptHeader) {
        givenPart.accept(acceptHeader);

    }

    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endPoint) {
        response = givenPart.when().get(endPoint);
        response.prettyPrint();

    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer expectedStatusCode) {
        thenPart = response.then();
        thenPart.statusCode(expectedStatusCode);


    }

    @Then("Response Content type is {string}")
    public void response_content_type_is(String expectedContentType) {
        thenPart.contentType(expectedContentType);


    }

    @Then("Each {string} field should not be null")
    public void each_field_should_not_be_null(String path) {
        thenPart.body(path, Matchers.everyItem(Matchers.notNullValue()));

        //OPT2
        List<String> allData = jp.getList(path);
        for (String eachData : allData) {
            Assert.assertNotNull(eachData);
        }
    }
    /***************UserStory 02***************/

    @Given("Path param {string} is {string}")
    public void path_param_is(String pathParamKey, String pathParamValue) {
        givenPart.pathParam(pathParamKey, pathParamValue);
        expectedID=pathParamValue; // declare globally
    }

    @Then("{string} field should be same with path param")
    public void field_should_be_same_with_path_param(String pathParam) {
        String actualID = jp.getString(pathParam);
        assertEquals(expectedID, actualID);

    }

    @Then("following fields should not be null")
    public void following_fields_should_not_be_null(List<String> allPaths) {

        for (String eachPath : allPaths) {
            thenPart.body(eachPath, Matchers.notNullValue());
        }


    }

    /********** US03 **********/
    //Scenario: Create a new book all layers

    @Given("I logged in Library UI as {string}")
    public void ı_logged_in_library_uı_as(String role) {
        // LoginPage loginPage= new LoginPage();
        loginPage.login(role);

    }


    @Given("I navigate to {string} page")
    public void ı_navigate_to_page(String moduleName) {

        bookPage.navigateModule(moduleName);

    }

    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String string) {


    }
    @Given("I create a random {string} as request body")
    public void ı_create_a_random_as_request_body(String dataType) {
        switch (dataType){
            case "book":
                randomData=LibraryAPI_Util.getRandomBookMap();
                break;
            case "user":
                randomData= LibraryAPI_Util.getRandomUserMap();
                break;
        }
        //Map<String, String> formParams = new HashMap<>();
        givenPart.formParams(randomData);

    }
    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String string) {


    }
    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String string, String string2) {


    }
    @Then("{string} field should not be null")
    public void field_should_not_be_null(String string) {


    }

    @Then("UI, Database and API created book information must match")
    public void uı_database_and_apı_created_book_information_must_match() {


        // actual data from DB
        String bookID=jp.getString("id");
        String query="select * from books where id=" +bookID;

        DB_Util.runQuery(query);
        Map<String, Object> dataMap = DB_Util.getRowMap(1);

        System.out.println("randomData = " + randomData);

        // expected              actual
        assertEquals(randomData.get("id").toString(), dataMap.get("book_id").toString());

    }
}
