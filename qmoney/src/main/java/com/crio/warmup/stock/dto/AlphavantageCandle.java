package com.crio.warmup.stock.dto;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
//  Implement the Candle interface in such a way that it matches the parameters returned
//  inside Json response from Alphavantage service.

   //Reference - https:www.baeldung.com/jackson-ignore-properties-on-serialization
   //Reference - https:www.baeldung.com/jackson-name-of-property
   @JsonIgnoreProperties(ignoreUnknown = true)
public class AlphavantageCandle implements Candle {
  @JsonProperty("1. open")
  private Double open;

  @JsonProperty("2. high")
  private double high;

  @JsonProperty("3. low")
  private Double low;

  @JsonProperty("4. close")
  private Double close;
  
  
  private LocalDate date;

  @Override
  public Double getClose() {
    return close;
  }

  public void setClose(Double close){
    this.close = close;
  }

  @Override
  public Double getOpen() {
    // TODO Auto-generated method stub
    return open;
  }
  
  public void setOpen(Double open) {
    this.open = open;
  }

  @Override
  public Double getHigh() {
    // TODO Auto-generated method stub
    return high;
  }
  
  public void setHigh(Double high) {
    this.high = high;
  }

  @Override
  public Double getLow() {
    // TODO Auto-generated method stub
    return low;
  }
  
  public void setLow(Double low) {
    this.low = low;
  }

  @Override
  public LocalDate getDate() {
    // TODO Auto-generated method stub
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
}




