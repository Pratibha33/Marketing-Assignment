package org.techArk.EndToEndScript;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.techArk.requestPOJO.AddDataRequest;
import org.techArk.responsePOJO.LoginResponse;
import org.techArk.utils.APIHelper;
import org.techArk.utils.BaseTest;
import org.techArk.utils.EnvironmentDetails;
import org.techArk.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

public class EndToEndScript extends BaseTest {
	APIHelper apiHelper;
	String name, email, location;
	Integer userId;

	@BeforeClass
	public void beforeClass() {
		apiHelper = new APIHelper();

	}

	@Test(priority = 1)
	public void registerNewUser() {

		Response login = apiHelper.registerUser(EnvironmentDetails.getProperty("name"),
				EnvironmentDetails.getProperty("email"), EnvironmentDetails.getProperty("password"));
		Assert.assertEquals(login.getStatusCode(), HttpStatus.SC_OK);
		String actualResponse = login.jsonPath().prettyPrint();
		actualResponse = actualResponse.replace("[", "").replace("]", "");
		JsonUtils.validateSchema(actualResponse, "LoginResponseSchema.json");

	}

	@Test(priority = 2)
	public void registerExistingUser() {

		Response login = apiHelper.registerExistingUser(EnvironmentDetails.getProperty("name"),
				EnvironmentDetails.getProperty("email"), EnvironmentDetails.getProperty("password"));
		Assert.assertEquals(login.getStatusCode(), HttpStatus.SC_OK);
		String actualResponse = login.jsonPath().prettyPrint();
		actualResponse = actualResponse.replace("[", "").replace("]", "");
		JsonUtils.validateSchema(actualResponse, "InvalidDataResponse.json");

	}

	@Test(priority = 3)
	public void validLogin() {

		Response login = apiHelper.login(EnvironmentDetails.getProperty("email"),
				EnvironmentDetails.getProperty("password"));
		userId = login.getBody().as(new TypeRef<LoginResponse>() {
		}).data.getId();
		Assert.assertEquals(login.getStatusCode(), HttpStatus.SC_OK, "Login is  working for valid credentials.");
		String actualResponse = login.jsonPath().prettyPrint();
		actualResponse = actualResponse.replace("[", "").replace("]", "");
		JsonUtils.validateSchema(actualResponse, "LoginResponseSchema.json");

	}

	@Test(priority = 4)
	public void invalidLogin() {

		Response login = apiHelper.login(EnvironmentDetails.getProperty("invalidemail"),
				EnvironmentDetails.getProperty("password"));
		Assert.assertEquals(login.getStatusCode(), HttpStatus.SC_OK, "Login fails for invalid credentials.");
		String actualResponse = login.jsonPath().prettyPrint();
		actualResponse = actualResponse.replace("[", "").replace("]", "");
		JsonUtils.validateSchema(actualResponse, "InvalidDataResponse.json");

	}

	@Test(priority = 5, description = "adding a new user with new email address", dependsOnMethods = { "validLogin" })
	public void createNewUser() {

		name = "Pratibha";
		email = "abc7000@gmail.com";
		location = "USA";
		AddDataRequest addDataRequest = AddDataRequest.builder().name(name).email(email).location(location).build();
		Response response = apiHelper.addData(addDataRequest, userId);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_CREATED,
				"Create new user functionality is  working as expected.");

