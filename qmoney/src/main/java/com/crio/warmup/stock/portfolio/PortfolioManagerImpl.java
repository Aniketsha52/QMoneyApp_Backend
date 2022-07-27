
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  private RestTemplate restTemplate;
  private StockQuotesService stockQuotesService;

  PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }

@Override
public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
    LocalDate endDate) throws StockQuoteServiceException {
  AnnualizedReturn annualizedReturn;
  List<AnnualizedReturn> annualizedReturns = new ArrayList<AnnualizedReturn>();

  //loop through all prortfilio trade object
  for (int i = 0; i < portfolioTrades.size(); i++) {
    // get annuallizse return object for each

    annualizedReturn = getAnnualizedReturn(portfolioTrades.get(i), endDate);

    annualizedReturns.add(annualizedReturn);
  }

    Comparator<AnnualizedReturn> SortByAnnReturn =
        Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
    Collections.sort(annualizedReturns, SortByAnnReturn);

    return annualizedReturns;

  }
   
  private AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endLocalDate) throws StockQuoteServiceException {
    AnnualizedReturn annualizedReturn;
    String symbol = trade.getSymbol();
    LocalDate startLocalDate = trade.getPurchaseDate();

    try{
      //fatch data
      List<Candle> stockStartToEndDate;
      stockStartToEndDate = getStockQuote(symbol, startLocalDate, endLocalDate);

      //extract stocks for startDate & endDate
      Candle stockStartDate = stockStartToEndDate.get(0);
      Candle stockLatest = stockStartToEndDate.get(stockStartToEndDate.size() - 1);

      Double buyPrice = stockStartDate.getOpen();
      Double sellPrice = stockLatest.getClose();

      //calculate total returns
      Double totalReturn = (sellPrice - buyPrice) / buyPrice;

      //calculate years
        Double numYers = (double) ChronoUnit.DAYS.between(startLocalDate, endLocalDate) / 364.24;

      //calculate annualized return
        Double annualizedReturns = Math.pow((1 + totalReturn), (1 / numYers)) - 1;
        annualizedReturn = new AnnualizedReturn(symbol, annualizedReturns, totalReturn);


      } catch (JsonProcessingException e) {
        annualizedReturn = new AnnualizedReturn(symbol, Double.NaN, Double.NaN);

      }
      return annualizedReturn;
}


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF






  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException  {
    //get stock start date to end date
    // start adate = purachedate
    //throw error if start date not before end date
    return stockQuotesService.getStockQuote(symbol, from, to);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = "3adb8bd633fd457f09b4d4f74e5e506f8acba793";

       String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
           + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
      
      String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol)
      .replace("$STARTDATE", startDate.toString())
          .replace("$ENDDATE", endDate.toString());
    
      return url;
            
  }




  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.



}
