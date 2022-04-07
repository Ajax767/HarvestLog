package com.james.adam.cropProd.model;

import java.time.LocalDate;
import java.math.BigDecimal;

public class Harvest {
	
	private String species;
	private String subSpecies;
	private int quantity;
	private BigDecimal mass;
	private LocalDate harvestDate;
	private String cropCode;
	private long ID;

	public Harvest(String species, String subSpecies, int quantity, BigDecimal mass, LocalDate harvestDate, String cropCode) {
		this.species = species;
		this.subSpecies = subSpecies;
		this.quantity = quantity;
		this.mass = mass;
		this.harvestDate = harvestDate;
		this.cropCode = cropCode;		
	}

	public String getSpecies() {
		return species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public String getSubSpecies() {
		return subSpecies;
	}

	public void setSubSpecies(String subSpecies) {
		this.subSpecies = subSpecies;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getMass() {
		return mass;
	}	

	public void setMass(BigDecimal mass) {
		this.mass = mass;
	}

	public LocalDate getHarvestDate() {
		return harvestDate;
	}

	public void setHarvestDate(LocalDate harvestDate) {
		this.harvestDate = harvestDate;
	}

	public String getCropCode() {
		return cropCode;
	}

	public void setCropCode(String cropCode) {
		this.cropCode = cropCode;
	}
	
	public long getID() {
		return ID;
	}

	public void setID(long ID) {
		this.ID = ID;
	}
@Override
	public String toString() {
		return "Harvest [species=" + species + ", subSpecies=" + subSpecies + ", quantity=" + quantity + ", mass="
				+ mass + ", harvestDate=" + harvestDate + ", cropCode=" + cropCode + "]";
	}
}
