
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {
  private RestTemplate restTemplate;
  public static final String token = "3adb8bd633fd457f09b4d4f74e5e506f8acba793";

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException{
    
    List<Candle> stockStartToEndDate;
    if (from.compareTo(to) >= 0) {
      throw new RuntimeException();
    }

    // create url object for api call
    String url = buildUri(symbol, from, to);

    try{
    String stocks = restTemplate.getForObject(url, String.class);
    ObjectMapper objectMapper = getObjectMapper();

    // api returns a list of result each day stock data
     TiingoCandle[] stockStartToEndDatetoArray = objectMapper.readValue(stocks, TiingoCandle[].class);

    if (stockStartToEndDatetoArray != null) {
      stockStartToEndDate = Arrays.asList(stockStartToEndDatetoArray);
    } else {
      stockStartToEndDate = Arrays.asList(new TiingoCandle[0]);
    }
  } catch (NullPointerException e) {
    throw new StockQuoteServiceException("Error occured when resquesting response from Tiingo API",
        e.getCause());
  }

    return stockStartToEndDate;
  }
  
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    

    String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
        + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";

    String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol)
        .replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());

    return url;

  }


  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }






  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
