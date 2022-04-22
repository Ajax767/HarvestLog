package com.james.adam.cropProd.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.james.adam.cropProd.model.Harvest;

public class cropProdTest {

	private Connection dBAddress;
	private HarvestLog harvest;
	private String fileName = "c:/H2/vg2021.csv";

	@BeforeEach
	void setUp() throws SQLException {
		dBAddress = DriverManager.getConnection("jdbc:h2:C://Users//Ajax/harvestDB");
		dBAddress.setAutoCommit(false);
		harvest = new HarvestLog(dBAddress);
	}

	@AfterEach
	void tearDown() throws SQLException {
		dBAddress.close();
	}

	@Test
	
	public void saveEntry() {
		Harvest plant = new Harvest("Tomato", "Yellow Pear", 6, new BigDecimal("2.45"), LocalDate.of(2021, 7, 26),"T1");
		long verifyPlant = harvest.save(plant);
		assert (verifyPlant==1);
	}

	@Test

	public void getCountEntries() {
		long startCount = harvest.countEntries();
		System.out.println(startCount);
		harvest.save(new Harvest("Tomato", "Yellow Pear", 6, new BigDecimal("2.45"), LocalDate.of(2021, 7, 26), "T1"));
		harvest.save(new Harvest("Tomato", "Black Cherry", 12, new BigDecimal("4.91"), LocalDate.of(2021, 7, 28), "T2"));
		long endCount = harvest.countEntries();
		System.out.println(endCount);
		assert (endCount == startCount + 2);
	}

	@Test
	
	public void deleteByID()  { 
		long startCount = harvest.countEntries(); 
		int newCount = harvest.deleteByID(1);
		long endCount = harvest.countEntries(); 
		assert(endCount == startCount - newCount); 
	}

	@Test

	public void deleteBySpecies() throws SQLException { 
		harvest.save(new Harvest("Pepper", "Green Bell", 6, new BigDecimal("2.45"), LocalDate.of(2021, 7, 26), "P1"));
		long startCount = harvest.countEntries(); 
		int newCount = harvest.deleteBySpecies("Pepper");
		long endCount = harvest.countEntries(); 
		assert(endCount == startCount - newCount); 
	}

	@Test

	public void deleteBySubSpecies() throws SQLException {
		harvest.save(new Harvest("Tomato", "Black Cherry", 12, new BigDecimal("4.91"), LocalDate.of(2021, 7, 28), "T2"));
		long startCount = harvest.countEntries(); 
		int newCount = harvest.deleteBySubSpecies("Black Cherry");
		long endCount = harvest.countEntries(); 
		assert(endCount == startCount - newCount); 
	}
	
	@Test
	
	public void addEntriesFromFile() throws SQLException {
		
		long recordCount = harvest.addFromFile(fileName);
		System.out.println(harvest.getSaveCount());
		assert(recordCount == harvest.getSaveCount());
	}

	@Test
	
	public void canSumBySpeciesWeight(){
		Harvest h1 = new Harvest("Tomato", "Yellow Pear", 6, new BigDecimal("2.45"), LocalDate.of(2021, 7, 26), "T1");
		BigDecimal m1 = h1.getMass();
		Harvest h2 = new Harvest("Tomato", "Yellow Pear", 6, new BigDecimal("3.61"), LocalDate.of(2021, 7, 26), "T1");
		BigDecimal m2 = h2.getMass();
		System.out.println(m1.add(m2));
		harvest.save(h1);
		harvest.save(h2);
		BigDecimal totalWeight = harvest.sumBySpeciesWeight("Tomato");
		
		assert(totalWeight.compareTo(m1.add(m2))==0);
	}
	
	@Test
	
	public void canSumBySubSpeciesWeight(){
		Harvest h1 = new Harvest("Tomato", "Yellow Pear", 6, new BigDecimal("2.45"), LocalDate.of(2021, 7, 26), "T1");
		BigDecimal m1 = h1.getMass();
		Harvest h2 = new Harvest("Tomato", "Yellow Pear", 6, new BigDecimal("3.61"), LocalDate.of(2021, 7, 26), "T1");
		BigDecimal m2 = h2.getMass();
		System.out.println(m1.add(m2));
		harvest.save(h1);
		harvest.save(h2);
		BigDecimal totalWeight = harvest.sumBySubSpeciesWeight("Yellow Pear");
		
		assert(totalWeight.compareTo(m1.add(m2))==0);
	}
	
	@Test
	
	public void findHighestYieldDay() {
		LocalDate knownHighestYieldDate = LocalDate.of(2021, 07, 21);
		LocalDate highestYieldDate = harvest.highestYieldDay();
		assert(highestYieldDate.isEqual(knownHighestYieldDate));
	}
	  
}
