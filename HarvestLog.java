package com.james.adam.cropProd.repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import com.james.adam.cropProd.model.Harvest;

public class HarvestLog {
	
	private static final String SUM_BY_SPECIES = "SELECT MASS FROM HARVEST WHERE SPECIES=?";
	private static final String SUM_BY_SUBSPECIES = "SELECT MASS FROM HARVEST WHERE SUBSPECIES=?";
	private static final String ADD_HARVEST = "INSERT INTO HARVEST (SPECIES, SUBSPECIES, QUANTITY, MASS, HARVEST_DATE, PLANT_CODE) VALUES(?, ?, ?, ?, ?, ?)";
	private static final String INDEX_COUNT = "SELECT COUNT(*) FROM HARVEST";	
	private static final String DELETE_BY_ID = "DELETE FROM HARVEST WHERE ID=?";
	private static final String DELETE_BY_SPECIES = "DELETE FROM HARVEST WHERE SPECIES=?";
	private static final String DELETE_BY_SUBSPECIES = "DELETE FROM HARVEST WHERE SUBSPECIES=?";
	
	private Connection dBAddress;
	private long numRecords;
	private long saveCount;
	
	public HarvestLog(Connection dBAddress) {
		this.dBAddress = dBAddress;
	}

	public long save(Harvest plant) {
		
		PreparedStatement ps;
		try {
			ps = dBAddress.prepareStatement(ADD_HARVEST, Statement.RETURN_GENERATED_KEYS);			
			ps.setString(1, plant.getSpecies());
			ps.setString(2, plant.getSubSpecies());
			ps.setLong(3, plant.getQuantity());
			ps.setBigDecimal(4, plant.getMass());
			ps.setDate(5, Date.valueOf(plant.getHarvestDate()));
			ps.setString(6, plant.getCropCode());
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			
			while (rs.next()) {
				saveCount++;
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return saveCount;
	}

	public long countEntries() {
		long count = 0;
		try {
			PreparedStatement ps = dBAddress.prepareStatement(INDEX_COUNT);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {				
				count = rs.getLong(1);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return count;
	}

	
	public int deleteByID(long id) {
		int affectedRecordCount=0;
		try {
			PreparedStatement ps = dBAddress.prepareStatement(DELETE_BY_ID);
			ps.setLong(1, id);
			affectedRecordCount = ps.executeUpdate();
			System.out.println(affectedRecordCount + " record(s) have been deleted.");			
		} catch (SQLException e) {
			e.printStackTrace();			
		}	
		return affectedRecordCount;
	}
	
	public int deleteBySpecies(String species) {
		int affectedRecordCount=0;
		try {
			PreparedStatement ps = dBAddress.prepareStatement(DELETE_BY_SPECIES);
			affectedRecordCount = setForDelete(species, ps);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return affectedRecordCount;
	}
	
	public int deleteBySubSpecies(String subSpecies) {
		int affectedRecordCount=0;
		try {
			PreparedStatement ps = dBAddress.prepareStatement(DELETE_BY_SUBSPECIES);
			affectedRecordCount = setForDelete(subSpecies, ps);			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		return affectedRecordCount;
	}
	
	public long addFromFile(String fileName) {
		
		try {
			numRecords = Files.lines(Path.of(fileName)).parallel().skip(1).count();
			System.out.printf("This file contains %d lines.%n", numRecords);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			 Files.lines(Path.of(fileName)).parallel()
			.skip(1)			
			.map(s -> s.split(","))
			.map(array -> new Harvest(array[0], array[1], Integer.parseInt(array[2]), new BigDecimal(array[3]), dateFormatter(array[4]), array[5]))
			.forEach(h -> save(h));
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return numRecords;
	}
	
	public BigDecimal sumBySpeciesWeight(String species) {
		BigDecimal totalWeight = BigDecimal.ZERO;
		try {
			PreparedStatement ps = dBAddress.prepareStatement(SUM_BY_SPECIES);
			ps.setString(1, species);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				totalWeight = totalWeight.add(rs.getBigDecimal("MASS")).stripTrailingZeros();
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return totalWeight;
	}

	public BigDecimal sumBySubSpeciesWeight(String subSpecies) {
		BigDecimal totalWeight = BigDecimal.ZERO;
		try {
			PreparedStatement ps = dBAddress.prepareStatement(SUM_BY_SUBSPECIES);
			ps.setString(1, subSpecies);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				totalWeight = totalWeight.add(rs.getBigDecimal("MASS")).stripTrailingZeros();
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return totalWeight;
	}
	
	public Date highestYieldDay() {
		record yieldByDate (BigDecimal mass, Date harvestDate){}
		List<yieldByDate> yieldList = new ArrayList<>();	
		Date result = null;
		try {
			PreparedStatement ps = dBAddress.prepareStatement(PULL_RECORD);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				BigDecimal mass = rs.getBigDecimal("MASS");
				Date harvestDate = rs.getDate("HARVEST_DATE");
				yieldList.add(new yieldByDate(mass, harvestDate));			
			}		
			
			result = yieldList.stream()
				.collect(
					Collectors.groupingBy(yieldByDate::harvestDate, HashMap::new,
						Collectors.reducing(BigDecimal.ZERO, yieldByDate::mass, (a,b) -> a.add(b))))
				.entrySet().stream()
				.max((a, b) -> a.getValue().compareTo(b.getValue()))
				.get().getKey();			
			
		} catch (SQLException e) {		
			e.printStackTrace();
		}
		return result;
	}
	
	private static LocalDate dateFormatter(String string) {
		String[] dateString = string.split("/");
		int year = Integer.parseInt(dateString[2]);
		int month = Integer.parseInt(dateString[0]);
		int day = Integer.parseInt(dateString[1]);
		
		return LocalDate.of(year, month, day);
	}

	private int setForDelete(String attribute, PreparedStatement ps) throws SQLException {
		int affectedRecordCount;
		ps.setString(1, attribute);
		affectedRecordCount = ps.executeUpdate();
		System.out.println(affectedRecordCount + " record(s) have been deleted.");
		return affectedRecordCount;
	}
	
	public long getSaveCount() {
		return saveCount;
	}
}
