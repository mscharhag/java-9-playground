package com.mscharhag.java9.money;


import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.format.CurrencyStyle;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.javamoney.moneta.function.MonetarySummaryStatistics;

import javax.money.*;
import javax.money.convert.*;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static void currencyUnit() {

        // getting CurrencyUnits by currency code
        CurrencyUnit euro = MonetaryCurrencies.getCurrency("EUR");
        CurrencyUnit usDollar = MonetaryCurrencies.getCurrency("USD");

        // getting CurrencyUnits by locale
        CurrencyUnit yen = MonetaryCurrencies.getCurrency(Locale.JAPAN);
        CurrencyUnit canadianDollar = MonetaryCurrencies.getCurrency(Locale.CANADA);

        System.out.println("euro = " + euro);
        System.out.println("usDollar = " + usDollar);
        System.out.println("yen = " + yen);
        System.out.println("canadianDollar = " + canadianDollar);
    }

    private static void monetaryAmount() {
        CurrencyUnit euro = MonetaryCurrencies.getCurrency("EUR");
        MonetaryAmount fiveEuro = Money.of(5, euro);

        MonetaryAmount tenUsDollar = Money.of(10, "USD");

        // multiple implementations of MonetaryAmount are available
        MonetaryAmount sevenEuro = FastMoney.of(7, euro);

        System.out.println("fiveEuro = " + fiveEuro);
        System.out.println("tenUsDollar = " + tenUsDollar);
        System.out.println("sevenEuro = " + sevenEuro);

        // getting a MonetaryAmount without import implementation classes
        MonetaryAmount specAmount = MonetaryAmounts.getDefaultAmountFactory()
                .setNumber(123.45)
                .setCurrency("USD")
                .create();

        System.out.println("specAmount = " + specAmount);


        MonetaryAmount monetaryAmount = Money.of(123.45, euro);
        CurrencyUnit currency = monetaryAmount.getCurrency();
        NumberValue numberValue = monetaryAmount.getNumber();

        int intValue = numberValue.intValue(); // 123
        double doubleValue = numberValue.doubleValue(); // 123.45
        long fractionDenominator = numberValue.getAmountFractionDenominator(); // 100
        long fractionNumerator = numberValue.getAmountFractionNumerator(); // 45
        int precision = numberValue.getPrecision(); // 5

        // NumberValue extends java.lang.Number. So we assign numberValue to a variable of type Number
        Number number = numberValue;

        // equality
        MonetaryAmount oneEuro = Money.of(1, MonetaryCurrencies.getCurrency("EUR"));
        boolean isEqual = oneEuro.equals(Money.of(1, "EUR"));
        boolean fastEqual = oneEuro.equals(FastMoney.of(1, "EUR"));


        System.out.println("intValue = " + intValue);
        System.out.println("doubleValue = " + doubleValue);
        System.out.println("fractionDenominator = " + fractionDenominator);
        System.out.println("fractionNumerator = " + fractionNumerator);
        System.out.println("precision = " + precision);
        System.out.println("isEqual = " + isEqual);
        System.out.println("fastEqual = " + fastEqual);
    }


    private static void monetaryAmountOperations() {

        MonetaryAmount fiveEuro = Money.of(5, "EUR");
        MonetaryAmount tenUsDollar = Money.of(10, "USD");
        MonetaryAmount sevenEuro = FastMoney.of(7, "EUR");

        MonetaryAmount twelveEuro = fiveEuro.add(sevenEuro); // "EUR 12"
        MonetaryAmount twoEuro = sevenEuro.subtract(fiveEuro); // "EUR 2"
        MonetaryAmount sevenPointFiveEuro = fiveEuro.multiply(1.5); // "EUR 7.5"

        // Monetary amount can have a negative NumberValue
        MonetaryAmount minusTwoEuro = fiveEuro.subtract(sevenEuro); // "EUR -2"

        System.out.println("twelveEuro = " + twelveEuro);
        System.out.println("twoEuro = " + twoEuro);
        System.out.println("sevenPointFiveEuro = " + sevenPointFiveEuro);
        System.out.println("minusTwoEuro = " + minusTwoEuro);

        boolean greaterThan = sevenEuro.isGreaterThan(fiveEuro); // true
        boolean positive = sevenEuro.isPositive(); // true
        boolean zero = sevenEuro.isZero(); // false

        System.out.println("greaterThan = " + greaterThan);
        System.out.println("positive = " + positive);
        System.out.println("zero = " + zero);

        // fails, Currency mismatch: EUR/USD
        try {
            fiveEuro.add(tenUsDollar);
        } catch (MonetaryException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void monetaryAmountReductionFunctions() {
        List<MonetaryAmount> amounts = new ArrayList<>();
        amounts.add(Money.of(10, "EUR"));
        amounts.add(Money.of(7.5, "EUR"));
        amounts.add(Money.of(12, "EUR"));

        Optional<MonetaryAmount> max = amounts.stream().reduce(MonetaryFunctions.max()); // "EUR 7.5"
        Optional<MonetaryAmount> min = amounts.stream().reduce(MonetaryFunctions.min()); // "EUR 12"
        Optional<MonetaryAmount> sum = amounts.stream().reduce(MonetaryFunctions.sum()); // "EUR 29.5"

        System.out.println("min = " + min);
        System.out.println("max = " + max);
        System.out.println("sum = " + sum);
    }

    private static void monetaryAmountFilterFunctions() {
        List<MonetaryAmount> amounts = new ArrayList<>();
        amounts.add(Money.of(2, "EUR"));
        amounts.add(Money.of(42, "USD"));
        amounts.add(Money.of(7, "USD"));
        amounts.add(Money.of(13.37, "JPY"));
        amounts.add(Money.of(18, "USD"));

        CurrencyUnit yen = MonetaryCurrencies.getCurrency("JPY");
        CurrencyUnit dollar = MonetaryCurrencies.getCurrency("USD");

        // filter by currency, get only dollars
        // result is [USD 18, USD 7, USD 42]
        List<MonetaryAmount> onlyDollar = amounts.stream()
                .filter(MonetaryFunctions.isCurrency(dollar))
                .collect(Collectors.toList());

        // filter by currency, get only dollars and yen
        // [USD 18, USD 7, JPY 13.37, USD 42]
        List<MonetaryAmount> onlyDollarAndYen = amounts.stream()
                .filter(MonetaryFunctions.isCurrency(dollar, yen))
                .collect(Collectors.toList());

        MonetaryAmount tenDollar = Money.of(10, dollar);

        // [USD 42, USD 18]
        List<MonetaryAmount> greaterThanTenDollar = amounts.stream()
                .filter(MonetaryFunctions.isCurrency(dollar))
                .filter(MonetaryFunctions.isGreaterThan(tenDollar))
                .collect(Collectors.toList());

        // [USD 7, USD 18, USD 42]
        List<MonetaryAmount> sortedByAmount = onlyDollar.stream()
                .sorted(MonetaryFunctions.sortNumber())
                .collect(Collectors.toList());

        // [EUR 2, JPY 13.37, USD 42, USD 7, USD 18]
        List<MonetaryAmount> sortedByCurrencyUnit = amounts.stream()
                .sorted(MonetaryFunctions.sortCurrencyUnit())
                .collect(Collectors.toList());

        // {USD=[USD 42, USD 7, USD 18], EUR=[EUR 2], JPY=[JPY 13.37]}
        Map<CurrencyUnit, List<MonetaryAmount>> groupedByCurrency = amounts.stream()
                .collect(MonetaryFunctions.groupByCurrencyUnit());

        Map<CurrencyUnit, MonetarySummaryStatistics> summary = amounts.stream()
               .collect(MonetaryFunctions.groupBySummarizingMonetary()).get();

        MonetarySummaryStatistics dollarSummary = summary.get(dollar);
        MonetaryAmount average = dollarSummary.getAverage(); // "USD 22.333333333333333333.."
        MonetaryAmount min = dollarSummary.getMin(); // "USD 7"
        MonetaryAmount max = dollarSummary.getMax(); // "USD 42"
        MonetaryAmount sum = dollarSummary.getSum(); // "USD 67"
        long count = dollarSummary.getCount(); // 3

        System.out.println("average = " + average);
        System.out.println("min = " + min);
        System.out.println("max = " + max);
        System.out.println("sum = " + sum);
        System.out.println("count = " + count);

        System.out.println("onlyDollarAndYen = " + onlyDollarAndYen);
        System.out.println("onlyDollar = " + onlyDollar);
        System.out.println("greaterThanTenDollar = " + greaterThanTenDollar);
        System.out.println("sortedByAmount = " + sortedByAmount);
        System.out.println("sortedByCurrencyUnit = " + sortedByCurrencyUnit);

        System.out.println("groupedByCurrency = " + groupedByCurrency);
    }

    private static void monetaryAmountRounding() {
        CurrencyUnit usd = MonetaryCurrencies.getCurrency("USD");
        MonetaryAmount dollars = Money.of(12.34567, "USD");

        MonetaryOperator roundingOperator = MonetaryRoundings.getRounding(usd);
        MonetaryAmount roundedDollars = dollars.with(roundingOperator); // USD 12.35

        System.out.println("roundedDollars = " + roundedDollars);
    }

    private static void monetaryOperators() {
        MonetaryAmount dollars = Money.of(12.34567, "USD");

        MonetaryOperator tenPercentOperator = (MonetaryAmount amount) -> {
            BigDecimal baseAmount = amount.getNumber().numberValue(BigDecimal.class);
            BigDecimal tenPercent = baseAmount.multiply(new BigDecimal("0.1"));
            return Money.of(tenPercent, amount.getCurrency());
        };

        MonetaryAmount tenPercentDollars = dollars.with(tenPercentOperator); // USD 1.234567

        System.out.println("tenPercentDollars = " + tenPercentDollars);
    }

    private static void exchangeRates() {

        // get the default ExchangeRateProvider (CompoundRateProvider)
        ExchangeRateProvider exchangeRateProvider = MonetaryConversions.getExchangeRateProvider();

        // get a specific ExchangeRateProvider (here ECB)
        ExchangeRateProvider ecbExchangeRateProvider = MonetaryConversions.getExchangeRateProvider("ECB");

        // get the names of the default provider chain
        // [IDENT, ECB, IMF, ECB-HIST]
        List<String> defaultProviderChain = MonetaryConversions.getDefaultProviderChain();

        // get the exchange rate from euro to us dollar
        ExchangeRate rate = exchangeRateProvider.getExchangeRate("EUR", "USD");

        NumberValue factor = rate.getFactor(); // 1.2537 (at time writing)
        CurrencyUnit baseCurrency = rate.getBaseCurrency(); // EUR
        CurrencyUnit targetCurrency = rate.getCurrency(); // USD


        // convert an MonetaryAmount from euro to us dollar

        // get the CurrencyConversion from the default provider chain
        CurrencyConversion dollarConversion = MonetaryConversions.getConversion("USD");

        // get the CurrencyConversion from a specific provider
        CurrencyConversion ecbDollarConversion = ecbExchangeRateProvider.getCurrencyConversion("USD");


        MonetaryAmount tenEuro = Money.of(10, "EUR");

        // convert 10 euro to us dollar: USD 12.537 (at the time writing)
        MonetaryAmount inDollar = tenEuro.with(dollarConversion);

        System.out.println("factor = " + factor);
        System.out.println("baseCurrency = " + baseCurrency);
        System.out.println("targetCurrency = " + targetCurrency);
        System.out.println("inDollar = " + inDollar);
    }

    private static void formatting() {
        MonetaryAmountFormat germanFormat = MonetaryFormats.getAmountFormat(Locale.GERMANY);
        MonetaryAmountFormat usFormat = MonetaryFormats.getAmountFormat(Locale.CANADA);

        MonetaryAmount amount = Money.of(12345.67, MonetaryCurrencies.getCurrency("USD"));

        String usFormatted = usFormat.format(amount);
        String germanFormatted = germanFormat.format(amount);

        System.out.println("usFormatted = " + usFormatted);
        System.out.println("germanFormatted = " + germanFormatted);

        MonetaryAmount parsed = germanFormat.parse("12,4 USD");


        MonetaryAmountFormat customFormat = MonetaryFormats.getAmountFormat(
                AmountFormatQueryBuilder.of(Locale.US)
                        .set(CurrencyStyle.NAME)
                        .set("pattern", "00,00,00,00.00 ¤")
                        .build());

        // results in "00,01,23,45.67 US Dollar"
        String formatted = customFormat.format(amount);

        System.out.println("formatted: " + formatted);
        System.out.println("parsed = " + parsed);
    }


    public static void main(String[] args) {
        currencyUnit();
        monetaryAmount();
        monetaryAmountRounding();
        monetaryAmountOperations();
        monetaryAmountReductionFunctions();
        monetaryAmountFilterFunctions();
        monetaryOperators();
        exchangeRates();
        formatting();
    }
}