		String actualResponse = response.jsonPath().prettyPrint();
		JsonUtils.validateSchema(actualResponse, "GetDataResponseSchema.json");

	}

	@Test(priority = 6, description = " adding existing user", dependsOnMethods = { "createNewUser" })
	public void createExistingUser() {

		name = "Pratibha";
		email = "abc7000@gmail.com";
		location = "USA";
		AddDataRequest addDataRequest = AddDataRequest.builder().name(name).email(email).location(location).build();
		Response response = apiHelper.addData(addDataRequest, userId);
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_BAD_REQUEST,
				"Cannot add same data again. Functionality Working as expected");
	}

	@Test(priority = 7, description = " getting all user records")
	public void getAllUsers() {
		Response response = apiHelper.getData();
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
		String actualResponse = response.jsonPath().prettyPrint();
		JsonUtils.validateSchema(actualResponse, "AllUsersResponse.json");
	}

	@Test(priority = 8, description = " getting all user records")
	public void validateUserRecords() {
		Response response = apiHelper.getData();
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
		String actualResponse = response.jsonPath().prettyPrint();
		JsonUtils.validateSchema(actualResponse, "AllUsersResponse.json");
		int total_pages = response.jsonPath().getInt("total_pages");
		int totalRecordActual = response.jsonPath().getInt("totalrecord");
		HashMap<Integer, Integer> dataSize = new HashMap<Integer, Integer>();
		int totalRecordExpected = 0;
		for (int i = 1; i <= total_pages; i++) {
			Response res = apiHelper.getRecordsByPage(i);
			List<Object> data = res.jsonPath().getList("data");
			totalRecordExpected += data.size();
			dataSize.put(i, data.size());
		}
		for (int i = 1; i < total_pages; i++) {
			// validating that there are 10 records except last page
			Assert.assertEquals(dataSize.get(i).intValue(), 10);
		}
		System.out.println("ten records per page as expected");
		Assert.assertEquals(totalRecordActual, totalRecordExpected, "Record totals match!");
		// checking if total records match the total records in response
		System.out.println(" total records match!");

	}

	@Test(priority = 9, enabled = true)
	public void writeToExcelFile() throws IOException {

		Response response = apiHelper.getData();
		Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
		String actualResponse = response.jsonPath().prettyPrint();
		JsonUtils.validateSchema(actualResponse, "AllUsersResponse.json");
		int total_pages = response.jsonPath().getInt("total_pages");

		HashMap<String, Integer> mapLocations = new HashMap<String, Integer>();
		List<Object> locations = null;

		List<Object> emails = null;
		HashMap<String, Integer> mapEmails = new HashMap<String, Integer>();

		for (int i = 1; i <= total_pages; i++) {
			Response res = apiHelper.getRecordsByPage(i);
			locations = res.jsonPath().getList("data.location");
			if (!mapLocations.containsKey(locations.get(i)))
				mapLocations.put((String) locations.get(i), 1);
			else {

				int currentcount = mapLocations.get(locations.get(i));
				currentcount++;
				mapLocations.put((String) locations.get(i), currentcount);
			}
			emails = res.jsonPath().getList("data.email");
			if (!mapEmails.containsKey(emails.get(i)))
				mapEmails.put((String) emails.get(i), 1);
			else {

				int currentcount = mapEmails.get(emails.get(i));
				currentcount++;
				mapEmails.put((String) emails.get(i), currentcount);
			}
		}

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Total Records per city");
		Set<String> keyset = mapLocations.keySet();
		int rownum = 0;
		Row row = sheet.createRow(rownum++);
		row.createCell(0).setCellValue("Location");
		row.createCell(1).setCellValue("Total Records");
		for (String key : keyset) {
			int cellnum = 0;
			// Creating a new row in the sheet

			Row row2 = sheet.createRow(rownum++);
			row2.createCell(cellnum++).setCellValue(key);
			row2.createCell(cellnum++).setCellValue(mapLocations.get(key));

		}

		XSSFSheet sheet2 = workbook.createSheet("Total Emails per city");
		Set<String> keyset2 = mapEmails.keySet();
		int rownum2 = 0;
		Row row3 = sheet2.createRow(rownum2++);
		row3.createCell(0).setCellValue("Location");
		row3.createCell(1).setCellValue("Total Emails");
		for (String key : keyset2) {
			int cellnum1 = 0;
			// Creating a new row in the sheet

			Row row4 = sheet.createRow(rownum++);
			row4.createCell(cellnum1++).setCellValue(key);
			row4.createCell(cellnum1++).setCellValue(mapEmails.get(key));

		}

		// Try block to check for exceptions
		try {

			// Writing the workbook
			FileOutputStream out = new FileOutputStream(new File("totalrecords.xlsx"));
			workbook.write(out);
			// Closing file output connections
			out.close();

			System.out.println();
		}

		catch (Exception e) {

			e.printStackTrace();
		} finally {
			workbook.close();

		}
	}

}
